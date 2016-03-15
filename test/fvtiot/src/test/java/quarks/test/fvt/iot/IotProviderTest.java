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

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.google.gson.JsonObject;

import quarks.apps.iot.IotDevicePubSub;
import quarks.connectors.iot.Commands;
import quarks.connectors.iot.IotDevice;
import quarks.connectors.pubsub.PublishSubscribe;
import quarks.providers.iot.IotProvider;
import quarks.test.apps.iot.EchoIotDevice;
import quarks.topology.TStream;
import quarks.topology.Topology;
import quarks.topology.services.ApplicationService;
import quarks.topology.tester.Condition;

/**
 * Test IotProvider using the EchoIotDevice.

 */
public class IotProviderTest {
    
    /**
     * Basic test we can start applications
     */
    @Test
    public void testIotProviderStartApplications() throws Exception {

        IotProvider provider = new IotProvider() {

            @Override
            protected IotDevice getMessageHubDevice(Topology topology) {
                return new EchoIotDevice(topology);
            }
        };
        
        assertSame(provider.getApplicationService(),
                provider.getServices().getService(ApplicationService.class));

        provider.start();

        IotTestApps.registerApplications(provider);

        // Create a Submit AppOne request
        JsonObject submitAppOne = IotAppServiceTest.newSubmitRequest("AppOne");
        
        // Create a test application that listens for the
        // output of AppOne (as a published topic).
        Topology checkAppOne = provider.newTopology();
        TStream<String> appOneOut = PublishSubscribe.subscribe(checkAppOne, "appOne", String.class);
        Condition<List<String>> appOnecontents = checkAppOne.getTester().streamContents(appOneOut, "APP1_A", "APP1_B", "APP1_C");
        
        // Run the test in the background as we need to start other apps
        // for it to complete.
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Boolean> appOneChecker = service.submit(() -> checkAppOne.getTester().complete(provider, new JsonObject(), appOnecontents, 5, TimeUnit.SECONDS));

        // Create an application that sends a device event
        // with the submit job command, and this will be echoed
        // back as the command that Quarks will detect and pick
        // up to start the application.
        Topology submitter = provider.newTopology();
        TStream<JsonObject> cmds = submitter.of(submitAppOne);
        IotDevice publishedDevice = IotDevicePubSub.addIotDevice(submitter);
        publishedDevice.events(cmds, Commands.CONTROL_SERVICE, 0);
        provider.submit(submitter).get();
        
        // Now AppOne is being submitted so wait for the
        // checker app to receive all the tuples
        // submitted by app one.
        
        appOneChecker.get();
        assertTrue(appOnecontents.getResult().toString(), appOnecontents.valid());
    }
}
