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
package quarks.test.connectors.kafka;

import org.junit.Test;

/**
 * KafkaStreams connector globalization tests.
 */
public class KafkaStreamsGlobalTestManual extends KafkaStreamsTestManual {
    private final String globalMsg1 = "你好";
    private final String globalMsg2 = "你在嗎";

    @Test
    public void testGlobalSimple() throws Exception {
        super.testSimple(globalMsg1, globalMsg2);
    }

    @Test
    public void testGlobalWithKey() throws Exception {
        super.testWithKey(globalMsg1, globalMsg2);
    }

    @Test
    public void testGlobalPubSubBytes() throws Exception {
        super.testPubSubBytes(globalMsg1, globalMsg2);
    }

    @Test
    public void testGlobalMultiPub() throws Exception {
        super.testMultiPub(globalMsg1, globalMsg2);
    }

    @Test(expected=IllegalStateException.class)
    public void testGlobalMultiSubNeg() throws Exception {
        super.testMultiSubNeg(globalMsg1, globalMsg2);
    }

}
