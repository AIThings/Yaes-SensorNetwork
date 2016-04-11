package yaes.sensornetwork.scenarios.icc13energy;

import java.io.Serializable;

import yaes.ui.format.Formatter;
import yaes.world.physical.location.Location;

public class IveReportModel implements Serializable {

    /**
     * The state of the report
     * 
     * <ul>
     * <li>TO_SEND - there is an obligation to send
     * <li>SENT_BY_ME - it had been transmitted by me on the path to the nodes
     * <li>OVERHEARD - a transmission had been heard, no obligation to send
     * <li>KEPT - transmitted, but kept for future reference
     * </ul>
     * 
     * @author Lotzi Boloni
     * 
     */
    public enum IveReportModelState {
        MY_OBSERVATION, RECEIVED_WITH_OBLIGATION, SENT_BY_ME, OVERHEARD
    };

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
     * @return the observationTime
     */
    public double getObservationTime() {
        return observationTime;
    }

    /**
     * @return the perceptionId
     */
    public int getPerceptionId() {
        return perceptionId;
    }

    /**
     * @return the sensingNode
     */
    public String getSensingNode() {
        return sensingNode;
    }

    private IveReportModelState state;

    /**
     * @return the state
     */
    public IveReportModelState getState() {
        return state;
    }

    /**
     * @param state
     *            the state to set
     */
    public void setState(IveReportModelState state) {
        this.state = state;
    }

    /**
     * The time when the observation was sent
     */
    private double sentTime = -1;

    /**
     * @return the sentTime
     */
    public double getSentTime() {
        return sentTime;
    }

    /**
     * @param sentTime
     *            the sentTime to set
     */
    public void setSentTime(double sentTime) {
        this.sentTime = sentTime;
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = -4318269491045420985L;
    /**
     * The location of the intruder
     */
    private Location intruderLocation;
    /**
     * The name of the intruder
     */
    private String intruderNode;
    /**
     * The time when the observation had been made
     */
    private double observationTime;
    /**
     * The identifier of the perception which created it
     */
    private int perceptionId;
    /**
     * The name of the sensing node which made the sighting
     */
    private String sensingNode;

    /**
     * @param state
     * @param intruderNode
     * @param intruderLocation
     * @param observationTime
     * @param sensingNode
     * @param perceptionId
     */
    public IveReportModel(IveReportModelState state, String intruderNode,
            Location intruderLocation, double observationTime,
            String sensingNode, int perceptionId) {
        this.state = state;
        this.intruderNode = intruderNode;
        this.intruderLocation = intruderLocation;
        this.observationTime = observationTime;
        this.sensingNode = sensingNode;
        this.perceptionId = perceptionId;
    }

    /**
     * Formats the report
     */
    @Override
    public String toString() {
        Formatter fmt = new Formatter();
        fmt.add("IveReportModel state " + state);
        fmt.indent();
        fmt.add(intruderNode + " at location " + intruderLocation);
        fmt.add("observed by: " + sensingNode + " at time "
                + Formatter.fmt(observationTime) + " perceptionId: "
                + perceptionId);
        fmt.is("sentTime", sentTime);
        return fmt.toString();
    }
}
