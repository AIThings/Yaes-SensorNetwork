/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Aug 1, 2010
 
   storeanddump.KnowledgeHistory
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import yaes.framework.simulation.AbstractContext;
import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.SimulationOutput;
import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.agents.KnowledgeBasedSensorAgent;
import yaes.sensornetwork.agents.SDIntruderTrackingSinkAgent;
import yaes.sensornetwork.agents.SensorRoutingHelper;
import yaes.sensornetwork.agents.directeddiffusion.DDSinkAgent;
import yaes.sensornetwork.agents.directeddiffusion.DirectedDiffusionAgent;
import yaes.sensornetwork.agents.directeddiffusion.DirectedDiffusionAgentFactory;
import yaes.sensornetwork.applications.intrudertracking.IntruderGenerator;
import yaes.sensornetwork.applications.intrudertracking.IntruderNode;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingSimpleSensorAgent;
import yaes.sensornetwork.energymodel.RapaportCommunicationEnergyModel;
import yaes.sensornetwork.knowledge.IntruderSighting;
import yaes.sensornetwork.knowledge.IntruderSightingHistory;
import yaes.sensornetwork.knowledge.SimpleEstimator;
import yaes.sensornetwork.knowledge.TrackingError;
import yaes.sensornetwork.model.SensorNode;
import yaes.sensornetwork.model.SinkNode;
import yaes.sensornetwork.model.stealth.StealthySensorNetworkWorld;
import yaes.sensornetwork.scenarios.bridgeprotection.BpaAgent;
import yaes.sensornetwork.scenarios.tryandbounce.TryAndBounceAgent;
import yaes.sensornetwork.visualization.SensorNetworkWorldPainter;
import yaes.sensornetwork.visualization.StealthySensorPainter;
import yaes.sensornetwork.visualization.paintEnvironment;
import yaes.sensornetwork.visualization.paintIntruderIcon;
import yaes.sensornetwork.visualization.paintTrackingError;
import yaes.ui.visualization.Visualizer;
import yaes.ui.visualization.painters.IPainter;
import yaes.ui.visualization.painters.paintMobileNode;
import yaes.world.physical.location.IMoving;
import yaes.world.physical.location.Location;
import yaes.world.physical.map.ArrangementHelper;

/**
 * This class implements a general purpose context which can be reused in many
 * sensor network papers.
 * 
 * NOTE: this was originally extracted from the one from the TAB so it is using
 * StealthySensorNetworkWorld etc... Probably some ongoing effort to clean it
 * would be very useful.
 * 
 * @author lboloni
 * 
 */
public class SensorNetworkContext extends AbstractContext implements
		constSensorNetwork {

	private static final long serialVersionUID = -1034456061592184370L;
	protected static final String SINK_NODE_NAME = "Sink";
	private int sensorNodeCount;
	protected StealthySensorNetworkWorld sensorWorld;
	protected SinkNode theSinkNode;
	protected double transmissionRange;
	public Environment environment;
	public IntruderSightingHistory groundTruth = new IntruderSightingHistory();
	public TrackingError trackingError = null;
	protected transient IPainter painterNode = null;

	/**
	 * Creates an environment. By default, this is the environment for the
	 * Partnership area at UCF.
	 * 
	 * Overwrite this to create a custom environment.
	 * 
	 * @return the newly created environment
	 */
	protected Environment createEnvironment() {
		Environment retval = EnvironmentGenerator.genenvPartnershipArea();
		return retval;
	}

	
	/**
	 *   Implements a variety of intruder scenarios.
	 */
	protected void createIntruderNodes() {
		int intruderCount = sip
				.getParameterInt(constSensorNetwork.Intruders_Number);
		double intruderSpeed = sip
				.getParameterDouble(constSensorNetwork.Intruders_Speed);
		IntruderScenario intruderScenario = sip
				.getParameterEnum(IntruderScenario.class);
		switch (intruderScenario) {
		case UCF_PARTNERSHIP:
			IntruderGenerator.genintrVarietyInPartnershipArea(this);
			break;
		case RANDOMLY_DISTRIBUTED_CROSSINGS:
			IntruderGenerator.genintrRandomlyDistributedCrossings(this,
					intruderCount, (int) sip.getStopTime() / 2, intruderSpeed,
					random);
			break;
		case COMBING:
			IntruderGenerator.genintrLine(this, intruderCount, intruderSpeed);
			break;
		case ORBIT:
			IntruderGenerator.genintrOrbit(this, intruderCount, theSinkNode
					.getLocation(), environment.getInterestArea().getHeight(),
					intruderSpeed);
			break;
		default:
			break;
		}

	}
	
	
	
	/**
	 * Creates a sensor agent for the given static node. The parameter
	 * SensorAgentClass determines the class of the agent. A number of
	 * 
	 * @param sip
	 * @param staticNode
	 * @return
	 */
	protected AbstractSensorAgent createSensorNodeAgent(SensorNode staticNode) {
		AbstractSensorAgent staticNodeAgent = null;
		switch (sip.getParameterEnum(SensorAgentClass.class)) {
		case SimpleIntruderTracking: {
			final IntruderTrackingSimpleSensorAgent agent = new IntruderTrackingSimpleSensorAgent(
					staticNode.getName(), sensorWorld);
			double timeInterval = sip
					.getParameterDouble(constSensorNetwork.AgentParameter_SimpleIntruderTracking_Interval);
			agent.setInterval(timeInterval);
			staticNodeAgent = agent;
			break;
		}
		case KnowledgeBasedSensor: {
			KnowledgeBasedSensorAgent agent = new KnowledgeBasedSensorAgent(
					staticNode.getName(), sensorWorld, environment);
			// specific parameters
			boolean useKnowledgePassthrough = (sip
					.getParameterInt(constSensorNetwork.AgentParameter_KBSA_UseKnowledgePassthrough) == 0);
			agent.setUsePassThroughKnowledge(useKnowledgePassthrough);
			boolean useKnowledgeOverheard = (sip
					.getParameterInt(constSensorNetwork.AgentParameter_KBSA_UseKnowledgeOverheard) == 0);
			agent.setUseOverheardKnowledge(useKnowledgeOverheard);
			agent.setKnowledgeMetric(sip
					.getParameterEnum(KBSA_KnowledgeMetric.class));
			staticNodeAgent = agent;
			break;
		}
		case DirectedDiffusion: {
			DirectedDiffusionAgent agent = new DirectedDiffusionAgent(
					staticNode.getName(), sensorWorld);
			staticNodeAgent = agent;
			break;
		}
		case TryAndBounce: {
			TryAndBounceAgent agent = new TryAndBounceAgent(
					staticNode.getName(), sensorWorld);
			agent.setRunningAverageTransmissionCostThreshold(sip
					.getParameterDouble(AgentParameter_TryAndBounce_TransmissionCostThreshold));
			staticNodeAgent = agent;
			break;
		}
		case BridgeProtectionAlgorithm: {
			BpaAgent agent = new BpaAgent(staticNode.getName(), sensorWorld);
			staticNodeAgent = agent;
			break;
		}
		case AnjiSensorNetwork:
		case Ive:
		case TryAndBounceES:
		case UnderWater:
		case VideoSensorNetwork:
		case VirtualCoordinate:
		default:
			break;
		}
		// parameters common to all sensor node agents
		staticNodeAgent
				.setTransmissionRange(sip
						.getParameterDouble(constSensorNetwork.SensorDeployment_TransmissionRange));
		staticNodeAgent
				.setSensorRange(sip
						.getParameterDouble(constSensorNetwork.SensorDeployment_SensorRange));
		return staticNodeAgent;
	}

	/**
	 * Creates and arranges the sensor nodes, adds the energy model to them
	 * 
	 * @param sip
	 */
	protected void createSensorNodes() {
		for (int i = 0; i < sensorNodeCount; i++) {
			final SensorNode staticNode = new SensorNode();
			staticNode.setName("S-" + String.format("%02d", i));
			final AbstractSensorAgent staticNodeAgent = createSensorNodeAgent(staticNode);
			// set the energy model
			RapaportCommunicationEnergyModel cem = new RapaportCommunicationEnergyModel(
					RapaportCommunicationEnergyModel.PowerConsumptionScenario.HIGH_PATH_LOSS);
			staticNodeAgent.setEnergyParameters(cem, 100, 0, true);
			staticNode.setAgent(staticNodeAgent);
			staticNodeAgent.setNode(staticNode);
			sensorWorld.addSensorNode(staticNode);
			sensorWorld.getDirectory().addAgent(staticNodeAgent);
		}
		distributeSensorNodes();
	}

	/**
	 * Creates a visual representation
	 * 
	 * @param sip
	 */

	@Override
	public void createVisualRepresentation(Visualizer existingVisualizer) {
		//
		// create the visualizer covering the full considered area
		//
		if (existingVisualizer != null) {
			visualizer = existingVisualizer;
			visualizer.removeAllObjects();
		} else {
			if (sip.getSimulationControlPanel() == null) {
				visualizer = new Visualizer((int) environment.getFullArea()
						.getWidth(), (int) environment.getFullArea()
						.getHeight(), null, "Sensor network", true);
			} else {
				visualizer = new Visualizer((int) environment.getFullArea()
						.getWidth(), (int) environment.getFullArea()
						.getHeight(), null, "Sensor network", true, true);
				sip.getSimulationControlPanel().addTab("Visual", visualizer);
			}
		}
		//
		// add the painting for the environment and the tracking error
		//
		if (trackingError != null) {
			visualizer.addObject(trackingError, new paintTrackingError());
		}
		visualizer.addObject(environment, new paintEnvironment());
		// add the painting of the sink node
		visualizer.addObject(sensorWorld.getSinkNode(), new paintMobileNode(20,
				Color.black, Color.red));
		// add the painting for the sensor nodes
		if (painterNode == null) {
			painterNode = new StealthySensorPainter(sensorWorld);
		}
		for (final SensorNode node : sensorWorld.getSensorNodes()) {
			visualizer.addObject(node, painterNode);
		}
		// add the painting for the intruder nodes
		paintIntruderIcon mPainter = new paintIntruderIcon(sensorWorld);
		for (final IntruderNode node : sensorWorld.getIntruderNodes()) {
			visualizer.addObject(node, mPainter);
		}
		// add the painting for the sensor world
		visualizer.addObject(sensorWorld, new SensorNetworkWorldPainter());
	}

	/**
	 * Distributes the location of the sensor nodes based on the specification
	 * in the SimulationInput
	 * 
	 * It uses the following parameter from sip
	 * <ul>
	 * <li>SensorArrangement.class
	 * </ul>
	 * 
	 * @param sip
	 */
	protected void distributeSensorNodes() {
		ArrayList<IMoving> listSensorNodes = new ArrayList<IMoving>(
				sensorWorld.getSensorNodes());
		switch (sip.getParameterEnum(SensorArrangement.class)) {
		case GRID: {
			Rectangle2D.Double rect = environment.getSensorDistributionArea();
			ArrangementHelper.arrangeInAGrid((int) rect.getMinX(),
					(int) rect.getMinY(), (int) rect.getMaxX(),
					(int) rect.getMaxY(), listSensorNodes);
			break;
		}
		case GRID_WITH_NOISE: {
			Rectangle2D.Double rect = environment.getSensorDistributionArea();
			ArrangementHelper.arrangeInAGridWithNoise((int) rect.getMinX(),
					(int) rect.getMinY(), (int) rect.getMaxX(),
					(int) rect.getMaxY(), listSensorNodes, random, 0.1);
			break;
		}
		case RANDOM: {
			ArrangementHelper.arrangeRandomlyInARectangle(
					environment.getSensorDistributionArea(), listSensorNodes,
					random);
			break;
		}
		case BENCHMARK:
		default:
			throw new Error("SensorArrangement:"
					+ sip.getParameterEnum(SensorArrangement.class)
					+ " not supported here.");
		}
	}

	@Override
	public StealthySensorNetworkWorld getWorld() {
		return sensorWorld;
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
	protected void createSinkNode() {
		double sinkNodeX = sip
				.getParameterDouble(constSensorNetwork.SensorDeployment_SinkNodeX);
		double sinkNodeY = sip
				.getParameterDouble(constSensorNetwork.SensorDeployment_SinkNodeY);
		Location sinkNodeLocation = new Location(sinkNodeX, sinkNodeY);

		switch (sip.getParameterEnum(SensorAgentClass.class)) {
		case DirectedDiffusion: {
			theSinkNode = DDSinkAgent.createSinkNode(SINK_NODE_NAME,
					sensorWorld, transmissionRange, sinkNodeLocation,
					environment.getInterestArea());
			List<Rectangle2D.Double> interestRectangles = DirectedDiffusionAgentFactory
					.createInterestRectanglesAroundNodes(
							sensorWorld.getSensorNodes(), 50);
			double interval = sip
					.getParameterDouble(AgentParameter_DirectedDiffusion_ReportInterval);
			((DDSinkAgent) (theSinkNode.getAgent()))
					.setInterestRectanglesDirectedDiffusion(interestRectangles,
							interval);
			break;
		}
		case AnjiSensorNetwork:
		case BridgeProtectionAlgorithm:
		case Ive:
		case KnowledgeBasedSensor:
		case SimpleIntruderTracking:
		case TryAndBounce:
		case TryAndBounceES:
		case UnderWater:
		case VideoSensorNetwork:
		case VirtualCoordinate:
		default: {
			theSinkNode = SDIntruderTrackingSinkAgent.createSinkNode(
					SINK_NODE_NAME, sensorWorld, transmissionRange,
					sinkNodeLocation, environment.getSensorDistributionArea());
			break;
		}
		}
	}

	/**
	 * Calculate the routing paths towards the sink
	 */
	protected void createPaths() {
		switch (sip.getParameterEnum(SensorAgentClass.class)) {
		case KnowledgeBasedSensor:
		case BridgeProtectionAlgorithm:
		case SimpleIntruderTracking: {
			SensorRoutingHelper.createPathsForForwarderSensorAgents(
					theSinkNode.getAgent(), sensorWorld);
			break;
		}
		case TryAndBounce:
		case Ive: {
			SensorRoutingHelper.createPathsForSortedNeighbors(
					theSinkNode.getAgent(), sensorWorld);
			break;
		}
		case DirectedDiffusion: {
			throw new Error(
					"The interest model of the directed diffusion agent has not been yet implemented for routing");
		}
		case AnjiSensorNetwork:
		case TryAndBounceES:
		case UnderWater:
		case VideoSensorNetwork:
		case VirtualCoordinate:
		default:
			break;

		}
	}

	/**
	 * Initializes the context
	 * 
	 */
	@Override
	public void initialize(SimulationInput sip, SimulationOutput sop) {
		super.initialize(sip, sop);
		environment = createEnvironment();
		// start by extracting the values from the the simulation parameter
		this.sensorNodeCount = sip
				.getParameterInt(constSensorNetwork.SensorDeployment_SensorNodeCount);
		this.transmissionRange = sip
				.getParameterDouble(constSensorNetwork.SensorDeployment_TransmissionRange);
		boolean keepTimeSeries = sip
				.getParameterInt(constSensorNetwork.SimControl_KeepTimeSeries) != 0;
		this.sensorWorld = new StealthySensorNetworkWorld(sop, keepTimeSeries);
		this.theWorld = sensorWorld;
		this.sensorWorld.setEndOfTheWorldTime((int) sip.getStopTime() - 1);
		this.random = new Random(
				sip.getParameterInt(constSensorNetwork.Intruders_MovementRandomSeed));
		//
		//
		createSinkNode();
		createSensorNodes();
		createPaths();
		createIntruderNodes();
		//
		// set up the tracking error calculation for the SDIntruderTrackingAgent
		//
		if (theSinkNode.getAgent() instanceof SDIntruderTrackingSinkAgent) {
			trackingError = new TrackingError();
			trackingError.environment = environment;
			trackingError.estimateIsh = ((SDIntruderTrackingSinkAgent) theSinkNode
					.getAgent()).getIntruderSightingHistory();
			trackingError.estimateEst = new SimpleEstimator();
			trackingError.groundTruthIsh = groundTruth;
			trackingError.groundTruthEst = new SimpleEstimator();
		}
		// create the visual representations
		if (sip.getParameterEnum(VisualDisplay.class) == VisualDisplay.YES) {
			createVisualRepresentation(null);
		}
	}

	/**
	 * Update the ground truth FIXME: this will need to be put into the
	 * IntruderTrackingWorld
	 */
	public void updateGroundTruth() {
		for (IntruderNode node : sensorWorld.getIntruderNodes()) {
			double time = sensorWorld.getTime();
			IntruderSighting is = new IntruderSighting("groundtruth", time,
					node.getName(), node.getLocation());
			groundTruth.addSighting(is, sensorWorld.getTime());
		}
		//
		// tracking error calculation for the SDIntruderTrackingSinkAgent
		//
		if (theSinkNode.getAgent() instanceof SDIntruderTrackingSinkAgent) {
			trackingError.time = sensorWorld.getTime();

			// updates the metrics of the tracking errors in the simulation
			// output
			// this version does not even consider anything...
			// FIXME: make this more sophisticated, based on the accuracy
			// metrics

			double sumErrorsInterestArea = trackingError
					.getSumInterestAreaTrackingErrorForAllIntruders();
			sop.update(Metrics_InterestAreaErrorSum, sumErrorsInterestArea);

			double sumErrors = trackingError
					.getSumAbsoluteTrackingErrorForAllIntruders();
			sop.update(Metrics_AbsoluteErrorSum, sumErrors);
		}
	}
}
