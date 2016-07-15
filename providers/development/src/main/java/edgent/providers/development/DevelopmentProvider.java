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
package edgent.providers.development;

import java.util.Hashtable;
import java.util.concurrent.Future;

import com.codahale.metrics.MetricRegistry;
import com.google.gson.JsonObject;

import edgent.console.server.HttpServer;
import edgent.execution.Job;
import edgent.execution.services.ControlService;
import edgent.metrics.Metrics;
import edgent.metrics.MetricsSetup;
import edgent.metrics.oplets.CounterOp;
import edgent.providers.direct.DirectProvider;
import edgent.runtime.jmxcontrol.JMXControlService;
import edgent.streamscope.StreamScopeRegistry;
import edgent.streamscope.StreamScopeSetup;
import edgent.streamscope.mbeans.StreamScopeRegistryMXBean;
import edgent.topology.Topology;

/**
 * Provider intended for development.
 * This provider executes topologies using {@code DirectProvider}
 * and extends it by:
 * <UL>
 * <LI>
 * starting an embedded web-server providing the Edgent development console
 * that shows live graphs for running applications.
 * </LI>
 * <LI>
 * Creating a metrics registry with metrics registered
 * in the platform MBean server.
 * </LI>
 * <LI>
 * Add a {@link ControlService} that registers control management
 * beans in the platform MBean server.
 * </LI>
 * <LI>
 * Add tuple count metrics on all the streams before submitting a topology.
 * The implementation calls {@link Metrics#counter(Topology)} to insert 
 * {@link CounterOp} oplets into each stream.
 * </LI>
 * <LI>
 * Instrument the topology adding {@link edgent.streamscope.oplets.StreamScope StreamScope}
 * oplets on all the streams before submitting a topology.  
 * See {@link StreamScopeSetup#addStreamScopes(Topology) StreamScopeSetup.addStreamscopes}.
 * </LI>
 * <LI>
 * Add a {@link StreamScopeRegistry} runtime service and a
 * {@link StreamScopeRegistryMXBean} management bean to the {@code ControlService}.
 * See {@link StreamScopeSetup#register(edgent.execution.services.ServiceContainer) StreamScopeSetup.register}.
 * </LI>
 * </UL>
 * @see StreamScopeRegistry
 */
public class DevelopmentProvider extends DirectProvider {
    
    /**
     * JMX domains that this provider uses to register MBeans.
     * Set to {@value}.
     */
    public static final String JMX_DOMAIN = "edgent.providers.development";
    
    public DevelopmentProvider() throws Exception {
        
        MetricsSetup.withRegistry(getServices(), new MetricRegistry()).
                startJMXReporter(JMX_DOMAIN);
        
        getServices().addService(ControlService.class,
                new JMXControlService(JMX_DOMAIN, new Hashtable<>()));
        
        StreamScopeSetup.register(getServices());

        HttpServer server = HttpServer.getInstance();
        getServices().addService(HttpServer.class, server);   
        server.startServer();
    }

    @Override
    public Future<Job> submit(Topology topology, JsonObject config) {
        Metrics.counter(topology);
        StreamScopeSetup.addStreamScopes(topology);
        return super.submit(topology, config);
    }

}
