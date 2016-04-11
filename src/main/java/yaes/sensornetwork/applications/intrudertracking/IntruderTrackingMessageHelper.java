/**
 * 
 */
package yaes.sensornetwork.applications.intrudertracking;

import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.model.Perception;
import yaes.sensornetwork.model.SensorNetworkMessageConstants;

/**
 * @author lboloni
 * 
 */
public class IntruderTrackingMessageHelper implements
		IntruderTrackingMessageConstants {
	/**
	 * Creates a no perception message, used for forced temporary reporting
	 * 
	 * @param sender
	 * @param destination
	 * @param interestType
	 * @return
	 */
	public static ACLMessage createNoPerceptionMessage(String sender,
			String destination, String interestType) {
		final ACLMessage message = new ACLMessage(sender,
				ACLMessage.Performative.INFORM);
		message.setDestination(destination);
		message.setValue(SensorNetworkMessageConstants.FIELD_CONTENT,
				SensorNetworkMessageConstants.MESSAGE_DATA);
		message.setValue(SensorNetworkMessageConstants.FIELD_TYPE, interestType);
		message.setValue(SensorNetworkMessageConstants.FIELD_INTENSITY, 0);
		return message;
	}

	/**
	 * Creates a message for a gradient based on an observation. If the
	 * perception is NoPerception, then it will send a message with the
	 * perception put to 0.
	 * 
	 * @return
	 */
	public static ACLMessage createPerceptionReportMessage(String sender,
			String destination, String interestType, Perception p) {
		final ACLMessage message = new ACLMessage(sender,
				ACLMessage.Performative.INFORM);
		message.setDestination(destination);
		message.setValue(SensorNetworkMessageConstants.FIELD_CONTENT,
				SensorNetworkMessageConstants.MESSAGE_DATA);
		message.setValue(SensorNetworkMessageConstants.FIELD_TYPE, interestType);
		message.setValue(SensorNetworkMessageConstants.FIELD_PERCEPTION_ID, p
				.getId());
		message.setValue(SensorNetworkMessageConstants.FIELD_INTENSITY, 1);
		message.setValue(IntruderTrackingMessageConstants.FIELD_INTRUDER_NAME,
				p.getMovingObject().getName());
		message.setValue(
				IntruderTrackingMessageConstants.FIELD_INTRUDER_LOCATION, p
						.getMovingObject().getLocation());
		message.setValue(
				IntruderTrackingMessageConstants.FIELD_INTRUDER_OBSERVER,
				sender);
		message.setValue(IntruderTrackingMessageConstants.FIELD_INTRUDER_TIME,
				p.getTime());
		return message;
	}

}
