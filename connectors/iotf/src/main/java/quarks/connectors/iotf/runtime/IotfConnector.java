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

package quarks.connectors.iotf.runtime;

import java.io.File;
import java.io.Serializable;
import java.util.Properties;

import com.google.gson.JsonObject;
import com.ibm.iotf.client.device.Command;
import com.ibm.iotf.client.device.DeviceClient;

import quarks.function.Consumer;

/**
 * Device connector for IoTf.
 */
public class IotfConnector implements Serializable, AutoCloseable {
    private static final long serialVersionUID = 1L;

    private Properties options;
    private File optionsFile;
    private transient DeviceClient client;

    /**
     * Create a new connector to the specified MQTT server.
     *
     * @param options connector options
     */
    public IotfConnector(Properties options) {
        this.options = options;
    }

    public IotfConnector(File optionsFile) {
        this.optionsFile = optionsFile;
    }

    synchronized DeviceClient connect() throws Exception {
        DeviceClient client = getClient();
        if (!client.isConnected())
            client.connect();
        return client;
    }

    synchronized DeviceClient getClient() throws Exception {
        if (client == null) {
            if (options == null)
                options = DeviceClient.parsePropertiesFile(optionsFile);

            client = new DeviceClient(options);
        }
        return client;
    }

    synchronized void subscribeCommands(Consumer<Command> tupleSubmitter) throws Exception {
        DeviceClient client = getClient();
        
        client.setCommandCallback(cmd -> {
            tupleSubmitter.accept(cmd);
        });
    }

    void publishEvent(String eventId, JsonObject event, int qos) {
        DeviceClient client;
        try {
            client = connect();
        } catch (Error err) {
            throw err;

        } catch (RuntimeException re) {
            throw re;

        } catch (Exception e) {
            throw new RuntimeException(e);

        }
        client.publishEvent(eventId, event, qos);
    }

    @Override
    public void close() throws Exception {
        if (client == null)
            return;

        client.disconnect();
        client = null;
    }
}
