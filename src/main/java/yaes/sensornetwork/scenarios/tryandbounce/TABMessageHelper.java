/**
 * 
 */
package yaes.sensornetwork.scenarios.tryandbounce;

import java.io.Serializable;

import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingMessageConstants;

/**
 * @author lboloni
 * 
 */
public class TABMessageHelper implements IntruderTrackingMessageConstants, Serializable {

	/**
	 * @param name
	 * @param report
	 * @return
	 */
	public static ACLMessage createCannotSendMessage(String sender,
			TABReportModel report) {
		final ACLMessage message = new ACLMessage(sender,
				ACLMessage.Performative.INFORM);
		message.setDestination(report.getPathRecord()
				.getCannotSendDestination());
		message.setValue(FIELD_CONTENT, MESSAGE_CANNOT_SEND);
		message.setValue(FIELD_TYPE, report.getInterestName());
		message.setValue(FIELD_INTENSITY, 1);
		message.setValue(FIELD_INTRUDER_NAME, report.getIntruderNode());
		message.setValue(FIELD_INTRUDER_LOCATION, report.getIntruderLocation());
		message.setValue(FIELD_INTRUDER_OBSERVER, report.getPathRecord()
				.getOriginalObserver());
		message.setValue(FIELD_INTRUDER_TIME, report.getObservationTime());
		message.setValue(FIELD_PATH_RECORD, report.getPathRecord());
		return message;
	}

	/**
	 * Creates a message for a gradient based on an observation. If the
	 * perception is NoPerception, then it will send a message with the
	 * perception put to 0.
	 * 
	 * @return
	 */
	public static ACLMessage createReportMessage(String sender,
			String destination, TABReportModel report) {
		final ACLMessage message = new ACLMessage(sender,
				ACLMessage.Performative.INFORM);
		message.setDestination(destination);
		message.setValue(FIELD_CONTENT, MESSAGE_DATA);
		message.setValue(FIELD_TYPE,
				IntruderTrackingMessageConstants.INTEREST_TYPE_INTRUDER);
		message.setValue(FIELD_TYPE, report.getInterestName());
		message.setValue(FIELD_INTENSITY, 1);
		message.setValue(FIELD_PERCEPTION_ID, report.getPerceptionId());
		message.setValue(FIELD_INTRUDER_NAME, report.getIntruderNode());
		message.setValue(FIELD_INTRUDER_LOCATION, report.getIntruderLocation());
		message.setValue(FIELD_INTRUDER_OBSERVER, report.getPathRecord()
				.getOriginalObserver());
		message.setValue(FIELD_INTRUDER_TIME, report.getObservationTime());
		message.setValue(FIELD_PATH_RECORD, report.getPathRecord());
		return message;
	}
}
