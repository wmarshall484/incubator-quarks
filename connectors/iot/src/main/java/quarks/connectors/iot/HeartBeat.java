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
package quarks.connectors.iot;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonObject;

import quarks.function.Consumer;
import quarks.function.Functions;
import quarks.topology.TStream;
import quarks.topology.plumbing.PlumbingStreams;

public class HeartBeat {
  private HeartBeat() { };
  
  /**
   * Add IoT device heart beat processing to a topology.
   * <P>
   * An IoTDevice event containing heart beat information 
   * is periodically published to the specified {@code eventId}.
   * </P>
   * <P>
   * The heart beat provides clients of the IoT hub with liveness information
   * about the device and its connection to the hub.
   * </P>
   * <P>
   * The heart beat also ensures there is some immediate output so
   * the connection to the IoT hub happens as soon as possible.
   * In the case where there may not otherwise be
   * IoT events to publish, a heart beat ensures a connection
   * to the IoT hub is maintained.
   * </P>
   * <P>
   * The heart beat's event payload is the JSON for a JsonObject with the
   * heart beat's properties:
   * <ul>
   * <li>"when" : (string) {@link Date#toString()}</li>
   * <li>"time" : (number) {@link System#currentTimeMillis()}</li>
   * </ul> 
   * 
   * When {@code consumer} is non-null, {@code consumer.accept()} is called
   * with the {@code TStream<JsonObject>} created for heart beat events.
   * </P>
   * 
   * @param iotDevice IoT hub device
   * @param period the heart beat period
   * @param unit TimeUnit for the period
   * @param eventId the IotDevice eventId to use for the event
   * @param consumer 
   */
  public static void addHeartBeat(IotDevice iotDevice, long period, TimeUnit unit, String eventId, Consumer<TStream<JsonObject>> consumer) {
    TStream<Date> hb = iotDevice.topology().poll(
        () -> new Date(),
        period, unit).tag("heartbeat");
    // Convert to JSON
    TStream<JsonObject> hbj = hb.map(date -> {
        JsonObject j = new  JsonObject();
        j.addProperty("when", date.toString());
        j.addProperty("time", date.getTime());
        return j;
    }).tag("heartbeat");
    
    if (consumer != null) {
      consumer.accept(hbj);
    }
  
    // Tolerate connection outages.  Don't block upstream processing
    // and retain the most recent heartbeat if unable to publish.
    hbj = PlumbingStreams.pressureReliever(hbj, 
                Functions.unpartitioned(), 1).tag("pressureRelieved");
  
    iotDevice.events(hbj, eventId, QoS.FIRE_AND_FORGET);
  }
  
  /**
   * Add IoT device heart beat processing to a topology.
   * <P>
   * Same as {@link #addHeartBeat(IotDevice, long, TimeUnit, String, Consumer)}
   * with a null consumer parameter.
   * </P>
   * 
   * @param iotDevice IoT hub device
   * @param period the heart beat period
   * @param unit TimeUnit for the period
   * @param eventId the IotDevice eventId to use for the event
   */
  public static void addHeartBeat(IotDevice iotDevice, long period, TimeUnit unit, String eventId) {
    addHeartBeat(iotDevice, period, unit, eventId, null);
  }

}
