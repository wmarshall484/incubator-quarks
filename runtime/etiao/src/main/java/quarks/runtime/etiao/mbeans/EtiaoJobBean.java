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
package quarks.runtime.etiao.mbeans;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import quarks.execution.mbeans.JobMXBean;
import quarks.runtime.etiao.EtiaoJob;
import quarks.runtime.etiao.graph.model.GraphType;

/**
 * Implementation of a JMX control interface for the {@code EtiaoJob}.
 */
public class EtiaoJobBean implements JobMXBean {
    private final EtiaoJob job;
    public EtiaoJobBean(EtiaoJob job) {
        this.job = job;
    }

    @Override
    public String getId() {
        return job.getId();
    }

    @Override
    public String getName() {
        return job.getName();
    }

    @Override
    public State getCurrentState() {
        return State.fromString(job.getCurrentState().name());
    }

    @Override
    public State getNextState() {
        return State.fromString(job.getNextState().name());
    }

    @Override
    public String graphSnapshot() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(new GraphType(job.graph()));
    }

    @Override
    public Health getHealth() {
        return Health.fromString(job.getHealth().name());
    }

    @Override
    public String getLastError() {
        return job.getLastError();
    }
}
