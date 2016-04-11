/**
 * 
 */
package yaes.sensornetwork.scenarios.tryandbounce;

import java.io.Serializable;
import java.util.List;

import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.agents.SortedNeighborsSensorAgent;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingMessageConstants;
import yaes.sensornetwork.model.Perception;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.stealth.StealthySensorNetworkWorld;
import yaes.sensornetwork.scenarios.tryandbounce.TABReportModel.ReportModelState;
import yaes.ui.format.Formatter;
import yaes.ui.text.TextUi;
import yaes.world.physical.location.INamedMoving;
import yaes.world.physical.location.Location;

/**
 * 
 * Implements the try and bounce stealth agent
 * 
 * @author lboloni
 * 
 */
public class TryAndBounceAgent extends SortedNeighborsSensorAgent implements
		IntruderTrackingMessageConstants, Serializable{

	private static final long serialVersionUID = -1156514665509565724L;
	protected TABWorldModel localWorldModel;
	protected TABReasoner reasoner;

	public TryAndBounceAgent(String name, SensorNetworkWorld wsnWorld) {
		super(name, wsnWorld);
		localWorldModel = new TABWorldModel(name, wsnWorld);
		reasoner = new TABReasoner(localWorldModel, wsnWorld.getSimulationOutput());
		ConstantStealthCostSendingDecision costAccounting = new ConstantStealthCostSendingDecision();
		setSendingDecision(costAccounting);
	}

	/**
	 * Processes all the reports
	 * 
	 */
	@Override
	protected void afterProcessingPerceptions() {
		for (TABReportModel rep : reasoner.getModel().getReportModels()) {
			TABReportModel report = rep;
			switch (report.getState()) {
			case NOT_REPORTED:
				processReport(report);
				break;
			case CONFIRMED:
				// do nothing, but it might come back to us
				break;
			case IN_PROGRESS:
				// check for timeout
				double inflight = (getSensorWorld().getTime())
						- report.getInprogressSince();
				if (inflight >= 2) {
					reasoner.reportTimeout(getSensorWorld().getTime(), report);
				}
				break;
			case FAILED:
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void beforeProcessingPerceptions() {
		((ConstantStealthCostSendingDecision) getSendingDecision())
				.updateOnTime();
	}

	@Override
	protected void handleIntruderPresence(final Perception p) {
		INamedMoving threat = p.getMovingObject();
		reasoner.intruderSighted(world.getTime(), threat.getName(), threat
				.getLocation(), INTEREST_TYPE_INTRUDER, p.getId());
	}

	/**
	 * This agent uses the overhearing that his own transmission was confirmed
	 * 
	 * @param message
	 */
	@Override
	protected void handleOverheardMessage(final ACLMessage m) {
		String threatName = (String) m.getValue(FIELD_INTRUDER_NAME);
		List<TABReportModel> reports = reasoner.getModel().getReportsForThreat(
				threatName);
		if (reports.isEmpty()) {
			return;
		}
		TABReportModel reportModel = (reports.get(0));
		reasoner.reportForwardingConfirmed(getSensorWorld().getTime(),
				reportModel);
	}

	/**
	 * 
	 * 
	 * @param m
	 */
	@Override
	protected void handleReceivedMessage(final ACLMessage m) {
		String content = (String) m.getValue(FIELD_CONTENT);
		if (content.equals(MESSAGE_DATA)) {
			// handle incoming data
			double observationTime = (Double) m.getValue(FIELD_INTRUDER_TIME);
			String threatName = (String) m.getValue(FIELD_INTRUDER_NAME);
			Location threatLocation = (Location) m
					.getValue(FIELD_INTRUDER_LOCATION);
			TABPathRecord record = (TABPathRecord) m
					.getValue(FIELD_PATH_RECORD);
			String originalReporter = (String) m
					.getValue(FIELD_INTRUDER_OBSERVER);
			String interestName = (String) m.getValue(FIELD_TYPE);
			int perceptionId = (Integer) m.getValue(FIELD_PERCEPTION_ID);
			reasoner.intruderReported(getSensorWorld().getTime(),
					observationTime, threatName, threatLocation, interestName,
					perceptionId, false, originalReporter, m.getSender(),
					"Sink", record);
			return;
		}
		if (content.equals(MESSAGE_CANNOT_SEND)) {
			// handle cannot send
			String threatName = (String) m.getValue(FIELD_INTRUDER_NAME);
			TABReportModel reportModel = (reasoner.getModel()
					.getReportsForThreat(threatName).get(0));
			TABPathRecord record = (TABPathRecord) m
					.getValue(FIELD_PATH_RECORD);
			reasoner.reportReturned(getSensorWorld().getTime(), reportModel,
					record);
			return;
		}
	}

	/**
	 * 
	 * Processes a report, decides where to send etc.
	 * 
	 * @param report
	 */
	private void processReport(TABReportModel report) {
		if (!getSendingDecision().readyToSend(this, report.getIntruderNode())) {
			report.setState(ReportModelState.FAILED);
			return;
		}
		getSendingDecision().sent(this, report.getIntruderNode());
		// decides where to send
		TABPathRecord pathRecord = report.getPathRecord();
		String nextHop = null;
		// try the route first
		for (String hop : getHopsToSink()) {
			if (pathRecord.canBeNextHop(hop)) {
				nextHop = hop;
			}
		}
		// then, try anything else
		if (nextHop == null) {
			for (String hop : getNeighbors()) {
				if (pathRecord.canBeNextHop(hop)) {
					nextHop = hop;
				}
			}
		}
		if (nextHop == null) {
			if (report.getPathRecord().getCannotSendDestination() != null) {
				reasoner.reportCanNotSend(getSensorWorld().getTime(), report);
				if (report.getPathRecord().getOriginalObserver() == null) {
					// TextUi.println(this);
					// throw new Error("Not possible!!!");
					// some kind of went around stuff...
					report.setState(ReportModelState.FAILED);
				} else {
					ACLMessage m = TABMessageHelper.createCannotSendMessage(
							getName(), report);
					transmit(m);
				}
			} else {
				TextUi.println("Can not send, abandoning report!" + report);
				report.setState(ReportModelState.FAILED);
			}
			// if there is nowhere to send, send a failed message back

		} else {
			// forward the message to the selected hop
			reasoner.reportSent(getSensorWorld().getTime(), report, nextHop);
			if (nextHop.equals("Sink")) {
				reasoner.reportConfirmed(getSensorWorld().getTime(), report);
			}
			ACLMessage m = TABMessageHelper.createReportMessage(getName(),
					nextHop, report);
			transmit(m);
		}
	}

	/**
	 * @param runningAverageTransmissionCostThreshold
	 *            the runningAverageTransmissionCostThreshold to set
	 */
	public void setRunningAverageTransmissionCostThreshold(
			double runningAverageTransmissionCostThreshold) {
		((ConstantStealthCostSendingDecision) getSendingDecision())
				.setRunningAverageTransmissionCostThreshold(runningAverageTransmissionCostThreshold);
	}

	/**
	 * Prints out an agent for debugging purposes
	 */
	@Override
	public String toString() {
	    Formatter fmt = new Formatter();
        fmt.add("TryAndBounceAgent");
        fmt.indent();
        SensorNetworkWorld snw = getSensorWorld();
		if (snw instanceof StealthySensorNetworkWorld) {
			StealthySensorNetworkWorld ssnw = (StealthySensorNetworkWorld) snw;
			fmt.is("Stealth level", ssnw.getStealthModel(getNode()).getStealthLevel());
		}
        fmt.add(toStringPaths());
        fmt.add(toStringCommonProperties());
        fmt.add(localWorldModel.toString());
		return fmt.toString();
	}

}
