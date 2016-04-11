package yaes.sensornetwork.scenarios.bridgeprotection;

import java.util.List;

import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingMessageConstants;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingMessageHelper;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingSimpleSensorAgent;
import yaes.sensornetwork.model.SensorNetworkMessageConstants;
import yaes.sensornetwork.model.SensorNetworkWorld;

public class BpaAgent extends IntruderTrackingSimpleSensorAgent {

	public enum BpaState {
		NORMAL, BRIDGE, GATE, FANOUT, NEARSIDE, FARSIDE
	}

	private static final long serialVersionUID = -926394160194022737L;;

	private BpaState bpaState = BpaState.NORMAL;

	public BpaAgent(String name, SensorNetworkWorld sensorWorld) {
		super(name, sensorWorld);
	}

	private List<String> fanoutNodes;

	/**
	 * The message forwarding behavior
	 */
	@Override
	protected void forwardMessage(ACLMessage message) {
		switch (bpaState) {
		case NORMAL:
		case FANOUT:
		case FARSIDE:
		case NEARSIDE: {
			super.action();
			return;
		}
		case BRIDGE: {
			forwardingDestination = nextForwardingDestination();
			super.action();
			break;
		}
		case GATE: {
			// eat up the heartbeat messages
			if (message.getValue(SensorNetworkMessageConstants.FIELD_CONTENT)
					.equals(SensorNetworkMessageConstants.MESSAGE_DATA)) {
				Object intruderName = message
						.getValue(IntruderTrackingMessageConstants.FIELD_INTRUDER_NAME);
				if (intruderName != null) {
					ACLMessage forward = new ACLMessage(forwardingDestination,
							getName(), message);
					transmit(forward);
				}
				return;
			}

		}
			break;
		default:
			break;
		}
	}

	/**
	 * Circles through the fanoutnodes for the forwarding destination
	 * 
	 * @return
	 */
	private String nextForwardingDestination() {
		String retval = null;
		boolean current = false;
		for (String dest : fanoutNodes) {
			if (current) {
				retval = dest;
				break;
			}
			if (dest.equals(forwardingDestination)) {
				current = true;
			}
		}
		if (retval == null) {
			retval = fanoutNodes.get(0);
		}
		return retval;
	}

	/**
	 * Flush all the pending messages
	 * 
	 */
	@Override
	protected void flushPendingMessages() {
		// if there is nothing to send, send a heartbeat
		if (pendingMessages.isEmpty()) {
			ACLMessage message = IntruderTrackingMessageHelper
					.createNoPerceptionMessage(getName(),
							forwardingDestination, "interestType");
			pendingMessages.put("-HeartBeat-", message);
		}
		super.flushPendingMessages();
	}

	public BpaState getBpaState() {
		return bpaState;
	}

	public void setBpaState(BpaState bpaState) {
		this.bpaState = bpaState;
	}

	/**
	 * @param fanoutNodes
	 *            the fanoutNodes to set
	 */
	public void setFanoutNodes(List<String> fanoutNodes) {
		this.fanoutNodes = fanoutNodes;
	}

	/**
	 * @return the fanoutNodes
	 */
	public List<String> getFanoutNodes() {
		return fanoutNodes;
	}

}
