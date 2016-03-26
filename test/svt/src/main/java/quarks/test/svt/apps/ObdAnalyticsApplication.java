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
