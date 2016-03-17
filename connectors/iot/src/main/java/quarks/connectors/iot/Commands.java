package quarks.connectors.iot;

/**
 * Command identifiers used by Quarks.
 * 
 * @see IotDevice#RESERVED_ID_PREFIX
 */
public interface Commands {
    
    /**
     * Command identifier used for the control service.
     * <BR>
     * The command payload is used to invoke operations
     * against control MBeans using an instance of
     * {@link quarks.runtime.jsoncontrol.JsonControlService}.
     * <BR>
     * Value is {@value}.
     * 
     * @see quarks.execution.services.ControlService
     * @see quarks.providers.iot.IotProvider
     */
    String CONTROL_SERVICE = IotDevice.RESERVED_ID_PREFIX + "Control";

}
