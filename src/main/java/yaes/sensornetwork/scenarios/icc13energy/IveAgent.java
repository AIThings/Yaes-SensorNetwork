package yaes.sensornetwork.scenarios.icc13energy;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.agents.SortedNeighborsSensorAgent;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingMessageConstants;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingWorldModel.ItwmType;
import yaes.sensornetwork.model.Perception;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.scenarios.icc13energy.IveConstants.IveTransmissionPolicy;
import yaes.ui.format.Formatter;
import yaes.ui.text.TextUi;
import yaes.world.physical.location.INamedMoving;
import yaes.world.physical.location.Location;

/**
 * The information-value energy optimizer agent -initial version, mostly
 * inspired by the Try-and-Bounce
 * 
 * @author Lotzi Boloni
 * 
 */
public class IveAgent extends SortedNeighborsSensorAgent implements
        IntruderTrackingMessageConstants {

    private static final long serialVersionUID = -1156514665509565724L;
    protected IveWorldModel localWorldModel;
    protected IveReasoner reasoner;
    private IveTransmissionPolicy iveTransmissionPolicy;
    private double interval;
    private double voiThreshold;
    /**
     * The last transmission about the specific intruder
     */
    private Map<String, Double> lastTransmission = new HashMap<>();

    /**
     * Creates an agent
     * 
     * @param name
     * @param wsnWorld
     * @param type
     */
    public IveAgent(String name, SensorNetworkWorld wsnWorld, ItwmType type,
            IveTransmissionPolicy iveTransmissionPolicy, double interval,
            double voiThreshold) {
        super(name, wsnWorld);
        localWorldModel = new IveWorldModel(type, name, wsnWorld);
        reasoner =
                new IveReasoner(localWorldModel, wsnWorld.getSimulationOutput());
        this.iveTransmissionPolicy = iveTransmissionPolicy;
        this.interval = interval;
        this.voiThreshold = voiThreshold;
    }

    /**
     * Processes all the reports
     */
    @Override
    protected void afterProcessingPerceptions() {
        for (IveReportModel rep : reasoner.getModel().getReportModels()) {
            IveReportModel report = rep;
            switch (report.getState()) {
            case RECEIVED_WITH_OBLIGATION: {
                sendReport(report);
                break;
            }
            case MY_OBSERVATION: {
                if (decideReport(report)) {
                    sendReport(report);
                }
                break;
            }
            case OVERHEARD: {
                // do nothing
                break;
            }
            case SENT_BY_ME: {
                // do nothing
                break;
            }
			default:
				break;
            }
        }
    }

    /**
     * Decide whether the report is supposed to be sent
     * 
     * @param report
     * @return
     */
    private boolean decideReport(IveReportModel report) {

        String intruderNode = report.getIntruderNode();
        double time = report.getObservationTime();
        Location location = report.getIntruderLocation();
        //Location sinkEstimate =
        //        localWorldModel.estimateIntruderLocation(intruderNode);
        IveSinkAgent sinkAgent = (IveSinkAgent) getSensorWorld().getSinkNode().getAgent();
        // FIXME: this does not really covers what the sensor node believes, but this must be done
        IveWorldModel model = sinkAgent.getReasoner().getModel();
        Location sinkEstimate = model.estimateIntruderLocation(intruderNode);
        // if sink estimate is null, assume it is far away
        if (sinkEstimate == null) {
            sinkEstimate = new Location(10000, 10000);
        }
        switch (iveTransmissionPolicy) {
        case VOI_THRESHOLD: {
            //double distance = location.distanceTo(sinkEstimate);
            // estimate the value of the sink's estimate
            Rectangle2D.Double interestArea =
                    new Rectangle2D.Double(300, 300, 800, 300);
            double estimateValue = InformationValue.calculateInfoValue(location, sinkEstimate, interestArea);
            double maxInformationValue = 0;
            if (interestArea.contains(location.asPoint())) {
                maxInformationValue = InformationValue.MAX_VALUE_PER_TIME;
            }
            double gain = Math.max(0, maxInformationValue - estimateValue);
            // TextUi.println("Estimated gain: " + Formatter.fmt(gain));
            if (gain > voiThreshold) {
                return true;
            } else {
                return false;
            }
        }
        case PERIODIC: {
            // last transmission about this intruder
            double lastTransmissionTime = 0;
            if (lastTransmission.containsKey(intruderNode)) {
                lastTransmissionTime = lastTransmission.get(intruderNode);
            }
            if (world.getTime() - lastTransmissionTime > interval) {
                lastTransmission.put(intruderNode, world.getTime());
                return true;
            } else {
                return false;
            }
        }
		default:
			break;
        }
        return false;
    }

    @Override
    protected void beforeProcessingPerceptions() {
        // nothing here for the time being...
    }

    @Override
    protected void handleIntruderPresence(final Perception p) {
        INamedMoving threat = p.getMovingObject();
        reasoner.intruderSighted(world.getTime(), threat.getName(),
                threat.getLocation(), INTEREST_TYPE_INTRUDER, p.getId());
    }

    /**
     * Received message, called when a message had been directly addressed to
     * me!!!
     * 
     * @param m
     */
    @Override
    protected void handleReceivedMessage(ACLMessage m) {
        handleReceivedMessageGeneric(m, reasoner, getSensorWorld(), false);
    }

    /**
     * Generic handling of the received value for a reasoner - to be used by the
     * node and the sink agents
     * 
     * 
     * @param m
     * @param reasoner
     * @param world
     */
    public static void handleReceivedMessageGeneric(ACLMessage m,
            IveReasoner reasoner, SensorNetworkWorld world,
            boolean overheardOnly) {
        String content = (String) m.getValue(FIELD_CONTENT);
        if (content.equals(MESSAGE_DATA)) {
            // handle incoming data
            double observationTime = (Double) m.getValue(FIELD_INTRUDER_TIME);
            String intruderName = (String) m.getValue(FIELD_INTRUDER_NAME);
            Location intruderLocation =
                    (Location) m.getValue(FIELD_INTRUDER_LOCATION);
            String originalReporter =
                    (String) m.getValue(FIELD_INTRUDER_OBSERVER);
            String interestName = (String) m.getValue(FIELD_TYPE);
            int perceptionId = (Integer) m.getValue(FIELD_PERCEPTION_ID);
            reasoner.intruderReported(world.getTime(), observationTime,
                    intruderName, intruderLocation, interestName, perceptionId,
                    false, originalReporter, m.getSender(), "Sink");
            return;
        }
        TextUi.println("Unhandled received message:" + m);
        System.exit(1);

    }

    /**
     * 
     * Processes a report, decides where to send etc.
     * 
     * @param report
     */
    private void sendReport(IveReportModel report) {
        String nextHop = getHopsToSink().get(0);
        // forward the message to the selected hop
        reasoner.reportSent(getSensorWorld().getTime(), report, nextHop);
        ACLMessage m =
                IveMessageHelper
                        .createReportMessage(getName(), nextHop, report);
        transmit(m);
    }

    /**
     * Overheard message
     */
    @Override
    protected void handleOverheardMessage(ACLMessage message) {
         // is this making it long???
         // handleReceivedMessageGeneric(message, reasoner, getSensorWorld(), true);
    }

    @Override
    public String toString() {
        Formatter fmt = new Formatter();
        fmt.add("IveAgent");
        fmt.indent();
        fmt.add(toStringPaths());
        fmt.add(toStringCommonProperties());
        fmt.add(localWorldModel.toString());
        return fmt.toString();
    }

}
