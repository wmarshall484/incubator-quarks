/**
 * GPS analytics
 * <p>
 * Source is a stream of GPS sensor data {@link GpsSensor}
 * <p>
 * Here's an outline of the topology
 * <ul>
 * <li>Log GPS coordinates by publishing to IotF. The data may be used by a
 * server application to display the vehicle on a map.</li>
 * <li>Filter to detect speeds above a threshold and publish alert IotF</li>
 * <li>Filter for GPS coordinates that are outside of a defined Geofence
 * boundary</li>
 * <li>Windowing to detect hard driving: hard braking or hard acceleration and
 * publish alert to IotF</li>
 * </ul>
 * <p>
 */
package quarks.test.svt.apps;

import static quarks.analytics.math3.stat.Statistic.MAX;
import static quarks.analytics.math3.stat.Statistic.MEAN;
import static quarks.analytics.math3.stat.Statistic.MIN;
import static quarks.analytics.math3.stat.Statistic.STDDEV;

import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.math3.util.Pair;

import com.google.gson.JsonObject;

import quarks.connectors.iot.QoS;
import quarks.samples.apps.JsonTuples;
import quarks.samples.apps.Range;
import quarks.samples.apps.sensorAnalytics.SensorAnalyticsApplication;
import quarks.samples.utils.sensor.PeriodicRandomSensor;
import quarks.test.svt.utils.sensor.gps.GpsSensor;
import quarks.topology.TStream;
import quarks.topology.Topology;
import quarks.topology.plumbing.PlumbingStreams;

public class ObdAnalyticsApplication{

    private final FleetManagementAnalyticsClientApplication app;
    private final Topology t;
    private final String sensorId = "obd";

    public ObdAnalyticsApplication(Topology t, FleetManagementAnalyticsClientApplication app) {
        this.t = t;
        this.app = app;
    }
    
    /**
     * Add the GPS sensor's analytics to the topology.
     */
    public void addAnalytics() {

    }
}
