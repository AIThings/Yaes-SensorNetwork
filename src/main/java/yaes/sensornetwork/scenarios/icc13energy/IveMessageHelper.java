package yaes.sensornetwork.scenarios.icc13energy;

import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingMessageConstants;

/**
 * Helper functions for creating messages in the InformationValue-Energy model
 * @author Lotzi Boloni
 *
 */
public class IveMessageHelper implements IntruderTrackingMessageConstants {

	/**
	 * Creates a message for a report observation. 
	 * 
	 * @return
	 */
	public static ACLMessage createReportMessage(String sender,
			String destination, IveReportModel report) {
		final ACLMessage message = new ACLMessage(sender,
				ACLMessage.Performative.INFORM);
		message.setDestination(destination);
		message.setValue(FIELD_CONTENT, MESSAGE_DATA);
		message.setValue(FIELD_TYPE,
				IntruderTrackingMessageConstants.INTEREST_TYPE_INTRUDER);
		message.setValue(FIELD_INTENSITY, 1);
		message.setValue(FIELD_PERCEPTION_ID, report.getPerceptionId());
		message.setValue(FIELD_INTRUDER_NAME, report.getIntruderNode());
		message.setValue(FIELD_INTRUDER_LOCATION, report.getIntruderLocation());
		message.setValue(FIELD_INTRUDER_OBSERVER, report.getSensingNode());
		message.setValue(FIELD_INTRUDER_TIME, report.getObservationTime());
		return message;
	}

	
}
