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
package quarks.test.connectors.file;

import org.junit.Test;

/**
 * FileStreamsTextFileWriter connector globalization tests.
 */
public class FileStreamsTextFileWriterGlobalTest extends FileStreamsTextFileWriterTest {

    String globalStr = "一二三四五六七八九";
    String[] globalLines = new String[] {
            "1-"+globalStr,
            "2-"+globalStr,
            "3-"+globalStr,
            "4-"+globalStr
    };

    @Test
    public void testGlobalOneFileCreated() throws Exception {
        super.testOneFileCreated(globalLines);
    }

    @Test
    public void testGlobalManyFiles() throws Exception {
        super.testManyFiles(globalLines);
    }

    @Test
    public void testGlobalManyFilesSlow() throws Exception {
        super.testManyFilesSlow(globalLines);
    }

    @Test
    public void testGlobalRetainCntBased() throws Exception {
        super.testRetainCntBased(globalLines);
    }

    @Test
    public void testGlobalRetainAggSizeBased() throws Exception {
        super.testRetainAggSizeBased(globalLines, globalStr);
    }

    @Test
    public void testGlobalRetainAgeBased() throws Exception {
        super.testRetainAgeBased(globalLines);
    }

    @Test
    public void testGlobalFlushImplicit() throws Exception {
        super.testFlushImplicit(globalLines);
    }

    @Test
    public void testGlobalFlushCntBased() throws Exception {
        super.testFlushCntBased(globalLines);
    }

    @Test
    public void testGlobalFlushTimeBased() throws Exception {
        super.testFlushTimeBased(globalLines);
    }

    @Test
    public void testGlobalFlushTupleBased() throws Exception {
        super.testFlushTupleBased(globalLines);
    }

    @Test
    public void testGlobalCycleCntBased() throws Exception {
        super.testCycleCntBased(globalLines);
    }

    @Test
    public void testGlobalCycleSizeBased() throws Exception {
        super.testCycleSizeBased(globalLines);
    }

    @Test
    public void testGlobalCycleTimeBased() throws Exception {
        super.testCycleTimeBased(globalLines);
    }

    @Test
    public void testGlobalCycleTupleBased() throws Exception {
        super.testCycleTupleBased(globalLines);
    }

    @Test
    public void testGlobalAllTimeBased() throws Exception {
        super.testAllTimeBased(globalLines);
    }

    @Test
    public void testGlobalWriterWatcherReader() throws Exception {
        super.testWriterWatcherReader(globalLines);
    }

}
