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
package quarks.test.connectors.mqtt;

import org.junit.Test;

/**
 * MqttStreams globalization tests
 * <p>
 * The MQTT server is expected to be configured as follows:
 * <ul>
 * <li>serverURL "tcp://localhost:1883"</li>
 * <li>if the server is configured for authentication requiring
 * a username/pw, it is configured for userName="testUserName" and password="testUserNamePw"</li>
 * </ul>
 */
public class MqttStreamsGlobalTestManual extends MqttStreamsTestManual {

    private final String globalMsg1 = "你好";
    private final String globalMsg2 = "你在嗎";

    @Test
    public void testGlobalStringPublish() throws Exception {
        super.testStringPublish(globalMsg1, globalMsg2);
    }

    @Test
    public void testGlobalAutoClientId() throws Exception {
        super.testAutoClientId(globalMsg1, globalMsg2);
    }

    @Test
    public void testGlobalQoS1() throws Exception {
        super.testQoS1(globalMsg1, globalMsg2);
    }

    @Test
    public void testGlobalQoS2() throws Exception {
        super.testQoS2(globalMsg1, globalMsg2);
    }

    @Test
    public void testGlobalGenericPublish() throws Exception {
        super.testGenericPublish(globalMsg1, globalMsg2);
    }

    @Test
    public void testGlobalMultiConnector() throws Exception {
        super.testMultiConnector(globalMsg1, globalMsg2);
    }

    @Test
    public void testGlobalMultiTopicPublish() throws Exception {
        super.testMultiTopicPublish(globalMsg1, globalMsg2);
    }

    @Test
    public void testGlobalMultiTopicSubscribe() throws Exception {
        super.testMultiTopicSubscribe(globalMsg1, globalMsg2);
    }

    @Test
    public void testGlobalConnectFail() throws Exception {
        super.testConnectFail(globalMsg1, globalMsg2);
    }

    @Test
    public void testGlobalRetainedFalse() throws Exception {
        super.testRetainedFalse(globalMsg1, globalMsg2);
    }

    @Test
    public void testGlobalRetainedTrue() throws Exception {
        super.testRetainedTrue(globalMsg1, globalMsg2);
    }

    @Test
    public void testGlobalMultipleServerURL() throws Exception {
        super.testMultipleServerURL(globalMsg1, globalMsg2);
    }

    @Test
    public void testGlobalActionTime() throws Exception {
        super.testActionTime(globalMsg1, globalMsg2);
    }

    @Test
    public void testGlobalIdleSubscribe() throws Exception {
        super.testIdleSubscribe(globalMsg1, globalMsg2);
    }

    @Test
    public void testGlobalIdlePublish() throws Exception {
        super.testIdlePublish(globalMsg1, globalMsg2);
    }

    @Test
    public void testGlobalConnectRetryPub() throws Exception {
        super.testConnectRetryPub(globalMsg1, globalMsg2);
    }

    @Test
    public void testGlobalConnectRetrySub() throws Exception {
        super.testConnectRetrySub(globalMsg1, globalMsg2);
    }

    @Test
    public void testGlobalSubscribeFnThrow() throws Exception {
        super.testSubscribeFnThrow(globalMsg1, globalMsg2);
    }

    @Test
    public void testGlobalPublishFnThrow() throws Exception {
        super.testPublishFnThrow(globalMsg1, globalMsg2);
    }

    /*
     * See mqtt/src/test/keystores/README for info about SSL/TLS and mosquitto
     */

    @Test
    public void testGlobalSsl() throws Exception {
        super.testSsl(globalMsg1, globalMsg2);
    }

    @Test
    public void testGlobalSslClientAuth() throws Exception {
        super.testSslClientAuth(globalMsg1, globalMsg2);
    }

}
