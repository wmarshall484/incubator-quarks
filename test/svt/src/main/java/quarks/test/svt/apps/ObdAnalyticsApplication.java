
package quarks.test.svt.apps;

import quarks.topology.Topology;
public class ObdAnalyticsApplication{

    private final FleetManagementAnalyticsClientApplication app;
    private final Topology t;
    private final String sensorId = "obd";

    public ObdAnalyticsApplication(Topology t, FleetManagementAnalyticsClientApplication app) {
        this.t = t;
        this.app = app;
    }
    
    /**
     * Add the ODB sensor's analytics to the topology.
     */
    public void addAnalytics() {

    }
}
