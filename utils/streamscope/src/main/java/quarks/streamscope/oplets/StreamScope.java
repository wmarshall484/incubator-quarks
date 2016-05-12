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
package quarks.streamscope.oplets;

import quarks.function.Consumer;
import quarks.oplet.functional.Peek;

/**
 * A Stream "oscilloscope" oplet.
 * <P>
 * TODO remove this?  Just added to help the Console specialize its presentation
 * and/or so user can differentiate a StreamScope from any other random Peek oplet use.
 * </P>
 *
 * @param <T> Type of the tuple.
 */
public class StreamScope<T> extends Peek<T> {
  private static final long serialVersionUID = 1L;

  /**
   * Create a new instance.
   * @param streamScope the consumer function
   */
  public StreamScope(Consumer<T> streamScope) {
    super(streamScope);
  }
    
}
