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
package quarks.test.fvt.iot;

import org.junit.Test;

import com.google.gson.JsonObject;

import quarks.apps.iot.IotDevicePubSub;
import quarks.connectors.iot.Commands;
import quarks.connectors.iot.IotDevice;
import quarks.providers.iot.IotProvider;
import quarks.test.apps.iot.EchoIotDevice;
import quarks.topology.TStream;
import quarks.topology.Topology;

public class IotProviderTest {
    
    @Test
    public void testIotProvider() throws Exception {
        
        IotProvider provider = new IotProvider() {

            @Override
            protected IotDevice getMessageHubDevice(Topology topology) {
                return new EchoIotDevice(topology);
            }};
        
            provider.start();
            
            provider.getApplicationService().registerTopology("AppOne", IotAppService::createApplicationOne);

            //
            JsonObject submitAppOne = IotAppService.newSubmitRequest("AppOne");
            
            
            Topology submitter = provider.newTopology();
            TStream<JsonObject> cmds = submitter.of(submitAppOne);
            IotDevice publishedDevice = IotDevicePubSub.addIotDevice(submitter);
            
            publishedDevice.events(cmds, Commands.CONTROL_SERVICE, 0);
            
            provider.submit(submitter).get();
    }
    
    
    
    public static void createApplicationOne(Topology topology, JsonObject config) {
        topology.strings("A", "B", "C").print();
    }
}
