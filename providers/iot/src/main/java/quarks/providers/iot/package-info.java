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

/**
 * Iot provider that allows multiple applications to
 * share an {@code IotDevice}.
 * 
 * <H3>IoT device</H3>
 * 
 * <H3>Application registration</H3>
 * The provider includes an {@link quarks.topology.services.ApplicationService ApplicationService} that allows applications
 * to be registered by name. Once registered an application can be started (and stopped) remotely
 * through the control service using a device command.
 * 
 * <H3>Supported device commands</H3>
 * This provider supports a number of system level device commands to control the applications
 * running within it.
 * <H4>Control service</H4>
 * Device commands with the command identifier '{@link quarks.connectors.iot.Commands#CONTROL_SERVICE quarksControl}'
 * are sent to the provider's control service, an instance of {@link quarks.runtime.jsoncontrol.JsonControlService JsonControlService}.
 * This allows invocation of an operation against a control mbean registered with the
 * control service, either by an application or the provider itself.
 * <BR>
 * The command's data (JSON) uniquely identifies a control MBean through its type and
 * alias, and indicates the operation to call on the MBean and the arguments to
 * pass the control MBean.
 * <BR>
 * Thus any control operation can be remotely invoked through a {@code quarksControl} device command,
 * including arbitrary control mbeans registered by applications.
 * 
 * <H4 id="providerOps">Provider operations</H4>
 * <TABLE border="1" aria-describedby="providerOps">
 * <tr>
 *     <th id="operation">Operation</th><th id="cmdIdentifier">Command identifier</th>
 *     <th id="type">Type</th><th id="alias">Alias</th><th id="method">Method</th><th id="args">Arguments</th>
 *     <th id="controlMbean">Control MBean</th>
 * </tr>
 * <tr>
 *    <td rowspan="2" headers="operation"><strong>Submit (start) a registered application</strong></td>
 *    <td headers="cmdIdentifier">{@code quarksControl}</td><td headers="type">{@code appService}</td>
 *    <td headers="alias">{@code quarks}</td><td headers="method">{@link quarks.topology.mbeans.ApplicationServiceMXBean#submit(String, String) submit}</td>
 *    <td headers="args"><em>{@code [applicationName, config]}</em></td>
 *    <td headers="controlMbean">{@link quarks.topology.mbeans.ApplicationServiceMXBean ApplicationServiceMXBean}</td>
 * </tr>
 * <tr>
 *    <td headers="cmdIdentifier"><strong>Sample command data</strong></td>
 *    <td colspan=5 headers="type alias method args controlMbean">{@code {"type":"appService","alias":"quarks","op":"submit","args":["Heartbeat",{}]}}</td>
 * </tr>
 * <tr></tr>
 * 
 * <tr>
 *    <td rowspan="2" headers="operation"><strong>Close (stop) a running registered application</strong></td>
 *    <td headers="cmdIdentifier">{@code quarksControl}</td><td headers="type">{@link quarks.execution.mbeans.JobMXBean#TYPE job}</td>
 *    <td headers="type"><em>{@code applicationName}</em></td><td headers="method">{@link quarks.execution.mbeans.JobMXBean#stateChange(quarks.execution.Job.Action) stateChange}</td>
 *    <td headers="args">{@code ["CLOSE"]}</td>
 *    <td headers="controlMbean">{@link quarks.execution.mbeans.JobMXBean JobMXBean}</td>
 * </tr>
 * <tr>
 *    <td headers="cmdIdentifier"><strong>Sample command data</strong></td>
 *    <td colspan=5 headers="type alias method args controlMbean">{@code {"type":"job","alias":"Heartbeat","op":"stateChange","args":["CLOSE"]}}</td>
 * </tr>
 * <tr></tr>
 * </TABLE>
 * 
 */
package quarks.providers.iot;
