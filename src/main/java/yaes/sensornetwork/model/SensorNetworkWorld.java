package yaes.sensornetwork.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yaes.framework.agent.ACLMessage;
import yaes.framework.simulation.SimulationOutput;
import yaes.sensornetwork.applications.intrudertracking.IntruderNode;
import yaes.sensornetwork.model.Perception.PerceptionType;
import yaes.ui.format.IDetailLevel;
import yaes.ui.format.ToStringDetailed;
import yaes.ui.text.TextUiHelper;
import yaes.world.World;
import yaes.world.physical.location.IMoving;
import yaes.world.physical.location.INamed;
import yaes.world.physical.location.Location;

/**
 * This class manages the transmissions and perceptions of the agents
 * 
 * @author Lotzi Boloni
 * 
 */
public class SensorNetworkWorld extends World implements INamed,
		ToStringDetailed, constSensorNetworkWorld {
	/**
     * 
     */
	private static final long serialVersionUID = 2631520831248708361L;
	public static final String HIDDEN_FIELD_SENDER_NODE = "senderNode";
	private final List<IntruderNode> actuatorNodes = new ArrayList<>();
	private final List<ACLMessage> messageCache = new ArrayList<>();
	private final Map<IMoving, SensingHistory> sensingHistory = new HashMap<>();
	private final List<SensorNode> sensorNodes = new ArrayList<>();
	private final Map<String, SensorNode> sensorNodesMap = new HashMap<>();
	private SinkNode sinkNode = new SinkNode();

	public SensorNetworkWorld(SimulationOutput so) {
		super(so);
		// create the variables
		initSoVariables();
	}

	/**
	 * Initializes the SO variables specific for the sensor network world
	 */
	private void initSoVariables() {
		getSimulationOutput().createVariable(
				constSensorNetworkWorld.SENSORNETWORK_MESSAGES_SENT, false);
		getSimulationOutput().createVariable(
				constSensorNetworkWorld.SENSORNETWORK_MESSAGES_RECEIVED, false);
		getSimulationOutput().createVariable(
				constSensorNetworkWorld.SENSORNETWORK_MESSAGES_OVERHEARD, false);
		getSimulationOutput()
				.createVariable(
						constSensorNetworkWorld.SENSORNETWORK_PERCEPTIONS_INTRUDERPRESENCE,
						false);
	}

	
    /**
     * Change the simulation output. Normally, this is necessary in situations when 
     * we want to run a different simulation with the same world, (for instance if
     * it was used for caching). 
     * 
     * @param simulationOutput
     */
	@Override
    public void changeSimulationOutput(SimulationOutput simulationOutput) {
    	super.changeSimulationOutput(simulationOutput);  
    	initSoVariables();
    }
	
	
	
	/**
	 * Adds a new intruder node to the sensor network world
	 * 
	 * @param node
	 */
	public void addIntruderNode(IntruderNode node) {
		actuatorNodes.add(node);
	}

	/**
	 * Adds a new sensor node to the sensor network world Creates the necessary
	 * variables in the simulation output
	 * 
	 * @param node
	 */
	public void addSensorNode(SensorNode node) {
		sensorNodes.add(node);
		sensorNodesMap.put(node.getName(), node);
		getSimulationOutput().createVariable(
				constSensorNetworkWorld.SENSORNETWORK_MESSAGES_SENT + "_"
						+ node.getName(), false);
		getSimulationOutput().createVariable(
				constSensorNetworkWorld.SENSORNETWORK_MESSAGES_RECEIVED + "_"
						+ node.getName(), false);
		getSimulationOutput().createVariable(
				constSensorNetworkWorld.SENSORNETWORK_MESSAGES_OVERHEARD + "_"
						+ node.getName(), false);
		getSimulationOutput()
				.createVariable(
						constSensorNetworkWorld.SENSORNETWORK_PERCEPTIONS_INTRUDERPRESENCE
								+ "_" + node.getName(), false);

	}

	/**
	 * Returns a read only list of actuator nodes
	 * 
	 * @return
	 */
	public List<IntruderNode> getIntruderNodes() {
		return Collections.unmodifiableList(actuatorNodes);
	}

	/**
	 * @return
	 */
	@Override
	public String getName() {
		return "Sensor network world";
	}

	/**
	 * Gets the sensing history associated with the sensor or actuator node (or
	 * creates it as needed)
	 * 
	 * @return
	 */
	public SensingHistory getSensingHistory(IMoving node) {
		SensingHistory sh = sensingHistory.get(node);
		if (sh == null) {
			sh = new SensingHistory(this);
			sensingHistory.put(node, sh);
		}
		return sh;
	}

	/**
	 * Returns a read only list of sensor nodes
	 * 
	 * @return
	 */
	public List<SensorNode> getSensorNodes() {
		return Collections.unmodifiableList(sensorNodes);
	}

	public SinkNode getSinkNode() {
		return sinkNode;
	}

	/**
	 * 
	 * Looks up the sensor node by the name -return null if not found
	 * 
	 * @return
	 */
	public SensorNode lookupSensorNodeByName(String sensorNodeName) {
		/*
		 * for (SensorNode node : sensorNodes) { if
		 * (node.getName().equals(sensorNodeName)) { return node; } }
		 */
		return sensorNodesMap.get(sensorNodeName);
	}

	/**
	 * Deliver all the pending messages
	 */
	public void messageFlush() {
		for (final ACLMessage message : messageCache) {
			transmitNow(message);
		}
		messageCache.clear();
	}

	/**
	 * An actuator node has moved. -it appears as an IntruderPresence perception
	 * to the sensors whose sensing range it covers
	 * 
	 * @param node
	 */
	public void move(IntruderNode actuatorNode) {
		if (actuatorNode.getLocation() == null) {
			throw new Error("Actuator node location is null!!!");
		}
		for (final SensorNode node : sensorNodes) {
			Location sensorNodeLocation = node.getLocation();
			if (sensorNodeLocation == null) {
				throw new Error("sensor node location is null!!!");
			}
			final double distance = sensorNodeLocation.distanceTo(actuatorNode
					.getLocation());
			if (distance <= node.getAgent().getSensorRange()) {
				final SensingHistory s = getSensingHistory(node);
				s.addPerception(new Perception(PerceptionType.IntruderPresence,
						getTime(), actuatorNode));
				getSimulationOutput()
						.update(
								constSensorNetworkWorld.SENSORNETWORK_PERCEPTIONS_INTRUDERPRESENCE,
								1);
				getSimulationOutput()
						.update(
								constSensorNetworkWorld.SENSORNETWORK_PERCEPTIONS_INTRUDERPRESENCE
										+ "_" + node.getName(), 1);
			}
		}
	}

	/**
	 * An actuator node (sink node) has moved. -it appears as an IntruderPresence perception
	 * to the sensors whose sensing range it covers
	 * 
	 * @param node
	 */
	public void move(SinkNode sinkNode) {
		if (sinkNode.getLocation() == null) {
			throw new Error("Sink node location is null!!!");
		}
		for (final SensorNode node : sensorNodes) {
			Location sensorNodeLocation = node.getLocation();
			if (sensorNodeLocation == null) {
				throw new Error("sensor node location is null!!!");
			}
			final double distance = sensorNodeLocation.distanceTo(sinkNode
					.getLocation());
			if (distance <= node.getAgent().getSensorRange()) {
				final SensingHistory s = getSensingHistory(node);
				s.addPerception(new Perception(PerceptionType.SinkNodePrescene,
						getTime(), sinkNode));
				getSimulationOutput()
						.update(
								constSensorNetworkWorld.SENSORNETWORK_PERCEPTIONS_INTRUDERPRESENCE,
								1);
				getSimulationOutput()
						.update(
								constSensorNetworkWorld.SENSORNETWORK_PERCEPTIONS_INTRUDERPRESENCE
										+ "_" + node.getName(), 1);
			}
		}
	}
	
	/**
	 * Processes the sensor nodes (including the sink)
	 * 
	 * @param sender
	 * @param message
	 */
	private void processSensorNode(SensorNode node, SensorNode sender,
			ACLMessage message) {
		final String destination = message.getDestination();
		SimulationOutput sop = getSimulationOutput();
		boolean broadcast;
		if (destination.equals("*")) { // broadcast
			broadcast = true;
		} else {
			broadcast = false;
		}
		if (node == sender) {
			return;
		}
		if (sender.getAgent().getTransmissionRangeShape().contains(
				node.getLocation().asPoint())) {
			final SensingHistory s = getSensingHistory(node);
			PerceptionType pt;
			if (broadcast || destination.equals(node.getName())) {
				pt = PerceptionType.ReceivedMessage;
				sop.updateDual(SENSORNETWORK_MESSAGES_RECEIVED, node.getName(), 1);
			} else {
				pt = PerceptionType.Overhearing;
				sop.updateDual(SENSORNETWORK_MESSAGES_OVERHEARD, node.getName(), 1);
			}
			s.addPerception(new Perception(pt, message, getTime()));
		}
	}

	public void setSinkNode(SinkNode sinkNode) {
		this.sinkNode = sinkNode;
	}

	/**
     * 
     */
	@Override
	public String toString() {
		return toStringDetailed(IDetailLevel.MIN_DETAIL);
	}

	/**
	 * @param detailLevel
	 * @return
	 */
	@Override
	public String toStringDetailed(int detailLevel) {
		switch (detailLevel) {
		case MIN_DETAIL:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case MAX_DETAIL:
			return toStringDetailedMax();
		default:
			break;
		}
		throw new Error("This should not happen");
	}

	public String toStringDetailedMax() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(TextUiHelper.createHeader("Sensor network world"));
		buffer.append(TextUiHelper.createLabeledSeparator("-Message cache"));
		for (ACLMessage message : messageCache) {
			buffer.append(message.toString() + "\n");
		}
		buffer.append(TextUiHelper.createLabeledSeparator("-Sink"));
		buffer.append(sinkNode.toString() + "\n");
		buffer.append(TextUiHelper.createLabeledSeparator("-Sensor nodes"));
		for (SensorNode node : sensorNodes) {
			buffer.append(node.toString() + "\n");
		}
		buffer.append(TextUiHelper.createLabeledSeparator("-Actuator nodes"));
		for (IntruderNode node : actuatorNodes) {
			buffer.append(node.toString() + "\n");
		}
		return buffer.toString();
	}

	/**
	 * Transmission - but it will not be actually transmitted until the end of
	 * the cycle
	 * 
	 * @param message
	 */
	public void transmit(SensorNode node, ACLMessage message) {
		message.setValue(SensorNetworkWorld.HIDDEN_FIELD_SENDER_NODE, node);
		messageCache.add(message);
	}

	/**
	 * Transmits a message (generally defined message) -it will appear as a
	 * ReceivedMessage perception to other sensors in the transmission range -it
	 * will appear as an Overhearing perception to actuators in the transmission
	 * range
	 * 
	 * @param node
	 * @param message
	 */
	private void transmitNow(ACLMessage message) {
		getSimulationOutput().update(
				constSensorNetworkWorld.SENSORNETWORK_MESSAGES_SENT, 1);
		final SensorNode sender = (SensorNode) message
				.getValue(SensorNetworkWorld.HIDDEN_FIELD_SENDER_NODE);
		getSimulationOutput().update(
				constSensorNetworkWorld.SENSORNETWORK_MESSAGES_SENT + "_"
						+ sender.getName(), 1);
		// TextUi.println(message);
		// add the message to all the other sensors in the transmission range
		for (final SensorNode node : sensorNodes) {
			processSensorNode(node, sender, message);
		}
		processSensorNode(sinkNode, sender, message);
		// add the message to all the actuators in the transmission range as an
		// overhearing
		for (final IntruderNode node : actuatorNodes) {
			// final double distance = node.getLocation().distanceTo(
			// sender.getLocation());
			if (sender.getAgent().getTransmissionRangeShape().contains(
					node.getLocation().asPoint())) {
				final SensingHistory s = getSensingHistory(node);
				s.addPerception(new Perception(PerceptionType.Overhearing,
						message, getTime()));
			}
		}
	}

}
