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
package quarks.test.analytics.sensors;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import quarks.analytics.sensors.Valve;
import quarks.analytics.sensors.Valve.State;
import quarks.test.providers.direct.DirectTestSetup;
import quarks.test.topology.TopologyAbstractTest;
import quarks.topology.TStream;
import quarks.topology.Topology;
import quarks.topology.tester.Condition;

public class ValveTest extends TopologyAbstractTest implements DirectTestSetup {
    
	@Test
	public void testState() throws Exception {
	    Valve<Integer> valve = new Valve<>();
	    assertSame(State.OPEN, valve.getState());
	    
	    valve.setState(State.CLOSED);
        assertSame(State.CLOSED, valve.getState());
        
        valve.setState(State.OPEN);
        assertSame(State.OPEN, valve.getState());
        
        valve = new Valve<>(State.OPEN);
        assertSame(State.OPEN, valve.getState());
        
        valve = new Valve<>(State.CLOSED);
        assertSame(State.CLOSED, valve.getState());
        
	}
    
    @Test
    public void testInitiallyOpen() throws Exception {
        Topology top = newTopology("testValve");

        TStream<Integer> values = top.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        Valve<Integer> valve = new Valve<>();
        AtomicInteger cnt = new AtomicInteger();
        TStream<Integer> filtered = values
                                    .peek(tuple -> {
                                        // reject 4,5,6
                                        int curCnt = cnt.incrementAndGet();
                                        if (curCnt > 6)
                                            valve.setState(State.OPEN);
                                        else if (curCnt > 3)
                                            valve.setState(State.CLOSED);
                                        })
                                    .filter(valve);

        Condition<Long> count = top.getTester().tupleCount(filtered, 7);
        Condition<List<Integer>> contents = top.getTester().streamContents(filtered, 1,2,3,7,8,9,10 );
        complete(top, count);
        assertTrue(contents.getResult().toString(), contents.valid());
    }
    
    @Test
    public void testInitiallyClosed() throws Exception {
        Topology top = newTopology("testValve");
        
        TStream<Integer> values = top.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        Valve<Integer> valve = new Valve<>(State.CLOSED);
        
        AtomicInteger cnt = new AtomicInteger();
        TStream<Integer> filtered = values
                                    .peek(tuple -> {
                                        // reject all but 4,5,6
                                        int curCnt = cnt.incrementAndGet();
                                        if (curCnt > 6)
                                            valve.setState(State.CLOSED);
                                        else if (curCnt > 3)
                                            valve.setState(State.OPEN);
                                        })
                                    .filter(valve);

        Condition<Long> count = top.getTester().tupleCount(filtered, 3);
        Condition<List<Integer>> contents = top.getTester().streamContents(filtered, 4,5,6 );
        complete(top, count);
        assertTrue(contents.getResult().toString(), contents.valid());
    }
}
