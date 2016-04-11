package yaes.sensornetwork.agents.directeddiffusion;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;

import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingMessageConstants;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingSinkAgent;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingWorldModel.ItwmType;
import yaes.sensornetwork.model.SensorNetworkMessageConstants;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.SensorNode;
import yaes.sensornetwork.model.SinkNode;
import yaes.ui.text.TextUi;
import yaes.ui.text.TextUiHelper;
import yaes.world.physical.location.Location;

/**
 * This is a directed diffusion sink agent.
 * 
 * It has a list of rectangles in which it is interested in.
 * 
 * It will broadcast interest messages, to the corresponding interest
 * rectangles.
 * 
 * It will reinforce those paths which are the shortest ones for each specific
 * interest.
 * 
 * @author Administrator
 * 
 */
public class DDSinkAgent extends IntruderTrackingSinkAgent implements
        IntruderTrackingMessageConstants {

    /**
     * 
     */
    private static final long serialVersionUID = 7559315912310371217L;

    /**
     * Creates a sink node with the corresponding directed diffusion agent
     * 
     * @param name
     * @param sensorWorld
     * @param transmissionRange
     * @param location
     * @return
     */
    public static SinkNode createSinkNode(String name,
            SensorNetworkWorld sensorWorld, double transmissionRange,
            Location location, Rectangle2D.Double overallInterestRectangle) {
        final SinkNode sinkNode = new SinkNode();
        sinkNode.setName(name);
        // not quite the right solution
        DDSinkAgent sinkAgent =
                new DDSinkAgent(name, sensorWorld, overallInterestRectangle);
        sinkAgent.setTransmissionRange(transmissionRange);
        sinkNode.setAgent(sinkAgent);
        sinkAgent.setNode(sinkNode);
        sinkNode.setLocation(location);
        sensorWorld.getDirectory().addAgent(sinkAgent);
        sensorWorld.setSinkNode(sinkNode);
        return sinkNode;
    }

    private boolean detailedSetup = false;

    private List<Rectangle2D.Double> interestRectangles = new ArrayList<>();

    private final HashMap<String, DDSinkInterestManager> interests =
            new HashMap<>();

    /**
     * Creates the directed diffusion agent. Takes as parameter the name of the
     * agent, the sensor network world manager and the list of the interest
     * rectangles.
     * 
     * @param name
     * @param sensorWorld
     */
    public DDSinkAgent(String name, SensorNetworkWorld sensorWorld,
            Rectangle2D.Double overallInterestRectangle) {
        super(name, sensorWorld, overallInterestRectangle, ItwmType.LAST_KNOWN);
    }

    @Override
    public void afterProcessingPerceptions() {
        if (detailedSetup) {
            for (DDSinkInterestManager si : interests.values()) {
                ((DDPathCreatingSinkInterestManager) si)
                        .attendToInterest(getSensorWorld().getTime());
            }
        }
    }

    /**
     * Returns a human readable label for a specific interest. It will be based
     * on the rectangle.
     * 
     * @param rectangle
     * @return
     */
    private String getInterestLabel(Rectangle2D.Double rectangle) {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        formatter.format("%s_%.0f_%.0f_%.0f_%.0f",
                IntruderTrackingMessageConstants.INTEREST_TYPE_INTRUDER,
                rectangle.getMinX(), rectangle.getMinY(), rectangle.getMaxX(),
                rectangle.getMaxY());
        String retval = sb.toString();
        formatter.close();
        return retval;
    }

    public HashMap<String, DDSinkInterestManager> getInterests() {
        return interests;
    }

    @Override
    protected void handleReceivedMessage(final ACLMessage m) {
        if (m.getValue(SensorNetworkMessageConstants.FIELD_CONTENT).equals(
                SensorNetworkMessageConstants.MESSAGE_DATA)) {
            final String interestType =
                    (String) m
                            .getValue(SensorNetworkMessageConstants.FIELD_TYPE);
            final DDSinkInterestManager interest = interests.get(interestType);
            if (interest == null) {
                TextUi.errorPrint("Sink received a message for an interest it did not express"
                        + interestType);
                System.exit(1);
            }
            interest.processReceivedData(m);
        }
    }

    /**
     * Sets the interest rectangles for this sink agent.
     * 
     * It also creates the gradients in the other agents for this interest. Use
     * this if the sensor nodes are of type DirectedDiffusion
     * 
     * @param interestRectangles
     */
    public void setInterestRectanglesDirectedDiffusion(
            List<Rectangle2D.Double> newInterestRectangles, double interval) {
        interestRectangles.addAll(newInterestRectangles);
        // create the interestManagers from the rectangles passed
        for (Rectangle2D.Double rect : interestRectangles) {
            String label = getInterestLabel(rect);
            DDPathCreatingSinkInterestManager interest =
                    new DDPathCreatingSinkInterestManager(label, rect, this);
            interest.setDuration(100000);
            interest.setInterval(interval);
            interests.put(label, interest);

            List<DirectedDiffusionAgent> agents =
                    new ArrayList<>();
            for (SensorNode s : getSensorWorld().getSensorNodes()) {
                agents.add((DirectedDiffusionAgent) s.getAgent());
            }
            if (!detailedSetup) {
                DDRoutingHelper.createGradientsForInterest(interest, this,
                        agents);
            }
        }
    }

    /**
     * Returns a description of the state of the agent
     */
    @Override
    public String toString() {
        final StringBuffer buffer =
                new StringBuffer("Directed Diffusion Sink \n");
        buffer.append("Interests:");
        for (DDSinkInterestManager interest : interests.values()) {
            buffer.append("\n" + TextUiHelper.indent(interest.toString()));
        }
        buffer.append("\n");
        buffer.append(getWorldModel().toString() + "\n");
        return buffer.toString();
    }
}
