package yaes.sensornetwork.model.stealth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yaes.framework.agent.ACLMessage;
import yaes.framework.simulation.SimulationOutput;
import yaes.sensornetwork.constSensorNetwork;
import yaes.sensornetwork.applications.intrudertracking.IntruderNode;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.SensorNode;
import yaes.sensornetwork.model.SinkNode;

/**
 * This is an extension of the sensor network world environment which in
 * addition to managing the radio transmission, sensor sightings etc. is also
 * managing the stealth level of the individual nodes.
 * 
 * @author Lotzi Boloni
 * 
 */
public class StealthySensorNetworkWorld extends SensorNetworkWorld implements
		constSensorNetwork {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1597555519393426972L;
	private static final double P_DEPLOY = 0;
	private static final double P_AD = 0;
	private static final double PATH_LOSS_INDEX = 3;
	private static final double PL_OVER_PG = 0.01; // was 0.1
	private static final double P_ATTENTION = 0.01; // was 1
	private Map<SensorNode, SimpleStealthModel> stealthModels = new HashMap<SensorNode, SimpleStealthModel>();

	public StealthySensorNetworkWorld(SimulationOutput so,
			boolean keepStealthMetricHistory) {
		super(so);
		if (keepStealthMetricHistory) {
			so.createVariable(Metrics_StealthAvg, true);
			so.createVariable(Metrics_StealthMin, true);
			so.createVariable(Metrics_StealthMax, true);
			so.createVariable(Metrics_StealthConnected, true);
		}
	}

	/**
	 * Whenever adding a sensor node, also create the stealth model of it.
	 */
	@Override
	public void addSensorNode(SensorNode node) {
		super.addSensorNode(node);
		stealthModels.put(node, new SimpleStealthModel(P_DEPLOY, P_AD));
	}

	/**
	 * Returns the cost of a node to transmit
	 */
	public double costOfTransmitting(SensorNode node) {
		SimpleStealthModel model = new SimpleStealthModel(stealthModels
				.get(node));
		double originalStealth = model.getStealthLevel();
		List<IntruderNode> actuators = getIntruderNodes();
		for (IntruderNode actuator : actuators) {
			double distance = node.getLocation().distanceTo(
					actuator.getLocation());
			TransmissionDisclosureEvent event = new TransmissionDisclosureEvent(
					distance, node.getAgent().getTransmissionRange(),
					PATH_LOSS_INDEX, PL_OVER_PG, P_ATTENTION);
			model.updateEvent(event);
		}
		return originalStealth - model.getStealthLevel();
	}

	public SimpleStealthModel getStealthModel(SensorNode node) {
		return stealthModels.get(node);
	}

	/**
	 * in addition to delivering all the messages, also update the time based
	 * component of the stealth models
	 */
	@Override
	public void messageFlush() {
		super.messageFlush();
		for (SensorNode node : getSensorNodes()) {
			SimpleStealthModel model = stealthModels.get(node);
			model.update(getTime());
		}
		// measures and prints the stealth values - this needs to go the
		double maxStealth = StealthMeasurementHelper.stealthMaximum(this);
		getSimulationOutput().update(Metrics_StealthMax, maxStealth);
		double minStealth = StealthMeasurementHelper.stealthMinimum(this);
		getSimulationOutput().update(Metrics_StealthMin, minStealth);
		double avgStealth = StealthMeasurementHelper.stealthAverage(this);
		getSimulationOutput().update(Metrics_StealthAvg, avgStealth);

	}

	@Override
	public void setSinkNode(SinkNode sinkNode) {
		super.setSinkNode(sinkNode);
		stealthModels.put(sinkNode, new SimpleStealthModel(P_DEPLOY, P_AD));
	}

	/**
	 * Whenever transmitting, update the stealth model as well.
	 */
	@Override
	public void transmit(SensorNode node, ACLMessage message) {
		super.transmit(node, message);
		SimpleStealthModel model = stealthModels.get(node);
		// updating the stealth level on transmission
		// List<ActuatorNode> actuators = getSensorWorld().getActuatorNodes();
		List<IntruderNode> actuators = getIntruderNodes();
		for (IntruderNode actuator : actuators) {
			double distance = node.getLocation().distanceTo(
					actuator.getLocation());
			TransmissionDisclosureEvent event = new TransmissionDisclosureEvent(
					distance, node.getAgent().getTransmissionRange(),
					PATH_LOSS_INDEX, PL_OVER_PG, P_ATTENTION);
			model.updateEvent(event);
		}
	}
}
