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
package quarks.runtime.etiao;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import quarks.execution.Job;
import quarks.execution.JobRegistryService;
import quarks.function.BiConsumer;

/**
 * Maintains a set of registered jobs and a set of listeners.
 * Notifies listeners on job additions, deletions and updates.
 */
public class JobRegistry implements JobRegistryService {
    private final ConcurrentMap<String /*JobId*/, Job> jobs;
    private final Broadcaster<JobRegistryService.EventType, Job> listeners;

    /**
     * Job event types.
     */
    public enum EventType {
        /** A Job has been added to the registry. */
        ADD,
        /** A Job has been removed from the registry. */
        REMOVE,
        /** A registered Job has been updated. */
        UPDATE
    }

    /**
     * Creates a new {@link JobRegistry}.
     */
    public JobRegistry() {
        this.jobs = new ConcurrentHashMap<String, Job>();
        this.listeners = new Broadcaster<JobRegistryService.EventType, Job>();
    }

    @Override
    public void addListener(BiConsumer<JobRegistryService.EventType, Job> listener) {
        listeners.add(listener);

        jobs.forEach((k, v) -> {
            listener.accept(JobRegistryService.EventType.ADD, v);
        });
    }

    @Override
    public void removeListener(BiConsumer<JobRegistryService.EventType, Job> listener) {
        listeners.remove(listener);
    }

    @Override
    public Set<String> getJobIds() {
        return Collections.unmodifiableSet(jobs.keySet());
    }

    @Override
    public Job getJob(String id) {
        return jobs.get(id);
    }

    /**
     * Adds the specified {@link Job} under the given name.
     *
     * @param job the job to register
     *
     * @throws IllegalArgumentException if a job with the same job name 
     * is already registered
     */
    void add(Job job) throws IllegalArgumentException {
        final Job existing = jobs.putIfAbsent(job.getId(), job);
        if (existing == null) {
            listeners.onEvent(JobRegistryService.EventType.ADD, job);
        } else {
            throw new IllegalArgumentException("A job with Id " + job.getId()
                + " already exists");
        }
    }

    @Override
    public boolean removeJob(String jobId) {
        final Job removed = jobs.remove(jobId);
        if (removed != null) {
            listeners.onEvent(JobRegistryService.EventType.REMOVE, removed);
            return true;
        }
        return false;
    }

    /**
     * Notifies listeners that the specified job has been updated.
     *
     * @param job the job
     */
    void update(Job job) {
        if (jobs.containsValue(job)) {
            listeners.onEvent(JobRegistryService.EventType.UPDATE, job);
        }
    }

    private static class Broadcaster<T, O> {
        private final List<BiConsumer<T, O>> listeners;

        Broadcaster() {
            this.listeners = new CopyOnWriteArrayList<BiConsumer<T, O>>();
        }

        void add(BiConsumer<T, O> listener) {
            listeners.add(listener);
        }

        void remove(BiConsumer<T, O> listener) {
            listeners.remove(listener);
        }

        private void onEvent(T event, O job) {
            for (BiConsumer<T, O> listener : listeners) {
                listener.accept(event, job);
            }
        }
    }
}
