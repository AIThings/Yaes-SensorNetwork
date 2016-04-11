package yaes.sensornetwork.agents.directeddiffusion;

import java.util.HashMap;

import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingMessageConstants;
import yaes.sensornetwork.model.Perception;
import yaes.sensornetwork.model.SensorNetworkMessageConstants;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.ui.text.TextUiHelper;

/**
 * 
 * This agent implements a very simple version of the directed diffusion
 * paradigm. It knows its own position and the position of the sink. If a
 * message is coming from a node which is closer to the sink than him, it does
 * not retransmit it. Otherwise it transmits it.
 * 
 * If it observes a node, it immediately transmits its position.
 * 
 * 
 * @author Lotzi Boloni
 * 
 */
public final class DirectedDiffusionAgent extends AbstractSensorAgent implements
		IntruderTrackingMessageConstants {

	/**
     * 
     */
	private static final long serialVersionUID = 8566898396573005237L;
	protected final HashMap<String, DDSensorInterestManager> interestManagers = new HashMap<String, DDSensorInterestManager>();
	protected boolean observationMadeThisCycle;

	// protected TABWorldModel localWorldModel;
	// protected SimpleReasoner simpleReasoner;

	public DirectedDiffusionAgent(String name, SensorNetworkWorld sensingManager) {
		super(name, sensingManager);
		// localWorldModel = new TABWorldModel(name);
		// simpleReasoner = new SimpleReasoner(localWorldModel);
	}

	@Override
	protected void afterProcessingPerceptions() {
		for (DDSensorInterestManager sim : interestManagers.values()) {
			sim.processTime();
		}
		if (!observationMadeThisCycle) {
			processNoObservation();
		}
		purgeExpiredInterests();
	}

	@Override
	protected void beforeProcessingPerceptions() {
		observationMadeThisCycle = false;
	}

	/**
	 * Returns a collection of interests
	 * 
	 * @return
	 */
	public HashMap<String, DDSensorInterestManager> getSensorInterestManagers() {
		return interestManagers;
	}

	/**
	 * Handles the presence of an intruder
	 * 
	 * @param p
	 */
	@Override
	protected void handleIntruderPresence(Perception p) {
		observationMadeThisCycle = true;
		for (final DDSensorInterestManager interest : interestManagers.values()) {
			if (interest
					.getSensorInterestRole()
					.equals(
							DDSensorInterestManager.SensorInterestRole.SensorForInterest)) {
				interest.processPerception(p);
			}
		}
	}

	/**
	 * @param message
	 */
	@Override
	protected void handleOverheardMessage(ACLMessage message) {
		// does not handle overheard messages
	}

	/**
	 * Process -interest -data message
	 * 
	 * @param m
	 */
	@Override
	protected void handleReceivedMessage(final ACLMessage m) {
		if (m.getValue(SensorNetworkMessageConstants.FIELD_CONTENT).equals(
				SensorNetworkMessageConstants.MESSAGE_INTEREST)) {
			processInterestMessage(m);
		}
		if (m.getValue(SensorNetworkMessageConstants.FIELD_CONTENT).equals(
				SensorNetworkMessageConstants.MESSAGE_DATA)) {
			processDataMessage(m);
		}
	}

	/**
	 * Process a received data message, and forwards it, if there are interested
	 * gradients
	 * 
	 * <ul>
	 * <li>check for an interest in the type of observation
	 * <li>check for gradients.
	 * <li>for each gradient, if it is time to send, send a peer-to-peer message
	 * </ul>
	 * 
	 * @param m
	 */
	protected void processDataMessage(ACLMessage m) {
		final String interestType = (String) m
				.getValue(SensorNetworkMessageConstants.FIELD_TYPE);
		final DDSensorInterestManager interest = interestManagers
				.get(interestType);
		if (interest == null) { // no interest, do not do anything
			return;
		}
		// simpleReasoner.nodeTransmitted(world.getTime(), m.getSender());
		interest.processDataMessage(m);
	}

	/**
	 * An interest was received.
	 * <ul>
	 * <li>check whether there is an interest for the type. If not, create.
	 * <li>check whether there is a gradient for the sender. If not, create.
	 * <li>update the gradient.
	 * <li>forward the interest if any update was made
	 * </ul>
	 * 
	 * @param m
	 */
	protected void processInterestMessage(ACLMessage message) {
		final String interestType = (String) message
				.getValue(SensorNetworkMessageConstants.FIELD_TYPE);
		final int interestDuration = (Integer) message
				.getValue(SensorNetworkMessageConstants.FIELD_DURATION);
		DDSensorInterestManager interest = interestManagers.get(interestType);
		// add a check for expired interestManagers, ignore them
		if (interestDuration < getWorld().getTime()) {
			return;
		}
		if (interest == null) { // create the interest
			DDSensorInterestManager sih = new DDSensorInterestManager(this,
					getSensorWorld());
			sih.createInterestFromMessage(message);
			interestManagers.put(sih.getInterestType(), sih);
			return;
		} else {
			interest.processUpdateMessage(message);
		}
	}

	/**
	 * 
	 * No observation was performed.
	 * 
	 * <ul>
	 * <li>Check for an interest in the type of observation.
	 * <li>Check for gradients on the observation.
	 * <li>For each gradient if it is time to send, send a peer to peer message.
	 * </ul>
	 * 
	 * @param p
	 */
	protected void processNoObservation() {
		// nothing here
	}

	/**
	 * It is here to be overridden
	 */
	protected void purgeExpiredInterests() {
		InterestHelper.purgeExpiredInterests(interestManagers);
	}

	/**
	 * Prints out an agent for debugging purposes
	 */
	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("DirectedDiffusionAgent: "
				+ getName());
		if (!interestManagers.isEmpty()) {
			for (final DDSensorInterestManager interest : interestManagers
					.values()) {
				buffer.append("\n" + interest);
			}
		} else {
			buffer.append("\n No interestManagers.");
		}
		buffer.append("\n");
		buffer.append(TextUiHelper.createLabeledSeparator("-Local world model"));
		// buffer.append(localWorldModel.toString());
		return buffer.toString();
	}

}
