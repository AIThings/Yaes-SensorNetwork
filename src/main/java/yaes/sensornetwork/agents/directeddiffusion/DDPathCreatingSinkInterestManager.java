package yaes.sensornetwork.agents.directeddiffusion;

import java.awt.geom.Rectangle2D;
import java.util.List;

import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.model.SensorNetworkMessageConstants;

/**
 * 
 * Directed diffusion sink interest manager. Captures the process of the
 * creation and reinforcement of the directed diffusion gradients by the sink
 * 
 * @author Lotzi Boloni
 * 
 */
public class DDPathCreatingSinkInterestManager extends DDSinkInterestManager {

	public enum SinkInterestState {
		BEFORE_BROADCAST, READY_TO_REINFORCE, REINFORCED, WAITING_FOR_INITIAL
	}

	private static final int FINAL_DURATION = 3000;
	private static final int FINAL_INTERVAL = 100; // was
													// 5
	private static final int INITIAL_DURATION = 60;
	private static final int INITIAL_INTERVAL = 5;

	private static final long serialVersionUID = -3222253423764615878L;;

	private int reinforcedPathPlan = 1; // the
										// number
										// of
										// paths
										// to
										// reinforce
	private SinkInterestState sinkInterestState;
	private double timeBroadcastSent;
	private double timeReinforcementSent;

	public DDPathCreatingSinkInterestManager(String interestType,
			Rectangle2D.Double rectangle, DDSinkAgent agent) {
		super(interestType, rectangle, agent);
		this.setSinkInterestState(SinkInterestState.BEFORE_BROADCAST);
	}

	/**
	 * Attends to the interest, including
	 * 
	 * @param si
	 */
	public void attendToInterest(double time) {
		switch (getSinkInterestState()) {
		case BEFORE_BROADCAST:
			setDuration(DDPathCreatingSinkInterestManager.INITIAL_DURATION);
			setInterval(DDPathCreatingSinkInterestManager.INITIAL_INTERVAL);
			setTimeBroadcastSent(time);
			ACLMessage message = createInterestMessage("*", getInterest()
					.getRectangle(), getDuration(), getInterval());
			agent.transmit(message);
			setSinkInterestState(SinkInterestState.WAITING_FOR_INITIAL);
			break;
		case WAITING_FOR_INITIAL:
			if (time - getTimeBroadcastSent() > DDPathCreatingSinkInterestManager.INITIAL_DURATION / 2) {
				setSinkInterestState(SinkInterestState.READY_TO_REINFORCE);
			}
			break;
		case READY_TO_REINFORCE:
			doReinforcement();
			setTimeReinforcementSent(time);
			setSinkInterestState(SinkInterestState.REINFORCED);
			break;
		case REINFORCED:
			// nothing here, collect data
			break;
		default:
			break;
		}
	}

	/**
	 * Creates an interest message for a rectangle, duration, interval
	 * 
	 * @param rect
	 * @param duration
	 * @param interval
	 * @return
	 */
	private ACLMessage createInterestMessage(String destination,
			Rectangle2D.Double rect, double duration, double interval) {
		final ACLMessage message = new ACLMessage(agent.getName(),
				ACLMessage.Performative.REQUEST_WHENEVER);
		message.setDestination(destination);
		message.setValue(SensorNetworkMessageConstants.FIELD_CONTENT,
				SensorNetworkMessageConstants.MESSAGE_INTEREST);
		message.setValue(SensorNetworkMessageConstants.FIELD_TYPE,
				getInterest().interestType);
		message.setValue(SensorNetworkMessageConstants.FIELD_RECTANGLE, rect);
		message.setValue(SensorNetworkMessageConstants.FIELD_DURATION, duration);
		message.setValue(SensorNetworkMessageConstants.FIELD_INTERVAL, interval);
		return message;
	}

	/**
	 * Does the reinforcement. For each interest, choose the node to reinforce.
	 * These are the nodes which were the first to report.
	 */
	public void doReinforcement() {
		List<String> trustworthyNodes = InterestHelper
				.findTrustworthyNodes(getInterest());
		// int sizeNeighbor = trustworthyNodes.size();
		if (trustworthyNodes.isEmpty()) {
			throw new Error(
					"There is no trustworthy node, we cannot reinforce any...");

		}
		int reinforced = 0;
		for (String dest : trustworthyNodes) {
			ACLMessage message = createInterestMessage(dest, getInterest()
					.getRectangle(),
					DDPathCreatingSinkInterestManager.FINAL_DURATION,
					DDPathCreatingSinkInterestManager.FINAL_INTERVAL);
			agent.transmit(message);
			reinforced++;
			if (reinforced >= reinforcedPathPlan) {
				break;
			}
		}
	}

	/**
	 * @return the sinkInterestState
	 */
	public SinkInterestState getSinkInterestState() {
		return sinkInterestState;
	}

	/**
	 * @return the timeBroadcastSent
	 */
	public double getTimeBroadcastSent() {
		return timeBroadcastSent;
	}

	/**
	 * @return the timeReinforcementSent
	 */
	public double getTimeReinforcementSent() {
		return timeReinforcementSent;
	}

	/**
	 * @param sinkInterestState
	 *            the sinkInterestState to set
	 */
	public void setSinkInterestState(SinkInterestState sinkInterestState) {
		this.sinkInterestState = sinkInterestState;
	}

	/**
	 * @param timeBroadcastSent
	 *            the timeBroadcastSent to set
	 */
	public void setTimeBroadcastSent(double timeBroadcastSent) {
		this.timeBroadcastSent = timeBroadcastSent;
	}

	/**
	 * @param timeReinforcementSent
	 *            the timeReinforcementSent to set
	 */
	public void setTimeReinforcementSent(double timeReinforcementSent) {
		this.timeReinforcementSent = timeReinforcementSent;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Path creating sink interest manager - state:"
				+ sinkInterestState);
		return buffer.toString();
	}

}
