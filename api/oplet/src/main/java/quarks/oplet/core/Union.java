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
package quarks.oplet.core;

import java.util.Collections;
import java.util.List;

import quarks.function.Consumer;

/**
 * Union oplet, merges multiple input ports
 * into a single output port.
 * 
 * Processing for each input is identical
 * and just submits the tuple to the single output.
 */
public final class Union<T> extends AbstractOplet<T, T> {

    @Override
    public void start() {
    }

    /**
     * For each input set the output directly to the only output.
     */
    @Override
    public List<? extends Consumer<T>> getInputs() {
        Consumer<T> output = getOpletContext().getOutputs().get(0);
        return Collections.nCopies(getOpletContext().getInputCount(), output);
    }

    @Override
    public void close() {
    }
}
