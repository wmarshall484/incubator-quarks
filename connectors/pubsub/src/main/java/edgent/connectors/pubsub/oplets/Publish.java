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
package edgent.connectors.pubsub.oplets;

import edgent.connectors.pubsub.service.PublishSubscribeService;
import edgent.oplet.OpletContext;
import edgent.oplet.core.Sink;

/**
 * Publish a stream to a PublishSubscribeService service.
 * If no such service exists then no tuples are published.
 *
 * @param <T> Type of the tuples.
 * 
 * @see edgent.connectors.pubsub.PublishSubscribe#publish(edgent.topology.TStream, String, Class)
 */
public class Publish<T> extends Sink<T> {
    
    private final String topic;
    private final Class<? super T> streamType;
    
    public Publish(String topic, Class<? super T> streamType) {
        this.topic = topic;
        this.streamType = streamType;
    }
    
    @Override
    public void initialize(OpletContext<T, Void> context) {
        super.initialize(context);
        
        PublishSubscribeService pubSub = context.getService(PublishSubscribeService.class);
        if (pubSub != null)
            setSinker(pubSub.getPublishDestination(topic, streamType));
    }
}
