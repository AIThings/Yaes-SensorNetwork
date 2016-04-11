package yaes.sensornetwork.scenarios.icc13energy;

import java.awt.geom.Rectangle2D;
import java.util.Random;

import yaes.framework.simulation.RandomVariable.Probe;
import yaes.sensornetwork.Environment;
import yaes.sensornetwork.SensorNetworkContext;
import yaes.sensornetwork.constSensorNetwork;
import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.applications.intrudertracking.IntruderNode;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingWorldModel.ItwmType;
import yaes.sensornetwork.identification.IntruderNodeType;
import yaes.sensornetwork.model.SensorNode;
import yaes.sensornetwork.model.stealth.StealthySensorNetworkWorld;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.PPMGenerator;
import yaes.world.physical.path.ProgrammedPathMovement;

public class IveContext extends SensorNetworkContext implements IveConstants {

    private InformationValue iv = new InformationValue();

    /**
	 * 
	 */
    private static final long serialVersionUID = -8007156202198428120L;

    /**
     * Generates the environment - currently defaults to the BPA, to be changed
     */
    @Override
    protected Environment createEnvironment() {
        // also use the opportunity to set the parameters for the delta's
        sop.update(Metrics_IVE_DeltaValueOfInformation, 0);
        sop.getRandomVar(Metrics_IVE_DeltaValueOfInformation)
                .enableTimeSeriesCollecting();
        sop.update(Metrics_IVE_VoIInstantRatio, 0);
        sop.getRandomVar(Metrics_IVE_VoIInstantRatio)
                .enableTimeSeriesCollecting();

        // create the environment
        Environment env = new Environment();
        env.setFullArea(new Rectangle2D.Double(0, 0, 1500, 1000));
        env.setSensorDistributionArea(new Rectangle2D.Double(200, 200, 1000,
                500));
        env.setInterestArea(new Rectangle2D.Double(300, 300, 800, 300));
        return env;
    }

    /**
     * Creates and arranges the intruder nodes, currently defaults to the BPA,
     * to be changed
     * 
     * @param sip
     */
    @Override
    protected void createIntruderNodes() {
        int intruderNodes =
                sip.getParameterInt(constSensorNetwork.Intruders_Number);
        for (int i = 1; i <= intruderNodes; i++) {
            addIntruder(new Random(i), i);
        }
    }

    /**
     * Adds a specific intruder
     * 
     * @param context
     * @param id
     */
    private void addIntruder(Random random, int id) {
        StealthySensorNetworkWorld world = getWorld();
        double speed = 1.0;
        double speedStdDev = 0.5;
        double waitTime = 5;
        double waitTimeStdDev = 10.0;
        double jumpMin = 300;
        double jumpMax = 1000;
        // this is a
        ProgrammedPathMovement ppm =
                PPMGenerator.randomWaypointSpecificJumps(environment.getFullArea(),
                        getSimulationInput().getStopTime(), speed, speedStdDev,
                        waitTime, waitTimeStdDev, random, jumpMin, jumpMax);
        IntruderNode mobileNode =
                new IntruderNode("Intruder-" + String.format("%03d", id), ppm,
                        world);
        mobileNode.setIntruderNodeType(IntruderNodeType.INTRUDER_HUMAN);
        world.addIntruderNode(mobileNode);
        world.addObject(mobileNode);
    }

    /**
     * Creates a sensor agent for the given static node - based on data from the
     * sip. Some of them have special stuff.
     * 
     * @param sip
     * @param staticNode
     * @return
     */
    @Override
    protected AbstractSensorAgent createSensorNodeAgent(SensorNode staticNode) {
        ItwmType type = null;
        IveTransmissionPolicy iveTransmissionPolicy =
                sip.getParameterEnum(IveTransmissionPolicy.class);
        double interval =
                sip.getParameterDouble(IveSensorNodeReasoner_Interval);
        double voiThreshold =
                sip.getParameterDouble(IveSensorNodeReasoner_VoiThreshold);
        switch (sip.getParameterEnum(IveSensorEstimation.class)) {
        case INERTIAL:
            type = ItwmType.INERTIAL;
            break;
        case LAST_KNOWN:
            type = ItwmType.LAST_KNOWN;
			break;
		default:
			break;
        }
        AbstractSensorAgent staticNodeAgent =
                new IveAgent(staticNode.getName(), sensorWorld, type,
                        iveTransmissionPolicy, interval, voiThreshold);
        staticNodeAgent
                .setTransmissionRange(sip
                        .getParameterDouble(constSensorNetwork.SensorDeployment_TransmissionRange));
        staticNodeAgent
                .setSensorRange(sip
                        .getParameterDouble(constSensorNetwork.SensorDeployment_SensorRange));
        return staticNodeAgent;
    }

    /**
     * Function for creating the sink node
     * 
     * This is the default implementation, creating a single sinkNode at the
     * specific locations given by the SensorDeployment_SinkNodeX parameters
     * 
     * If some of the scenarios require a different sink node, then they must
     * override this function
     * 
     * @return
     */
    @Override
    public void createSinkNode() {
        double sinkNodeX =
                sip.getParameterDouble(constSensorNetwork.SensorDeployment_SinkNodeX);
        double sinkNodeY =
                sip.getParameterDouble(constSensorNetwork.SensorDeployment_SinkNodeY);
        Location sinkNodeLocation = new Location(sinkNodeX, sinkNodeY);
        ItwmType type = null;
        switch (sip.getParameterEnum(IveSinkEstimation.class)) {
        case INERTIAL:
            type = ItwmType.INERTIAL;
            break;
        case LAST_KNOWN:
            type = ItwmType.LAST_KNOWN;
			break;
		default:
			break;
        }
        theSinkNode =
                IveSinkAgent.createSinkNode(SINK_NODE_NAME, sensorWorld, type,
                        transmissionRange, sinkNodeLocation,
                        environment.getSensorDistributionArea());
    }

    @Override
    public void updateGroundTruth() {
        iv.update(this);
        // TextUi.println(iv);
        double oldVoI =
                sop.getValue(Metrics_IVE_ValueOfInformation, Probe.LASTVALUE);
        double oldMaxVoI =
                sop.getValue(Metrics_IVE_ValueOfInformation, Probe.LASTVALUE);
        sop.update(Metrics_IVE_ValueOfInformation, iv.getSumInfoValue());
        sop.update(Metrics_IVE_MaxValueOfInformation, iv.getSumMaxInfoValue());
        // update the DELTA
        sop.update(Metrics_IVE_DeltaValueOfInformation, iv.getSumInfoValue()
                - oldVoI);
        sop.update(Metrics_IVE_DeltaMaxValueOfInformation,
                iv.getSumMaxInfoValue() - oldMaxVoI);
        double voiRatio =
                (iv.getSumInfoValue() - oldVoI)
                        / (iv.getSumMaxInfoValue() - oldMaxVoI);
        sop.update(Metrics_IVE_VoIInstantRatio, voiRatio);
        sop.update(Metrics_IVE_VoIRatio,
                iv.getSumInfoValue() / iv.getSumMaxInfoValue());
    }
}
