/**
 * 
 */
package yaes.sensornetwork.agents.directeddiffusion;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.List;

import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingMessageConstants;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingMessageHelper;
import yaes.sensornetwork.model.Perception;
import yaes.sensornetwork.model.SensorNetworkMessageConstants;
import yaes.sensornetwork.model.SensorNetworkWorld;

/**
 * The code for the per interest management
 * 
 * @author lboloni
 * 
 */
public class DDSensorInterestManager implements
		IntruderTrackingMessageConstants, Serializable {

	public enum SensorInterestRole {
		Expired, Router, SensorForInterest, Unknown
	}

	private static final long serialVersionUID = 6322388779355777108L;;

	private AbstractSensorAgent agent;
	private Interest interest;
	private int reinforcedPaths = 1;
	/**
	 * Sends empty messages (if there was no perception)
	 */
	private boolean sendEmptyMessages = true;
	private SensorInterestRole sensorInterestRole = SensorInterestRole.Unknown;
	private SensorNetworkWorld sensorWorld;

	/**
	 * Creates a sensor interest manager
	 * 
	 * @param agent
	 * @param sensorWorld
	 */
	public DDSensorInterestManager(AbstractSensorAgent agent,
			SensorNetworkWorld sensorWorld) {
		this.agent = agent;
		this.sensorWorld = sensorWorld;
	}

	/**
	 * Creates a message which forwards the current parameters of the interest
	 * 
	 * @param destination
	 * @param interestType
	 * @param interestRectangle
	 * @param interestDuration
	 * @param interestInterval
	 * @return
	 */
	private ACLMessage createForwardMessage(String destination,
			String interestType, Rectangle2D.Double interestRectangle,
			int interestDuration, int interestInterval) {
		final ACLMessage forwardMessage = new ACLMessage(agent.getName(),
				ACLMessage.Performative.REQUEST_WHENEVER);
		forwardMessage.setDestination(destination);
		forwardMessage.setValue(SensorNetworkMessageConstants.FIELD_CONTENT,
				SensorNetworkMessageConstants.MESSAGE_INTEREST);
		forwardMessage.setValue(SensorNetworkMessageConstants.FIELD_TYPE,
				interestType);
		forwardMessage.setValue(SensorNetworkMessageConstants.FIELD_RECTANGLE,
				interestRectangle);
		forwardMessage.setValue(SensorNetworkMessageConstants.FIELD_DURATION,
				interestDuration);
		forwardMessage.setValue(SensorNetworkMessageConstants.FIELD_INTERVAL,
				interestInterval);
		return forwardMessage;
	}

	/**
	 * Creates the interest from the initial message
	 * 
	 * @param message
	 */
	public void createInterestFromMessage(ACLMessage message) {
		final String interestReceivedFromSensor = message.getSender();
		final String interestType = (String) message
				.getValue(SensorNetworkMessageConstants.FIELD_TYPE);
		final Rectangle2D.Double interestRectangle = (Rectangle2D.Double) message
				.getValue(SensorNetworkMessageConstants.FIELD_RECTANGLE);
		final int interestDuration = (Integer) message
				.getValue(SensorNetworkMessageConstants.FIELD_DURATION);
		final int interestInterval = (Integer) message
				.getValue(SensorNetworkMessageConstants.FIELD_INTERVAL);
		interest = new Interest(interestType, interestReceivedFromSensor,
				interestInterval, interestDuration, interestRectangle);
		// FIXME: what I need here is whether the transmission range is
		// intersecting
		if (agent.getSensorRangeShape().intersects(interestRectangle)) {
			sensorInterestRole = SensorInterestRole.SensorForInterest;
		} else {
			sensorInterestRole = SensorInterestRole.Router;
		}
		ACLMessage forwardMessage = createForwardMessage("*", interestType,
				interestRectangle, interestDuration, interestInterval);
		agent.transmit(forwardMessage);
	}

	/**
	 * Does the reinforcement. For each interest, choose the node to reinforce.
	 * These are the nodes which were the first to report. Then, it sets the
	 * send empty messages to false
	 */
	public void doReinforcement(int interestDuration, int interestInterval) {
		List<String> trustworthyNodes = InterestHelper
				.findTrustworthyNodes(interest);
		if (trustworthyNodes.isEmpty()) {
			return;
		}
		// the number of paths to reinforce is the smaller from our wish
		// and the actual number of trustworthy nodes
		int pathsToReinforce = reinforcedPaths;
		if (trustworthyNodes.size() < pathsToReinforce) {
			pathsToReinforce = trustworthyNodes.size();
		}
		for (int i = 0; i < pathsToReinforce; i++) {
			String dest = trustworthyNodes.get(i);
			ACLMessage message = createForwardMessage(dest, interest
					.getInterestType(), interest.getRectangle(),
					interestDuration, interestInterval);
			agent.transmit(message);
		}
		sendEmptyMessages = false;
	}

	/**
	 * @return the interest
	 */
	public Interest getInterest() {
		return interest;
	}

	public String getInterestType() {
		return interest.getInterestType();
	}

	/**
	 * @return the sensorInterestRole
	 */
	public SensorInterestRole getSensorInterestRole() {
		return sensorInterestRole;
	}

	/**
	 * Processes an incoming data message matching this interest
	 * 
	 * @param m
	 */
	protected void processDataMessage(ACLMessage m) {
		// boolean sendFirstOnly = true;
		final int perceptionId = (Integer) m
				.getValue(SensorNetworkMessageConstants.FIELD_PERCEPTION_ID);
		String sender = m.getSender();
		if (interest.handlePreviouslyProcessedPerception(perceptionId, sender)) {
			// Perception previously processed
			return;
		}
		for (final Gradient gradient : interest.getGradientCollection()) {
			if (!gradient.isActive()) {
				continue;
			}
			ACLMessage retransmit = new ACLMessage(gradient.getSensorName(),
					agent.getNode().getName(), m);
			agent.transmit(retransmit);
		}
		// sendFirstOnly = true;
	}

	/**
	 * Processes a perception - sends to all gradients, which are ready
	 * 
	 * @param p
	 */
	public void processPerception(Perception p) {
		for (final Gradient gradient : interest.getGradientCollection()) {
			final double currentTime = sensorWorld.getTime();
			if (currentTime - gradient.getLastMessageSent() < gradient
					.getInterval()) {
				continue;
			}
			gradient.setLastMessageSent(currentTime);
			ACLMessage message = IntruderTrackingMessageHelper
					.createPerceptionReportMessage(agent.getName(), gradient
							.getSensorName(), interest.getInterestType(), p);
			agent.transmit(message);
		}
	}

	/**
	 * Process based on time
	 * <ul>
	 * <li>updates the gradients (expires them)
	 * <li>if there is no gradient left, set itself to expired mode
	 * <li>sends empty messages, if necessary
	 * </ul>
	 */
	public void processTime() {
		interest.updateInterestBasedOnTime(sensorWorld.getTime());
		if (interest.getGradientCollection().isEmpty()) {
			sensorInterestRole = SensorInterestRole.Expired;
			return;
		}
		// if we are a router, do not send empty messages
		if (sensorInterestRole == SensorInterestRole.Router) {
			return;
		}
		if ((sensorInterestRole == SensorInterestRole.SensorForInterest)
				&& !sendEmptyMessages) {
			return;
		}
		for (final Gradient gradient : interest.getGradientCollection()) {
			if (!gradient.isActive()) {
				continue;
			}
			final double currentTime = sensorWorld.getTime();
			if (currentTime - gradient.getLastMessageSent() < gradient
					.getInterval()) {
				continue;
			}
			if (currentTime - gradient.getLastEmptyMessageSent() < gradient
					.getInterval()) {
				continue;
			}
			// it is time to send something:
			// Perception p = new Perception(
			// Perception.PerceptionType.NoPerception, currentTime);
			gradient.setLastEmptyMessageSent(currentTime);
			ACLMessage message = IntruderTrackingMessageHelper
					.createNoPerceptionMessage(agent.getName(), gradient
							.getSensorName(), interest.getInterestType());
			agent.transmit(message);
		}
	}

	/**
	 * Processes an incoming update message, which might be a reinforcement.
	 * 
	 * @param message
	 */
	public void processUpdateMessage(ACLMessage message) {
		final String interestReceivedFromSensor = message.getSender();
		// final Rectangle2D.Double interestRectangle = (Rectangle2D.Double)
		// message
		// .getValue(SensorNetworkMessageConstants.FIELD_RECTANGLE);
		final int interestDuration = (Integer) message
				.getValue(SensorNetworkMessageConstants.FIELD_DURATION);
		final int interestInterval = (Integer) message
				.getValue(SensorNetworkMessageConstants.FIELD_INTERVAL);

		// if it is not initial, it is reinforcement
		boolean forward = interest.refreshGradient(interestReceivedFromSensor,
				interestInterval, interestDuration);
		if (!forward) {
			return;
		}
		doReinforcement(interestDuration, interestInterval);
	}

	/**
	 * @param interest
	 *            the interest to set
	 */
	public void setInterest(Interest interest) {
		this.interest = interest;
	}

	/**
	 * @param sensorInterestRole
	 *            the sensorInterestRole to set
	 */
	public void setSensorInterestRole(SensorInterestRole sensorInterestRole,
			boolean sendEmptyMessages) {
		this.sensorInterestRole = sensorInterestRole;
		this.sendEmptyMessages = false;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("DDSensorInterestManager role:" + sensorInterestRole
				+ "\n");
		buffer.append("for" + interest);
		return buffer.toString();
	}

}
