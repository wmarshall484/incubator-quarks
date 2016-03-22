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
package quarks.providers.direct;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import quarks.execution.Configs;
import quarks.execution.Job;
import quarks.execution.services.RuntimeServices;
import quarks.execution.services.ServiceContainer;
import quarks.function.Supplier;
import quarks.graph.Graph;
import quarks.runtime.etiao.EtiaoJob;
import quarks.runtime.etiao.Executable;
import quarks.runtime.etiao.graph.DirectGraph;
import quarks.topology.spi.graph.GraphTopology;

/**
 * {@code DirectTopology} is a {@link GraphTopology} that
 * is executed in threads in the current virtual machine.
 * <P> 
 * The topology is backed by a {@code DirectGraph} and its
 * execution is controlled and monitored by a {@code EtiaoJob}.
 * </P>
 */
public class DirectTopology extends GraphTopology<DirectTester> {

    private final DirectGraph eg;
    private final Executable executable;
    private final Job job;

    /**
     * Creates a {@code DirectTopology} instance.
     * 
     * @param name topology name
     * @param container container which provides runtime services
     */
    DirectTopology(String name, ServiceContainer container) {
        super(name);

        this.eg = new DirectGraph(name, container);
        this.executable = eg.executable();
        this.job = eg.job();
    }

    @Override
    public Graph graph() {
        return eg;
    }

    Executable getExecutable() {
        return executable;
    }

    Job getJob() {
        return job;
    }
    
    @Override
    public Supplier<RuntimeServices> getRuntimeServiceSupplier() {
        return () -> getExecutable();
    }

    @Override
    protected DirectTester newTester() {
        return new DirectTester(this);
    }

    Callable<Job> getCallable() {
        return new Callable<Job>() {

            @Override
            public Job call() throws Exception {
                execute();
                return getJob();
            }
        };
    }

    Future<Job> executeCallable(JsonObject config) {
        // TODO create the job at this time rather than in the Topology constructor
        // this removes the need for EtiaoJob.setName()
        
        JsonElement value = null;
        if (config != null) 
            value = config.get(Configs.JOB_NAME);
        if (value != null && !(value instanceof JsonNull))
            ((EtiaoJob)getJob()).setName(value.getAsString()); 
        return getExecutable().getScheduler().submit(getCallable());
    }

    private void execute() {
        getJob().stateChange(Job.Action.INITIALIZE);
        getJob().stateChange(Job.Action.START);
    }
}
