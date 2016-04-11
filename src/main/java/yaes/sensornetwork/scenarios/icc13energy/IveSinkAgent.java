package yaes.sensornetwork.scenarios.icc13energy;

import java.awt.geom.Rectangle2D;

import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingMessageConstants;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingWorldModel.ItwmType;
import yaes.sensornetwork.model.Perception;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.SinkNode;
import yaes.world.physical.location.Location;

/**
 * A special sink agent for the Ive framework
 * 
 * @author Lotzi Boloni
 * 
 */
public class IveSinkAgent extends AbstractSensorAgent implements
        IntruderTrackingMessageConstants {

    protected IveWorldModel worldModel;
    protected IveReasoner reasoner;
    private Rectangle2D.Double interestRectangle;

    
    /**
     * Creates a sink node with the corresponding sink agent
     * 
     * @param name
     * @param sensorWorld
     * @param transmissionRange
     * @param location
     * @return
     */
    public static SinkNode createSinkNode(String name,
            SensorNetworkWorld sensorWorld, ItwmType type, double transmissionRange,
            Location location, Rectangle2D.Double overallInterestRectangle) {
        final SinkNode sinkNode = new SinkNode();
        sinkNode.setName(name);
        // not quite the right solution
        IveSinkAgent sinkAgent =
                new IveSinkAgent(name, sensorWorld, type, overallInterestRectangle);
        sinkAgent.setTransmissionRange(transmissionRange);
        sinkNode.setAgent(sinkAgent);
        sinkAgent.setNode(sinkNode);
        sinkNode.setLocation(location);
        sensorWorld.getDirectory().addAgent(sinkAgent);
        sensorWorld.setSinkNode(sinkNode);
        return sinkNode;
    }


    /**
     * Creates the directed diffusion agent. Takes as parameter the name of the
     * agent, the sensor network world manager and the list of the interest
     * rectangles.
     * 
     * @param name
     * @param sensorWorld
     */
    public IveSinkAgent(String name, SensorNetworkWorld sensorWorld,  ItwmType type,
            Rectangle2D.Double interestRectangle) {
        super(name, sensorWorld);
        this.interestRectangle = interestRectangle;
        worldModel = new IveWorldModel(type, name, sensorWorld);
        reasoner =
                new IveReasoner(worldModel, sensorWorld.getSimulationOutput());
    }

    /**
     * @return the interestRectangle
     */
    public Rectangle2D.Double getInterestRectangle() {
        return interestRectangle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * yaes.world.sensornetwork.AbstractSensorAgent#handleIntruderPresence(yaes
     * .world.sensornetwork.Perception)
     */
    @Override
    protected void handleIntruderPresence(Perception p) {
        // doesn't handle intruder presence directly
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * yaes.world.sensornetwork.AbstractSensorAgent#handleOverheardMessage(yaes
     * .framework.agent.ACLMessage)
     */
    @Override
    protected void handleOverheardMessage(ACLMessage message) {
        // doesn't handle overheard messages
    }

    @Override
    protected void handleReceivedMessage(final ACLMessage m) {
        IveAgent.handleReceivedMessageGeneric(m, reasoner, getSensorWorld(), false);
    }


    /**
     * @return the worldModel
     */
    public IveWorldModel getWorldModel() {
        return worldModel;
    }


    /**
     * @return the reasoner
     */
    public IveReasoner getReasoner() {
        return reasoner;
    }

}
