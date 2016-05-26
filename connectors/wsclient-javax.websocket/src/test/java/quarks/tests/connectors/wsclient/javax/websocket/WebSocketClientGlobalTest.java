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
package quarks.tests.connectors.wsclient.javax.websocket;

import org.junit.Test;

/**
 * WebSocketClient connector globalization tests.
 */
public class WebSocketClientGlobalTest extends WebSocketClientTest {
    private final static String globalStr1 = "一";
    private final static String globalStr2 = "二";
    private final static String globalStr3 = "三三";
    private final static String globalStr4 = "四";

    @Test
    public void testGlobalBasicStaticStuff() {
        super.testBasicStaticStuff(globalStr1, globalStr2);
    }

    @Test(expected = IllegalStateException.class)
    public void testGlobalTooManySendersNeg() {
        super.testTooManySendersNeg(globalStr1, globalStr2);
    }

    @Test
    public void testGlobalJson() throws Exception {
        super.testJson(globalStr1, globalStr2);
    }

    @Test
    public void testGlobalString() throws Exception {
        super.testString(globalStr1, globalStr2);
    }

    @Test
    public void testGlobalBytes() throws Exception {
        super.testBytes(globalStr1, globalStr2);
    }

    @Test
    public void testGlobalReconnect() throws Exception {
        super.testReconnect(globalStr1, globalStr2, globalStr3, globalStr4);
    }

    @Test
    public void testGlobalReconnectBytes() throws Exception {
        super.testReconnectBytes(globalStr1, globalStr2, globalStr3, globalStr4);
    }

    @Test
    public void testGlobalSslClientAuthSystemProperty() throws Exception {
        super.testSslClientAuthSystemProperty(globalStr1, globalStr2);
    }

    @Test
    public void testGlobalSsl() throws Exception {
        super.testSsl(globalStr1, globalStr2);
    }

     @Test
     public void testGlobalSslReconnect() throws Exception {
         super.testSslReconnect(globalStr1, globalStr2, globalStr3, globalStr4);
     }

    @Test
    public void testGlobalSslNeg() throws Exception {
        super.testSslNeg(globalStr1, globalStr2);
    }

    @Test
    public void testGlobalSslClientAuth() throws Exception {
        super.testSslClientAuth(globalStr1, globalStr2);
    }

    @Test
    public void testGlobalSslClientAuthDefault() throws Exception {
        super.testSslClientAuthDefault(globalStr1, globalStr2);
    }

    @Test
    public void testGlobalSslClientAuthMy2ndCertNeg() throws Exception {
        super.testSslClientAuthMy2ndCertNeg(globalStr1, globalStr2);
    }

    @Test
    public void testGlobalSslClientAuthMy3rdCert() throws Exception {
        super.testSslClientAuthMy3rdCert(globalStr1, globalStr2);
    }

    @Test
    public void testGlobalSslClientAuthNeg() throws Exception {
        super.testSslClientAuthNeg(globalStr1, globalStr2);
    }

    @Test
    public void testGlobalPublicServer() throws Exception {
        super.testPublicServer(globalStr1, globalStr2);
    }

    @Test
    public void testGlobalSslPublicServer() throws Exception {
        super.testSslPublicServer(globalStr1, globalStr2);
    }

    public void testGlobalSslPublicServerBadTrustStoreSystemPropertyNeg() throws Exception {
        super.testSslPublicServerBadTrustStoreSystemPropertyNeg(globalStr1, globalStr2);
    }
}
