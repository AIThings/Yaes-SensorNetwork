package yaes.sensornetwork.agents;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.List;

import yaes.framework.agent.ACLMessage;
import yaes.framework.agent.AbstractCommunicatingAgent;
import yaes.framework.simulation.SimulationOutput;
import yaes.sensornetwork.energymodel.MessageTransmissionEnergyModel;
import yaes.sensornetwork.energymodel.RapaportCommunicationEnergyModel;
import yaes.sensornetwork.model.Perception;
import yaes.sensornetwork.model.SensingHistory;
import yaes.sensornetwork.model.SensorNetworkMessageConstants;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.SensorNode;
import yaes.sensornetwork.model.constSensorNetworkWorld;
import yaes.ui.format.Formatter;
import yaes.world.physical.location.Location;

/**
 * <code>yaes.world.sensornetwork.AbstractSensorAgent</code>
 * 
 * The basic sensor class. Contains overwritable functions for various events,
 * and support for transmission energy cost.
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public abstract class AbstractSensorAgent extends AbstractCommunicatingAgent
        implements constSensorNetworkWorld, SensorNetworkMessageConstants {

    private static final long serialVersionUID = 2181719193783713982L;
    MessageTransmissionEnergyModel mtem;
    protected SensorNode node;
    private double sensorRange = 20;
    private final SensorNetworkWorld sensorWorld;
    private double transmissionRange = 30;
    protected ISendingDecision sendingDecision;

    /**
     * Creates an abstract sensor agent for transmission scheduling purposes.
     */
    public AbstractSensorAgent(String name, SensorNetworkWorld sensorWorld) {
        super(name, sensorWorld);
        this.sensorWorld = sensorWorld;
    }

    /**
     * Contains the agents behavior
     * 
     * <ul>
     * <li>actuator presence
     * <li>received message of interest
     * <li>received message
     * <li>data
     * </ul>
     * 
     * Also create empty messages if there is nothing to send.
     * 
     */
    @Override
    public void action() {
        beforeProcessingPerceptions();
        // read out his stuff from the sensing manager
        final SensingHistory sensingHistory =
                getSensorWorld().getSensingHistory(getNode());
        final List<Perception> perceptions =
                sensingHistory.extractNewPerceptions();
        for (final Perception p : perceptions) {
            switch (p.getType()) {
            case IntruderPresence:
                handleIntruderPresence(p);
                break;
            case ReceivedMessage:
                handleReceivedMessage(p.getMessage());
                break;
            case Overhearing:
                handleOverheardMessage(p.getMessage());
                break;
			case NoPerception:
			case SinkNodePrescene:
            default:
                handleReceivedMessage(p.getMessage());                	
            }
        }
        afterProcessingPerceptions();
    }

    /**
     * Called after the perceptions are processed. Contains for instance:
     * <ul>
     * <li>transmissions which are not done any instance
     * <li>cleanups
     * </ul>
     * 
     */
    protected void afterProcessingPerceptions() {
    }

    /**
     * Called before the perceptions are processed
     */
    protected void beforeProcessingPerceptions() {
    }

    public SensorNode getNode() {
        return node;
    }

    /**
     * @return the sendingDecision
     */
    public ISendingDecision getSendingDecision() {
        return sendingDecision;
    }

    /**
     * @return the sensorRange
     */
    public double getSensorRange() {
        return sensorRange;
    }

    /**
     * Returns a shape corresponding to the sensor range (normally, an ellipse)
     * 
     * It is a good idea to use this one instead of messing with the range
     * numbers because it allows for the use of different shapes.
     * 
     * @return
     */
    public Shape getSensorRangeShape() {
        Location l = node.getLocation();
        return new Ellipse2D.Double(l.getX() - sensorRange, l.getY()
                - sensorRange, 2 * sensorRange, 2 * sensorRange);
    }

    public SensorNetworkWorld getSensorWorld() {
        return sensorWorld;
    }

    /**
     * @return the transmissionRange
     */
    public double getTransmissionRange() {
        return transmissionRange;
    }

    /**
     * Returns a shape corresponding to the transmission range (normally, a
     * circle)
     * 
     * It is a good idea to use this one instead of messing with the range
     * numbers because it allows for the use of different shapes.
     * 
     * @return
     */
    public Shape getTransmissionRangeShape() {
        Location l = node.getLocation();
        return new Ellipse2D.Double(l.getX() - transmissionRange, l.getY()
                - transmissionRange, 2 * transmissionRange,
                2 * transmissionRange);
    }

    /**
     * Handles the perception of the presence of an intruder
     * 
     * @param iNamedMoving
     */
    protected abstract void handleIntruderPresence(final Perception p);

    /**
     * Handles an overheard message
     * 
     * @param message
     */
    protected abstract void handleOverheardMessage(final ACLMessage message);

    /**
     * Handles an incoming message (typically for forwarding)
     * 
     * @param message
     */
    protected abstract void handleReceivedMessage(final ACLMessage message);

    /**
     * Set the energy parameters
     * 
     * @param communicationEnergyModel
     * @param transmissionEnergyFixedMessageSize
     * @param transmissionEnergyFixedOverhead
     * @param transmissionEnergyExactDistance
     */
    public void setEnergyParameters(
            RapaportCommunicationEnergyModel communicationEnergyModel,
            int transmissionEnergyFixedMessageSize,
            int transmissionEnergyFixedOverhead,
            boolean transmissionEnergyExactDistance) {
        mtem = new MessageTransmissionEnergyModel(this);
        mtem.setCommunicationEnergyModel(communicationEnergyModel);
        mtem.setTransmissionEnergyFixedMessageSize(transmissionEnergyFixedMessageSize);
        mtem.setTransmissionEnergyFixedOverhead(transmissionEnergyFixedOverhead);
        mtem.setTransmissionEnergyExactDistance(transmissionEnergyExactDistance);
        getSensorWorld().getSimulationOutput().createVariable(
                constSensorNetworkWorld.SENSORNETWORK_TRANSMISSION_ENERGY + "_"
                        + getName(), false);
        getSensorWorld().getSimulationOutput().createVariable(
                constSensorNetworkWorld.SENSORNETWORK_TRANSMISSION_ENERGY,
                false);
    }

    public void setNode(SensorNode node) {
        this.node = node;
    }

    /**
     * @param sendingDecision
     *            the sendingDecision to set
     */
    public void setSendingDecision(ISendingDecision sendingDecision) {
        this.sendingDecision = sendingDecision;
    }

    /**
     * @param sensorRange
     *            the sensorRange to set
     */
    public void setSensorRange(double sensorRange) {
        this.sensorRange = sensorRange;
    }

    /**
     * @param transmissionRange
     *            the transmissionRange to set
     */
    public void setTransmissionRange(double transmissionRange) {
        this.transmissionRange = transmissionRange;
    }

    /**
     * Transmit a message, at the same time accounting for energy consumption
     * etc.
     * 
     * @param message
     */
    public void transmit(ACLMessage message) {
        getSensorWorld().transmit(getNode(), message);
        if (mtem != null) {
            double energyCost = mtem.transmissionEnergyCost(message);
            SimulationOutput so = getSensorWorld().getSimulationOutput();
            so.update(constSensorNetworkWorld.SENSORNETWORK_TRANSMISSION_ENERGY
                    + "_" + getName(), energyCost);
            so.update(
                    constSensorNetworkWorld.SENSORNETWORK_TRANSMISSION_ENERGY,
                    energyCost);
        }
    }

    /**
     * The overall toString function for a general purpose sensor node
     */
    @Override
    public String toString() {
        Formatter fmt = new Formatter();
        fmt.add("AbstractSensorAgent");
        fmt.addIndented(toStringCommonProperties());
        return fmt.toString();
    }
    
    /**
     * A function 
     */
    protected String toStringCommonProperties() {
        Formatter fmt = new Formatter();
        fmt.is("Name", getName());
        fmt.is("Location", getNode().getLocation());
        fmt.is("Transmission range", transmissionRange);
        fmt.is("Sensor range", sensorRange);
        fmt.is("Agent class", getClass().getName());
        return fmt.toString();
        
    }

}
