# Quarks Java support

As documented in [DEVELOPMENT.md] Quarks development is performed
using Java 8 but Java 7 and Android is supported through use of
retrolambda to build specific jars.

Building a release `ant release` produces three sets of Jars under
* target/java8 - Java 8 SE
* target/java7 - Java 7 SE
* target/android - Android

This page documents which jars are expected to work in each environment.

## Core

| Jar | Java 8 | Java 7 SE | Android | Notes |
|---|---|---|---|---|
|quarks.api.execution.jar| yes | yes | yes |
|quarks.api.function.jar| yes | yes | yes |
|quarks.api.graph.jar| yes | yes | yes |
|quarks.api.oplet.jar| yes | yes | yes |
|quarks.api.topology.jar| yes | yes | yes |
|quarks.api.window.jar| yes | yes | yes |
|quarks.providers.development.jar | yes | | no | Uses JMX, For development only, not deployment |
|quarks.providers.direct.jar| yes | yes | yes |
|quarks.providers.iot.jar| yes | | |
|quarks.runtime.appservice.jar| yes | | |
|quarks.runtime.etiao.jar| yes | yes | yes |
|quarks.runtime.jmxcontrol.jar| yes | yes | no | Uses JMX |
|quarks.runtime.jobregistry.jar| yes | | |
|quarks.runtime.jsoncontrol.jar| yes | | |
|quarks.spi.graph.jar| yes | yes | yes |
|quarks.spi.topology.jar| yes | yes | yes |

## Connectors

| Jar | Java 8 SE | Java 7 SE | Android | Notes |
|---|---|---|---|---|
|quarks.connectors.common.jar | yes | yes | yes | |
|quarks.connectors.file.jar | yes | | | |
|quarks.connectors.http.jar | yes | yes | yes | |
|quarks.connectors.iotf.jar | yes | yes | yes | |
|quarks.connectors.iot.jar | yes | yes | yes | |
|quarks.connectors.jdbc.jar | yes | | | |
|quarks.connectors.kafka.jar | yes | | | |
|quarks.connectors.mqtt.jar | yes | | | |
|quarks.connectors.pubsub.jar | yes | | | |
|quarks.connectors.serial.jar | yes | | | |
|quarks.connectors.wsclient.jar | yes | | | |
|quarks.connectors.wsclient-javax.websocket.jar | yes | | | |
|quarks.javax.websocket.jar | yes | | | |

## Applications
| Jar | Java 8 SE | Java 7 SE | Android | Notes |
|---|---|---|---|---|
|quarks.apps.iot.jar | yes | | | | 
|quarks.apps.runtime.jar | yes | | | | 

### Analytics

| Jar | Java 8 SE | Java 7 SE | Android | Notes |
|---|---|---|---|---|
|quarks.analytics.math3.jar | yes | | | |
|quarks.analytics.sensors.jar | yes | | | |

### Utilities

| Jar | Java 8 SE | Java 7 SE | Android | Notes |
|---|---|---|---|---|
|quarks.utils.metrics.jar | yes | | | |

### Development Console

| Jar | Java 8 SE | Java 7 SE | Android | Notes |
|---|---|---|---|---|
|quarks.console.servlets.jar | yes | | no | Uses JMX, Servlet|
|quarks.console.server.jar | yes | | no | Uses JMX, Servlet |

### Android
| Jar | Java 8 SE | Java 7 SE | Android | Notes |
|---|---|---|---|---|
|quarks.android.topology.jar | no | no | yes | |
|quarks.android.hardware.jar | no | no | yes | |

## Java API Usage

| Feature | Packages | Quarks Usage | Notes |
|---|---|---|---|
|JMX | java.lang.management, javax.managment* | | JMX not supported on Android |
|JMX | | utils/metrics | Optional utility methods |
|JMX | | console/servlets, runtime/jmxcontrol | 

