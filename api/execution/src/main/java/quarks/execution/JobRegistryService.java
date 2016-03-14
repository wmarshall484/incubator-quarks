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
package quarks.execution;

import java.util.Set;

import quarks.function.BiConsumer;

/**
 * Contains the methods necessary for the registration and removal
 * of {@link Job} instances as well as access methods for registered jobs.
 * Allows clients to register listeners, which are notified on job 
 * registrations, removals, and updates.
 * <p>
 * Events are sent to registered listeners when jobs are added, removed, 
 * or their state changes:
 * <ul>
 * <li>An {@link EventType#ADD} event type is sent when a job is added.</li>
 * <li>An {@link EventType#REMOVE} event type is sent when a job is removed.</li>
 * <li>An {@link EventType#UPDATE} event type is sent when a job is updated.</li>
 * </ul>
 */
public interface JobRegistryService {
    /**
     * Job event types.
     */
    enum EventType {
        /** A Job has been added to the registry. */
        ADD,
        /** A Job has been removed from the registry. */
        REMOVE,
        /** A registered Job has been updated. */
        UPDATE
    }

    /**
     * Adds a handler to a collection of listeners that will be notified
     * on job registration and state changes.  Listeners will be notified 
     * in the order in which they are added.
     * <p>
     * A listener is notified of all existing jobs when it is first added.
     *
     * @param listener the listener that will be added
     */
    void addListener(BiConsumer<JobRegistryService.EventType, Job> listener);

    /**
     * Removes a handler from this registry's collection of listeners.
     *
     * @param listener the listener that will be removed
     */
    void removeListener(
            BiConsumer<JobRegistryService.EventType, Job> listener);

    /**
     * Returns a set of all the registered job identifiers.
     *
     * @return the identifiers of all the jobs
     */
    Set<String> getJobIds();

    /**
     * Returns a job given its identifier.
     *
     * @return the job or {@code null} if no job is registered with that 
     *      identifier.
     */
    Job getJob(String id);

    /**
     * Removes the job specified by the given identifier.
     *
     * @param jobId the identifier of the job to remove
     * @return whether or not the job was removed
     */
    boolean removeJob(String jobId);

    // TODO add job retrieval given its name
}
