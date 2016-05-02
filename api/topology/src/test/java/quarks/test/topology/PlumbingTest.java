/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package quarks.test.topology;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.JsonObject;

import quarks.function.Function;
import quarks.function.Functions;
import quarks.topology.TStream;
import quarks.topology.Topology;
import quarks.topology.plumbing.PlumbingStreams;
import quarks.topology.plumbing.Valve;
import quarks.topology.tester.Condition;

@Ignore
public abstract class PlumbingTest extends TopologyAbstractTest {
	

	@Test
    public void testBlockingDelay() throws Exception {
		// Timing variances on shared machines can cause this test to fail
		assumeTrue(!Boolean.getBoolean("quarks.build.ci"));

        Topology topology = newTopology();
        
        TStream<String> strings = topology.strings("a", "b", "c", "d");
        
        TStream<Long> starts = strings.map(v -> System.currentTimeMillis());
        
        // delay stream
        starts = PlumbingStreams.blockingDelay(starts, 300, TimeUnit.MILLISECONDS);
        
        // calculate display
        starts = starts.modify(v -> System.currentTimeMillis() - v);
        
        starts = starts.filter(v -> v >= 300);
        
        Condition<Long> tc = topology.getTester().tupleCount(starts, 4);
        complete(topology, tc);
        assertTrue("valid:" + tc.getResult(), tc.valid());
    }

    @Test
    public void testBlockingThrottle() throws Exception {
		// Timing variances on shared machines can cause this test to fail
    	assumeTrue(!Boolean.getBoolean("quarks.build.ci"));

        Topology topology = newTopology();
        
        TStream<String> strings = topology.strings("a", "b", "c", "d");

        TStream<Long> emittedDelays = strings.map(v -> 0L);
        
        // throttle stream
        long[] lastEmittedTimestamp = { 0 };
        emittedDelays = PlumbingStreams.blockingThrottle(emittedDelays, 300, TimeUnit.MILLISECONDS)
                .map(t -> {
                    // compute the delay since the last emitted tuple
                    long now = System.currentTimeMillis();
                    if (lastEmittedTimestamp[0] == 0)
                        lastEmittedTimestamp[0] = now;
                    t = now - lastEmittedTimestamp[0];
                    lastEmittedTimestamp[0] = now;
                    // System.out.println("### "+t);
                    return t;
                    })
                .map(t -> {
                    // simulate 200ms downstream processing delay
                    try {
                        Thread.sleep(TimeUnit.MILLISECONDS.toMillis(200));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    } return t;
                    }) ;

        // should end up with throttled delays close to 300 (not 500 like
        // a blockingDelay() under these same conditions would yield)
        emittedDelays = emittedDelays.filter(v -> v <= 320);
        
        Condition<Long> tc = topology.getTester().tupleCount(emittedDelays, 4);
        complete(topology, tc);
        assertTrue("valid:" + tc.getResult(), tc.valid());
    }

    @Test
    public void testOneShotDelay() throws Exception {

        Topology topology = newTopology();
        
        TStream<String> strings = topology.strings("a", "b", "c", "d");
        
        TStream<Long> starts = strings.map(v -> System.currentTimeMillis());
        
        // delay stream
        starts = PlumbingStreams.blockingOneShotDelay(starts, 300, TimeUnit.MILLISECONDS);
        
        // calculate display
        starts = starts.modify(v -> System.currentTimeMillis() - v);
        
        // the first tuple shouldn't satisfy the predicate
        starts = starts.filter(v -> v < 300);
        
        Condition<Long> tc = topology.getTester().tupleCount(starts, 3);
        complete(topology, tc);
        assertTrue("valid:" + tc.getResult(), tc.valid());
    }

    public static class TimeAndId {
    	private static AtomicInteger ids = new AtomicInteger();
    	long ms;
    	final int id;
    	
    	public TimeAndId() {
    		this.ms = System.currentTimeMillis();
    		this.id = ids.incrementAndGet();
    	}
    	public TimeAndId(TimeAndId tai) {
    		this.ms = System.currentTimeMillis() - tai.ms;
    		this.id = tai.id;
    	}
    	@Override
    	public String toString() {
    		return "TAI:" + id + "@" + ms;
    	}
    	
    }
    
    @Test
    public void testPressureReliever() throws Exception {
		// Timing variances on shared machines can cause this test to fail
		assumeTrue(!Boolean.getBoolean("quarks.build.ci"));

        Topology topology = newTopology();
        
        TStream<TimeAndId> raw = topology.poll(() -> new TimeAndId(), 10, TimeUnit.MILLISECONDS);
           
        
        TStream<TimeAndId> pr = PlumbingStreams.pressureReliever(raw, Functions.unpartitioned(), 5);
        
        // insert a blocking delay acting as downstream operator that cannot keep up
        TStream<TimeAndId> slow = PlumbingStreams.blockingDelay(pr, 200, TimeUnit.MILLISECONDS);
        
        // calculate the delay
        TStream<TimeAndId> slowM = slow.modify(v -> new TimeAndId(v));
        
        // Also process raw that should be unaffected by the slow path
        TStream<String> processed = raw.asString();
        
        
        Condition<Long> tcSlowCount = topology.getTester().atLeastTupleCount(slow, 20);
        Condition<List<TimeAndId>> tcRaw = topology.getTester().streamContents(raw);
        Condition<List<TimeAndId>> tcSlow = topology.getTester().streamContents(slow);
        Condition<List<TimeAndId>> tcSlowM = topology.getTester().streamContents(slowM);
        Condition<List<String>> tcProcessed = topology.getTester().streamContents(processed);
        complete(topology, tcSlowCount);
        
        assertTrue(tcProcessed.getResult().size() > tcSlowM.getResult().size());
        for (TimeAndId delay : tcSlowM.getResult())
            assertTrue(delay.ms < 300);

        // Must not lose any tuples in the non relieving path
        Set<TimeAndId> uniq = new HashSet<>(tcRaw.getResult());
        assertEquals(tcRaw.getResult().size(), uniq.size());

        // Must not lose any tuples in the non relieving path
        Set<String> uniqProcessed = new HashSet<>(tcProcessed.getResult());
        assertEquals(tcProcessed.getResult().size(), uniqProcessed.size());
        
        assertEquals(uniq.size(), uniqProcessed.size());
           
        // Might lose tuples, but must not have send duplicates
        uniq = new HashSet<>(tcSlow.getResult());
        assertEquals(tcSlow.getResult().size(), uniq.size());
    }
    
    @Test
    public void testPressureRelieverWithInitialDelay() throws Exception {

        Topology topology = newTopology();
        
        
        TStream<String> raw = topology.strings("A", "B", "C", "D", "E", "F", "G", "H");
        
        TStream<String> pr = PlumbingStreams.pressureReliever(raw, v -> 0, 100);
        
        TStream<String> pr2 = PlumbingStreams.blockingOneShotDelay(pr, 5, TimeUnit.SECONDS);
        
        Condition<Long> tcCount = topology.getTester().tupleCount(pr2, 8);
        Condition<List<String>> contents = topology.getTester().streamContents(pr2, "A", "B", "C", "D", "E", "F", "G", "H");
        complete(topology, tcCount);
        
        assertTrue(tcCount.valid());
        assertTrue(contents.valid());
    }
    
    @Test
    public void testValveState() throws Exception {
        Valve<Integer> valve = new Valve<>();
        assertTrue(valve.isOpen());
        
        valve = new Valve<>(true);
        assertTrue(valve.isOpen());
        
        valve = new Valve<>(false);
        assertFalse(valve.isOpen());
        
        valve.setOpen(true);
        assertTrue(valve.isOpen());
        
        valve.setOpen(false);
        assertFalse(valve.isOpen());
    }
    
    @Test
    public void testValveInitiallyOpen() throws Exception {
        Topology top = newTopology("testValve");

        TStream<Integer> values = top.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        Valve<Integer> valve = new Valve<>();
        AtomicInteger cnt = new AtomicInteger();
        TStream<Integer> filtered = values
                                    .peek(tuple -> {
                                        // reject 4,5,6
                                        int curCnt = cnt.incrementAndGet();
                                        if (curCnt > 6)
                                            valve.setOpen(true);
                                        else if (curCnt > 3)
                                            valve.setOpen(false);
                                        })
                                    .filter(valve);

        Condition<Long> count = top.getTester().tupleCount(filtered, 7);
        Condition<List<Integer>> contents = top.getTester().streamContents(filtered, 1,2,3,7,8,9,10 );
        complete(top, count);
        assertTrue(contents.getResult().toString(), contents.valid());
    }
    
    @Test
    public void testValveInitiallyClosed() throws Exception {
        Topology top = newTopology("testValve");
        
        TStream<Integer> values = top.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        Valve<Integer> valve = new Valve<>(false);
        
        AtomicInteger cnt = new AtomicInteger();
        TStream<Integer> filtered = values
                                    .peek(tuple -> {
                                        // reject all but 4,5,6
                                        int curCnt = cnt.incrementAndGet();
                                        if (curCnt > 6)
                                            valve.setOpen(false);
                                        else if (curCnt > 3)
                                            valve.setOpen(true);
                                        })
                                    .filter(valve);

        Condition<Long> count = top.getTester().tupleCount(filtered, 3);
        Condition<List<Integer>> contents = top.getTester().streamContents(filtered, 4,5,6 );
        complete(top, count);
        assertTrue(contents.getResult().toString(), contents.valid());
    }
    
    private Function<Integer,JsonObject> fakeAnalytic(int channel, long period, TimeUnit unit) {
      return value -> { 
        try {
          Thread.sleep(unit.toMillis(period));
          JsonObject jo = new JsonObject();
          jo.addProperty("channel", channel);
          jo.addProperty("result", value);
          return jo;
        } catch (InterruptedException e) {
          throw new RuntimeException("channel="+channel+" interrupted", e);
        }
      };
    }

    private Function<TStream<Integer>,TStream<JsonObject>> fakePipeline(int channel, long period, TimeUnit unit) {
      return stream -> stream.map(fakeAnalytic(channel, period, unit)).filter(t->true).tag("pipeline-ch"+channel);
    }
    
    @Test
    public void testConcurrentMap() throws Exception {
        Topology top = newTopology("testConcurrentMap");
        
        int ch = 0;
        List<Function<Integer,JsonObject>> mappers = new ArrayList<>();
        mappers.add(fakeAnalytic(ch++, 100, TimeUnit.MILLISECONDS));
        mappers.add(fakeAnalytic(ch++, 100, TimeUnit.MILLISECONDS));
        mappers.add(fakeAnalytic(ch++, 100, TimeUnit.MILLISECONDS));
        mappers.add(fakeAnalytic(ch++, 100, TimeUnit.MILLISECONDS));
        mappers.add(fakeAnalytic(ch++, 100, TimeUnit.MILLISECONDS));
        mappers.add(fakeAnalytic(ch++, 100, TimeUnit.MILLISECONDS));
        // a couple much faster just in case something's amiss with queues
        mappers.add(fakeAnalytic(ch++, 3, TimeUnit.MILLISECONDS));
        mappers.add(fakeAnalytic(ch++, 13, TimeUnit.MILLISECONDS));
        
        Function<List<JsonObject>,Integer> combiner = list -> {
            int sum = 0;
            int cnt = 0;
            System.out.println("combiner: "+list);
            for(JsonObject jo : list) {
              assertEquals(cnt++, jo.getAsJsonPrimitive("channel").getAsInt());
              sum += jo.getAsJsonPrimitive("result").getAsInt();
            }
            return sum;
        };

        TStream<Integer> values = top.of(1, 2, 3);
        Integer[] resultTuples = new Integer[]{
            1*mappers.size(),
            2*mappers.size(),
            3*mappers.size(),
        };
        
        TStream<Integer> result = PlumbingStreams.concurrentMap(values, mappers, combiner);
        
        Condition<Long> count = top.getTester().tupleCount(result, 3);
        Condition<List<Integer>> contents = top.getTester().streamContents(result, resultTuples );
        long begin = System.currentTimeMillis();
        complete(top, count);
        long end = System.currentTimeMillis();
        assertTrue(contents.getResult().toString(), contents.valid());
        
        long actDuration = end - begin;
        
        long expMinSerialDuration = resultTuples.length * mappers.size() * 100;
        long expMaxDuration = resultTuples.length * (100 + 75/*slop and more tuple overhead*/);
        
        System.out.println("expMaxDuration="+expMaxDuration+" actDuration="+actDuration+" expMinSerialDuration="+expMinSerialDuration);
        
        // a gross level performance check
        assertTrue("expMinSerialDuration="+expMinSerialDuration+" actDuration="+actDuration, 
            actDuration < 0.5 * expMinSerialDuration);
        
        // a tighter performance check
        assertTrue("expMaxDuration="+expMaxDuration+" actDuration="+actDuration, 
            actDuration <= expMaxDuration);
    }
    
    @Test
    public void testConcurrent() throws Exception {
        Topology top = newTopology("testConcurrent");
        
        int ch = 0;
        List<Function<TStream<Integer>,TStream<JsonObject>>> pipelines = new ArrayList<>();
        pipelines.add(fakePipeline(ch++, 100, TimeUnit.MILLISECONDS));
        pipelines.add(fakePipeline(ch++, 100, TimeUnit.MILLISECONDS));
        pipelines.add(fakePipeline(ch++, 100, TimeUnit.MILLISECONDS));
        pipelines.add(fakePipeline(ch++, 100, TimeUnit.MILLISECONDS));
        pipelines.add(fakePipeline(ch++, 100, TimeUnit.MILLISECONDS));
        
        Function<List<JsonObject>,Integer> combiner = list -> {
            int sum = 0;
            int cnt = 0;
            System.out.println("combiner: "+list);
            for(JsonObject jo : list) {
              assertEquals(cnt++, jo.getAsJsonPrimitive("channel").getAsInt());
              sum += jo.getAsJsonPrimitive("result").getAsInt();
            }
            return sum;
        };
        
        TStream<Integer> values = top.of(1, 2, 3);
        Integer[] resultTuples = new Integer[]{
            1*pipelines.size(),
            2*pipelines.size(),
            3*pipelines.size(),
        };
        
        TStream<Integer> result = PlumbingStreams.concurrent(values, pipelines, combiner).tag("result");
        
        Condition<Long> count = top.getTester().tupleCount(result, 3);
        Condition<List<Integer>> contents = top.getTester().streamContents(result, resultTuples );
        long begin = System.currentTimeMillis();
        complete(top, count);
        long end = System.currentTimeMillis();
        assertTrue(contents.getResult().toString(), contents.valid());
        
        long actDuration = end - begin;
        
        long expMinSerialDuration = resultTuples.length * pipelines.size() * 100;
        long expMaxDuration = resultTuples.length * (100 + 75/*slop and more tuple overhead*/);
        
        System.out.println("expMaxDuration="+expMaxDuration+" actDuration="+actDuration+" expMinSerialDuration="+expMinSerialDuration);
        
        // a gross level performance check
        assertTrue("expMinSerialDuration="+expMinSerialDuration+" actDuration="+actDuration, 
            actDuration < 0.5 * expMinSerialDuration);
        
        // a tighter performance check
        assertTrue("expMaxDuration="+expMaxDuration+" actDuration="+actDuration, 
            actDuration <= expMaxDuration);
    }

}
