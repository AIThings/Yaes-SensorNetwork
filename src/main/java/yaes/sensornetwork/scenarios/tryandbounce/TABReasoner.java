/**
 * 
 */
package yaes.sensornetwork.scenarios.tryandbounce;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import yaes.framework.simulation.SimulationOutput;
import yaes.sensornetwork.constSensorNetwork;
import yaes.sensornetwork.scenarios.tryandbounce.NodeModel.NodeModelState;
import yaes.sensornetwork.scenarios.tryandbounce.TABReportModel.ReportModelState;
import yaes.world.physical.location.Location;

/**
 * The reasoner for the TAB
 * 
 * @author lboloni
 * 
 */
public class TABReasoner implements Serializable, constSensorNetwork {

	private static final long serialVersionUID = -4418678293148527641L;
	private TABWorldModel model;
	private double paramTimeoutDown = 500;
	private double paramTimeoutIntruder = 5;
	private SimulationOutput sop;

	/**
	 * @param model
	 * @param simulationOutput 
	 */
	public TABReasoner(TABWorldModel model, SimulationOutput sop) {
		this.model = model;
		this.sop = sop;
	}

	/**
	 * @return the model
	 */
	public TABWorldModel getModel() {
		return model;
	}


	/**
	 * An intruder was reported to me
	 * 
	 * This shadows all the other reports
	 */
	public void intruderReported(double currentTime, double observationTime,
			String intruderName, Location location, String interestName,
			int perceptionId, boolean overheardOnly, String sourceNode,
			String reportingNode, String destinationNode,
			TABPathRecord pathRecord) {
		sop.update(InternalMetrics_TryAndBounce_CountIntruderReported, 1);
		if (!intruderUpdate(observationTime, intruderName, location)) {
			return;
		}
		sop.update(InternalMetrics_TryAndBounce_CountIntruderReportedNewInfo, 1);		
		shadowAllReportsOfThisIntruder(intruderName);
		// create the new report
		TABReportModel report = new TABReportModel();
		report.setIntruderNode(intruderName);
		report.setIntruderLocation(location);
		report.setInterestName(interestName);
		report.setObservationTime(observationTime);
		report.setPerceptionId(perceptionId);
		if (!overheardOnly) {
			report.setNodeResponsibleForProgress(model.getMyself());
		} else {
			report.setNodeResponsibleForProgress(destinationNode);
		}
		report.setState(ReportModelState.NOT_REPORTED);
		report.setPathRecord(new TABPathRecord(pathRecord));
		model.getReportModels().add(report);
	}

	/**
	 * An intruder was sighted by me
	 * 
	 * This shadows all the other reports
	 */
	public void intruderSighted(double currentTime, String intruderName,
			Location location, String interestName, int perceptionId) {
		sop.update(InternalMetrics_TryAndBounce_CountSighted, 1);
		if (!intruderUpdate(currentTime, intruderName, location)) {
			return;
		}
		shadowAllReportsOfThisIntruder(intruderName);
		// create the new report
		TABReportModel report = new TABReportModel();
		report.setIntruderNode(intruderName);
		report.setInterestName(interestName);
		report.setIntruderLocation(location);
		report.setObservationTime(currentTime);
		report.setNodeResponsibleForProgress(model.getMyself());
		report.setState(ReportModelState.NOT_REPORTED);
		report.setPerceptionId(perceptionId);
		TABPathRecord pathRecord = new TABPathRecord(model.getMyself());
		report.setPathRecord(pathRecord);
		model.getReportModels().add(report);
		return;
	}

	/**
	 * Updates the knowledge about an intruder
	 * 
	 * @return true if an update has been made
	 */
	protected boolean intruderUpdate(double sightingTime, String intruderName,
			Location location) {
	    return model.addIntruderAtLocation(sightingTime, intruderName, location);
	}

	/**
	 * For all the nodes, if they timed out, their state is adjusted accordingly
	 * 
	 * @param currentTime
	 */
	public void nodeTimeUpdate(double currentTime) {
		for (NodeModel nodeModel : model.getNodeModels()) {
			switch (nodeModel.getState()) {
			case ALIVE: {
				if (currentTime - nodeModel.getLastHeard() > paramTimeoutIntruder) {
					nodeModel.setState(NodeModelState.THREATENED);
				}
				break;
			}
			case THREATENED: {
				if (currentTime - nodeModel.getLastHeard() > paramTimeoutDown) {
					nodeModel.setState(NodeModelState.DEAD);
				}
				break;
			}
			case DEAD: {
				// stays dead
			}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * If a node transmitted and recorded directly: -it is alive, in hearing
	 * range
	 * 
	 * @param currentTime
	 * @param nodeName
	 */
	public void nodeTransmitted(double currentTime, String nodeName) {
		for (NodeModel nodeModel : model.getNodeModels()) {
			if (nodeModel.getName().equals(nodeName)) {
				nodeModel.setState(NodeModelState.ALIVE);
				nodeModel.setLastHeard(currentTime);
				nodeModel.setInHearingRange(true);
				return;
			}
		}
		// was not found create it
		NodeModel nodeModel = new NodeModel(nodeName);
		nodeModel.setState(NodeModelState.ALIVE);
		nodeModel.setLastHeard(currentTime);
		nodeModel.setInHearingRange(true);
		model.getNodeModels().add(nodeModel);
	}

	/**
	 * We can not send a report - we tried all the nodes
	 */
	public void reportCanNotSend(double currentTime, TABReportModel reportModel) {
		sop.update(InternalMetrics_TryAndBounce_Bounces, 1);
		reportModel.setState(ReportModelState.FAILED);
		reportModel.getPathRecord().addBouncedNodes(model.getMyself());
	}

	/**
	 * A report was confirmed
	 */
	public void reportConfirmed(double currentTime, TABReportModel reportModel) {
		sop.update(InternalMetrics_TryAndBounce_Confirmed, 1);
		reportModel.setState(TABReportModel.ReportModelState.CONFIRMED);
	}

	/**
	 * A report which was in progress, had failed
	 */
	public void reportFailed(double currentTime, TABReportModel reportModel) {
		sop.update(InternalMetrics_TryAndBounce_Failed, 1);
		reportModel.setState(TABReportModel.ReportModelState.FAILED);
	}

	/**
	 * Called when the forwarding of a message was confirmed (by overhearing)
	 */
	public void reportForwardingConfirmed(double currentTime,
			TABReportModel reportModel) {
		reportModel.setState(ReportModelState.CONFIRMED);
	}

	/**
	 * The report was returned (the sending node is not able to forward)
	 * 
	 * @param currentTime
	 * @param reportModel
	 * @param pathRecord
	 *            -- the path record the way it came back
	 */
	public void reportReturned(double currentTime, TABReportModel reportModel,
			TABPathRecord pathRecord) {
		reportModel.setState(ReportModelState.NOT_REPORTED);
		// we set the new path record, because it contains new failed nodes
		reportModel.setPathRecord(pathRecord);
		reportModel.setNodeResponsibleForProgress(model.getMyself());
	}

	/**
	 * Called before a report is sent (because the message needs to contain the
	 * new path record
	 * 
	 * @param currentTime
	 * @param reportModel
	 * @param nextNode
	 */
	public void reportSent(double currentTime, TABReportModel reportModel,
			String nextNode) {
		reportModel.setInprogressSince(currentTime);
		reportModel.setNodeResponsibleForProgress(nextNode);
		reportModel.setState(ReportModelState.IN_PROGRESS);
		reportModel.getPathRecord().addProgressNode(nextNode);
	}

	/**
	 * Called when a report which was reported, had timed out
	 * 
	 * @param currentTime
	 * @param reportModel
	 */
	public void reportTimeout(double currentTime, TABReportModel reportModel) {
		reportModel.getPathRecord().addFailedNode(
				reportModel.getNodeResponsibleForProgress());
		reportModel.setNodeResponsibleForProgress(model.getMyself());
		reportModel.setState(ReportModelState.NOT_REPORTED);
	}

	/**
	 * @param intruderName
	 */
	protected void shadowAllReportsOfThisIntruder(String intruderName) {
		// clean up all the reports related to this threat
		List<TABReportModel> shadowedReports = new ArrayList<TABReportModel>();
		for (TABReportModel report : model.getReportModels()) {
			if (report.getIntruderNode().equals(intruderName)) {
				sop.update(InternalMetrics_TryAndBounce_CountShadowing, 1);				
				shadowedReports.add(report);
			}
		}
		model.getReportModels().removeAll(shadowedReports);
	}

}
