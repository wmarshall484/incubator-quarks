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
package quarks.streamscope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A registry for Stream "oscilloscope" {@link StreamScope} instances.
 * <P>
 * The registry contains a collection of StreamScope instances
 * that are registered by one or more names.
 * </P><P>
 * The names are: by a TStream {@link quarks.topology.TStream#alias(String) alias} or
 * by a stream's (output port's) unique identifier.
 * Static methods are provided for composing these names and extracting
 * the alias/identifier from generated names.
 * </P>
 * @see quarks.providers.development.DevelopmentProvider DevelopmentProvider
 */
public class StreamScopeRegistry {
  private final Map<String, StreamScope<?>> byNameMap = new HashMap<>();
  private final Map<StreamScope<?>, List<String>> byStreamScopeMap = new HashMap<>();

  public StreamScopeRegistry() {
    
  }
  
  /** create a registration name for a stream alias */
  public static String nameByStreamAlias(String alias) {
    Objects.requireNonNull(alias, "alias");
    return "alias."+alias;
  }
  
  /** create a registration name for a stream id */
  public static String nameByStreamId(String id) {
    Objects.requireNonNull(id, "id");
    return "id."+id;
  }
  
  /** returns null if {@code name} is not a from nameByStreamAlias() */
  public static String streamAliasFromName(String name) {
    Objects.requireNonNull(name, "name");
    if (!name.startsWith("alias."))
      return null;
    return name.substring("alias.".length());
  }
  
  /** returns null if {@code name} is not a from nameByStreamId() */
  public static String streamIdFromName(String name) {
    Objects.requireNonNull(name, "name");
    if (!name.startsWith("id."))
      return null;
    return name.substring("id.".length());
  }
  
  /** A single StreamScope can be registered with multiple names.
   * @throws IllegalStateException if a registration already exists for {@code name}
   */
  public synchronized void register(String name, StreamScope<?> streamScope) {
    if (byNameMap.containsKey(name))
      throw new IllegalStateException("StreamScope already registered by name "+name);
    byNameMap.put(name, streamScope);
    List<String> names = byStreamScopeMap.get(streamScope); 
    if (names == null) {
      names = new ArrayList<>(2);
      byStreamScopeMap.put(streamScope, names);
    }
    names.add(name);
  }
  
  /**
   * @param name
   * @return the StreamScope. null if name is not registered.
   */
  public synchronized StreamScope<?> lookup(String name) {
    return byNameMap.get(name);
  }
  
  /**
   * Get the registered names.
   * @return unmodifiable collection of the name.
   * The set is backed by the registry so the set may change.
   */
  public synchronized Set<String> getNames() {
    return Collections.unmodifiableSet(byNameMap.keySet());
  }
  
  /** Get registered StreamScopes and the name(s) each is registered with.
   * The map is backed by the registry so its contents may change.
   */
  public synchronized Map<StreamScope<?>, List<String>> getStreamScopes() {
    return Collections.unmodifiableMap(byStreamScopeMap);
  }
  
  /** remove the specific name registration.  Other registration of the same StreamScope may still exist.
   * no-op if name is not registered.
   * @see #unregister(StreamScope)
   */
  public synchronized void unregister(String name) {
    StreamScope<?> streamScope = byNameMap.get(name);
    if (streamScope == null)
      return;
    byNameMap.remove(name);
    List<String> names = byStreamScopeMap.get(streamScope);
    names.remove(name);
    if (names.isEmpty())
      byStreamScopeMap.remove(streamScope);
  }
  
  /** remove all name registrations of the StreamScope.
   * no-op if no registrations for the StreamScope
   */
  public synchronized void unregister(StreamScope<?> streamScope) {
    List<String> names = byStreamScopeMap.get(streamScope);
    if (names == null)
      return;
    names = new ArrayList<>(names);
    for (String name : names)
      unregister(name);
  }
  
}

