/**
 * 
 */
package yaes.sensornetwork.scenarios.tryandbounce;

import java.io.Serializable;

import yaes.ui.text.TextUiHelper;
import yaes.world.physical.location.Location;

/**
 * An extension of the reportmodel with the TAB structure
 * 
 * @author lboloni
 * 
 */
public class TABReportModel implements Serializable {

	public enum ReportModelState {
		CONFIRMED, FAILED, IN_PROGRESS, NOT_REPORTED
	}

	private static final long serialVersionUID = -4164414659899647552L;

	private double inprogressSince;
	private String interestName;
	private Location intruderLocation;;

	private String intruderNode;
	private String nodeResponsibleForProgress;
	private double observationTime;
	private TABPathRecord pathRecord;
	private int perceptionId;
	private ReportModelState state;

	private String description() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Interest:" + interestName + "\n");
		buffer.append("Intruder " + intruderNode + " is at location "
				+ intruderLocation + "\n");
		buffer.append("Observation time:" + observationTime + "\n");
		return TextUiHelper.indent(buffer.toString());
	}

	/**
	 * @return the inprogressSince
	 */
	public double getInprogressSince() {
		return inprogressSince;
	}

	/**
	 * @return the interestName
	 */
	public String getInterestName() {
		return interestName;
	}

	/**
	 * @return the intruderLocation
	 */
	public Location getIntruderLocation() {
		return intruderLocation;
	}

	/**
	 * @return the intruderNode
	 */
	public String getIntruderNode() {
		return intruderNode;
	}

	/**
	 * @return the nodeResponsibleForProgress
	 */
	public String getNodeResponsibleForProgress() {
		return nodeResponsibleForProgress;
	}

	/**
	 * @return the observationTime
	 */
	public double getObservationTime() {
		return observationTime;
	}

	/**
	 * @return the pathRecord
	 */
	public TABPathRecord getPathRecord() {
		return pathRecord;
	}

	/**
	 * @return the perceptionId
	 */
	public int getPerceptionId() {
		return perceptionId;
	}

	/**
	 * @return the state
	 */
	public ReportModelState getState() {
		return state;
	}

	/**
	 * @param inprogressSince
	 *            the inprogressSince to set
	 */
	public void setInprogressSince(double inprogressSince) {
		this.inprogressSince = inprogressSince;
	}

	/**
	 * @param interestName
	 *            the interestName to set
	 */
	public void setInterestName(String interestName) {
		this.interestName = interestName;
	}

	/**
	 * @param intruderLocation
	 *            the intruderLocation to set
	 */
	public void setIntruderLocation(Location intruderLocation) {
		this.intruderLocation = intruderLocation;
	}

	/**
	 * @param intruderNode
	 *            the intruderNode to set
	 */
	public void setIntruderNode(String intruderNode) {
		this.intruderNode = intruderNode;
	}

	/**
	 * @param nodeResponsibleForProgress
	 *            the nodeResponsibleForProgress to set
	 */
	public void setNodeResponsibleForProgress(String nodeResponsibleForProgress) {
		this.nodeResponsibleForProgress = nodeResponsibleForProgress;
	}

	/**
	 * @param observationTime
	 *            the observationTime to set
	 */
	public void setObservationTime(double observationTime) {
		this.observationTime = observationTime;
	}

	/**
	 * @param pathRecord
	 *            the pathRecord to set
	 */
	public void setPathRecord(TABPathRecord pathRecord) {
		this.pathRecord = pathRecord;
	}

	/**
	 * @param perceptionId
	 *            the perceptionId to set
	 */
	public void setPerceptionId(int perceptionId) {
		this.perceptionId = perceptionId;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(ReportModelState state) {
		this.state = state;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("REPORT " + perceptionId);
		switch (state) {
		case CONFIRMED:
			buffer.append("[CONFIRMED]\n" + description());
			break;
		case FAILED:
			buffer.append("[FAILED]\n" + description());
			buffer.append("   responsible:" + nodeResponsibleForProgress);
			break;
		case IN_PROGRESS:
			buffer.append("[IN PROGRESS]\n" + description());
			buffer.append("   responsible:" + nodeResponsibleForProgress);
			break;
		case NOT_REPORTED:
			buffer.append("[NOT REPORTED]\n" + description());
			buffer.append("   responsible:" + nodeResponsibleForProgress);
			break;
		default:
			break;
		}
		buffer.append(pathRecord + "\n");
		return buffer.toString() + "\n";
	}

}
