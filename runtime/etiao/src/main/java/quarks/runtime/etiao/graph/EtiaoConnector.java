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
package quarks.runtime.etiao.graph;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import quarks.graph.Connector;
import quarks.graph.Edge;
import quarks.graph.Vertex;
import quarks.graph.spi.DirectEdge;
import quarks.oplet.core.FanOut;
import quarks.oplet.core.Peek;

class EtiaoConnector<P> implements Connector<P> {

	/**
	 * The original port for this connector.
	 */
    private final ExecutableVertex<?, ?, P> originalVertex;
	@SuppressWarnings("unused")
    private final int originalPort;
	private String alias;

	/**
	 * The active port for this connector - where to add a peek or the 1st connect() target. 
	 * active is different from original when
	 * a peek has been inserted.  See commentary in peek().
	 */
	private ExecutableVertex<?, ?, P> activeVertex;
	private int activePort;

	/* The connector's (non-peek) target. When connected this refers to
	 * the 1st connect() target or a connect() injected FanOut.
	 * See commentary in peek().
	 */
	private Target<P> target;

	/**
	 * Fanout vertex. When the output port is logically connected to multiple
	 * inputs activeVertex will be connected to fanOutVertex and logical inputs
	 * are connected to fanOutVertex.
	 */
    private ExecutableVertex<FanOut<P>, P, P> fanOutVertex;

	public EtiaoConnector(ExecutableVertex<?, ?, P> originalVertex, int originalPort) {
		this.originalVertex = originalVertex;
		this.originalPort = originalPort;

		this.activeVertex = originalVertex;
		this.activePort = originalPort;

    }

    @Override
	public DirectGraph graph() {
        return activeVertex.graph();
    }

    @Override
    public boolean isConnected() {
    // See Connector.isConnected() doc for semantics
		return target != null;
    }

    private boolean isFanOut() {
        return fanOutVertex != null;
    }

	Target<P> disconnect() {
		assert isConnected();

		activeVertex.disconnect(activePort);
		Target<P> target = this.target;
		this.target = null;
		assert !isConnected();
		
		return target;
    }

	/**
	 * Take other's connection(s) leaving it disconnected.
	 */
	private void take(EtiaoConnector<P> other) {
		connectDirect(other.disconnect());
    }

	private void connectDirect(Target<P> target) {
		assert !isConnected();
		
        activeVertex.connect(activePort, target, newEdge(target));
		this.target = target;
	}

    @Override
    public void connect(Vertex<?, P, ?> target, int targetPort) {
		if (!isConnected()) {

			connectDirect(new Target<>((ExecutableVertex<?, P, ?>) target, targetPort));
			return;
        }

		if (!isFanOut()) {

            // Insert a FanOut oplet, initially with a single output port
			fanOutVertex = graph().insert(new FanOut<P>(), 1, 1);
            
            // Connect the FanOut's first port to the previous target
            EtiaoConnector<P> fanOutConnector = fanOutVertex.getConnectors().get(0);
            fanOutConnector.take(this);
            
			// Connect this to the FanOut oplet
			assert !isConnected();
            connect(fanOutVertex, 0);
        }

        // Add another output port to the fan out oplet.
        Connector<P> fanOutConnector = fanOutVertex.addOutput();
        fanOutConnector.connect(target, targetPort);
    }

    @Override
    public <N extends Peek<P>> Connector<P> peek(N oplet) {
        /*
			   * See Connector.peek() method/class doc for semantics.
				 * A peek is added at the activeVertex/port and
				 * then becomes the new activeVertex/port.
         * 
         * Adding a peek must not change whether or not the
         * "primary" connector "is connected" / has a target.
				 * The new peek does not become the "target".
         * 
         * The net is a peekConnector's target is always null
				 * and its activeVertex is always its originalVertex.
         */
        ExecutableVertex<N, P, P> peekVertex = graph().insert(oplet, 1, 1);

        // Have the output of the peek take over the connections of the current output.
        EtiaoConnector<P> peekConnector = peekVertex.getConnectors().get(0);

        if (isConnected()) {
            // N.B. "take" is designed for normal (non-peek) use - it fully
            // dissociates "other" from its connected target and then fully
            // associates the connector with the target.  Perfect when
            // doing something like injecting a FanOut.
            // 
            // However, for injecting a peek, peekConnector incorrectly
            // ends up inheriting "this"'s target and "this" is no longer
            // "connected" to the target as it should / must be.
          
            peekConnector.take(this);
            
            // put the target back the way it must be
            target = peekConnector.target;
            peekConnector.target = null;
        }
        
        // Connect to the new peek from our active vertex and
        // extend the activeVertex to be the new peek.
        //
        // DO NOT change this connector's target - it remains connected or not
        // independent of peeks.
        
        Target<P> target = new Target<P>(peekVertex, 0);
        activeVertex.connect(activePort, target, newEdge(target));

        activeVertex = peekVertex;
        activePort = 0;

        return this;
    }

	private Edge newEdge(Target<?> target) {
	    return new DirectEdge(this, activeVertex, activePort, target.vertex, target.port);
	}

    private Set<String> tags = new HashSet<>();

    @Override
    public void tag(String... values) {
        for (String v : values)
            tags.add(v);
    }

    @Override
    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    @Override
    public void alias(String alias) {
        if (this.alias != null)
            throw new IllegalStateException("alias already set");
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }
    
    /**
     * Intended only as a debug aid and content is not guaranteed. 
     */
    @Override
    public String toString() {
        return getClass().getSimpleName()
                + " activePort=" + activePort
                + " alias=" + getAlias()
                + " tags=" + getTags();
    }

}
