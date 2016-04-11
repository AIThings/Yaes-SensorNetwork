package yaes.sensornetwork.scenarios.icc13energy;

import yaes.sensornetwork.constSensorNetwork;

/**
 * Additional constants necessary for the information value - energy tradeoff
 * work
 * 
 * @author Lotzi Boloni
 * 
 */
public interface IveConstants extends constSensorNetwork {

    public static final String InternalMetrics_Ive_CountIntruderReported =
            "InternalMetrics_Ive_CountIntruderReported";
    public static final String InternalMetrics_Ive_CountOcclusion =
            "InternalMetrics_Ive_CountShadowing";
    public static final String InternalMetrics_Ive_CountSighted =
            "InternalMetrics_Ive_CountSighted";
    public static final String InternalMetrics_Ive_CountIntruderReportedNewInfo =
            "InternalMetrics_Ive_CountIntruderReportedNewInfo";

    /**
     * Metric to store the value of information (and its step by step delta)
     */
    public static final String Metrics_IVE_ValueOfInformation = "Metrics_IVE_ValueOfInformation";
    public static final String Metrics_IVE_DeltaValueOfInformation = "Metrics_IVE_DeltaValueOfInformation";

    /**
     * Metric to store the maximum value of information (and its step by step delta)
     */
    public static final String Metrics_IVE_MaxValueOfInformation = "Metrics_IVE_MaxValueOfInformation";
    public static final String Metrics_IVE_DeltaMaxValueOfInformation = "Metrics_IVE_DeltaMaxValueOfInformation";
    /**
     * The ratio between the achievable metrics and the maximum
     */
    public static final String Metrics_IVE_VoIInstantRatio = "Metrics_IVE_VoIInstantRatio";
    public static final String Metrics_IVE_VoIRatio = "Metrics_IVE_VoIRatio";
    
    /**
     * Decides the type of estimation used by the sensor nodes
     * @author Lotzi Boloni
     *
     */
    public enum IveSensorEstimation {
        INERTIAL, LAST_KNOWN;
    }
    
    public enum IveTransmissionPolicy {
        PERIODIC, VOI_THRESHOLD;
    }

    
    
    /**
     * Decides the type of estimation used by the sink node
     * @author Lotzi Boloni
     *
     */
    public enum IveSinkEstimation {
        INERTIAL, LAST_KNOWN;
    }

    /**
     * Decides whether the sensor node reasoner uses or not occlusion
     * @author Lotzi Boloni
     *
     */
    public enum IveSensorNodeReasoner_Occlusion {
        NO_OCCLUSION, USE_OCCLUSION
    }
    
    /**
     * The decision with regards of home much estimated value is sufficient to 
     * transmit
     */
    public static final String IveSensorNodeReasoner_VoiThreshold = 
            "IveSensorNodeReasoner_VoiThreshold";

    /**
     * The decision with regards of home much estimated value is sufficient to 
     * transmit
     */
    public static final String IveSensorNodeReasoner_Interval = 
            "IveSensorNodeReasoner_Interval";

}
