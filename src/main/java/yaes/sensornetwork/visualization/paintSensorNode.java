package yaes.sensornetwork.visualization;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;

import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.agents.ForwarderSensorAgent;
import yaes.sensornetwork.agents.directeddiffusion.DirectedDiffusionAgent;
import yaes.sensornetwork.agents.directeddiffusion.Gradient;
import yaes.sensornetwork.agents.directeddiffusion.Interest;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingSimpleSensorAgent;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.SensorNode;
import yaes.ui.visualization.VisualCanvas;
import yaes.ui.visualization.VisualizationProperties;
import yaes.ui.visualization.painters.PainterHelper;
import yaes.ui.visualization.painters.paintMobileNode;
import yaes.world.physical.location.INamedMoving;
import yaes.world.physical.location.Location;

/**
 * This is a painter for the sensor nodes which are static.
 * 
 * 
 * @author Administrator
 * 
 */
public class paintSensorNode extends paintMobileNode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7182496052635144332L;
	public static final String vpropPaintRoutes = "vpropPaintRoutes";
	public static final String vpropRoutesColor = "vpropRoutesColor";
	public static final String vpropRoutesThickness = "vpropRoutesThickness";
	protected SensorNetworkWorld sensorNetworkWorld;
	public int sensorNodeSize = 4;
	public static final String vpropPaintSensorRange = "vpropPaintSensorRange";
	public static final String vpropPaintTransmissionRange = "vpropPaintTransmissionRange";
	public static final String vpropSensorPaintLabel = "vpropSensorPaintLabel";
	public static final String vpropSensorRangeColor = "vpropSensorRangeColor";
	public static final String vpropTransmissionRangeColor = "vpropTransmissionRangeColor";

	public paintSensorNode() {
	}

	@Override
	public int getLayer() {
		return ALL_LAYERS;
	}

	public paintSensorNode(SensorNetworkWorld sensorNetworkWorld) {
		this.sensorNetworkWorld = sensorNetworkWorld;
	}

	@Override
	public void paint(Graphics2D g, Object o, VisualCanvas panel) {
		VisualizationProperties vprops = panel.getVisualizer()
				.getVisualizationProperties();
		if (!(o instanceof SensorNode)) {
			throw new Error("This is supposed to handle a SensorNode!!!");
		}
		final SensorNode node = (SensorNode) o;
		final AbstractSensorAgent agent = node.getAgent();
		// let us call the upper one

		if (panel.getCurrentLayer() == FOREGROUND_LAYER) {
			paintLabel = (Boolean) vprops
					.getPropertyValue(vpropSensorPaintLabel);
			super.paint(g, o, panel);
			if ((Boolean) vprops.getPropertyValue(vpropPaintSensorRange)) {
				PainterHelper.paintRange(node.getLocation(),
						agent.getSensorRange(),
						(Color) vprops.getPropertyValue(vpropSensorRangeColor),
						null, 0f, g, panel);
			}
			if ((Boolean) vprops.getPropertyValue(vpropPaintTransmissionRange)) {
				PainterHelper.paintRange(node.getLocation(),
						agent.getTransmissionRange(), null,
						(Color) vprops.getPropertyValue(vpropSensorRangeColor),
						0.2f, g, panel);
			}
		}
		// routes go to the communication events layer
		if (panel.getCurrentLayer() == COMMUNICATION_LINKS_LAYER) {
			if ((Boolean) vprops
					.getPropertyValue(paintSensorNode.vpropPaintRoutes)) {
				paintRoutes(node, agent, g, panel);
			}
		}
		// painting the selection
		SelectionHelper.paintSelection(g, o, panel, node.getLocation(),
				2 * sensorNodeSize + 2);

	}

	/**
	 * Paints one route
	 * 
	 * @param from
	 * @param to
	 * @param g
	 * @param canvas
	 */
	public void paintRoute(INamedMoving from, INamedMoving to, Graphics2D g,
			VisualCanvas canvas) {
		VisualizationProperties vprops = canvas.getVisualizer()
				.getVisualizationProperties();
		Location shiftedStart = new Location(from.getLocation().getX() + 3,
				from.getLocation().getY() + 3);
		Location shiftedEnd = new Location(to.getLocation().getX() + 3, to
				.getLocation().getY() + 3);
		PainterHelper
				.paintMiddleArrow(
						shiftedStart,
						shiftedEnd,
						(Color) vprops
								.getPropertyValue(paintSensorNode.vpropRoutesColor),
						g,
						canvas,
						(Double) vprops
								.getPropertyValue(paintSensorNode.vpropRoutesThickness));
	}

	/**
	 * Paints the routes. There is support here for the agents which are in this
	 * world. Override this one for your own agent.
	 * 
	 * @param node
	 * @param abstractagent
	 * @param g
	 * @param canvas
	 */
	public void paintRoutes(SensorNode node, AbstractSensorAgent abstractagent,
			Graphics2D g, VisualCanvas canvas) {
		if (!node.isEnabled())
			return;
		// special handling for directed diffusion
		if (abstractagent instanceof DirectedDiffusionAgent) {
			paintRoutesDDGradients(node,
					(DirectedDiffusionAgent) abstractagent, g, canvas);
		}
		if (abstractagent instanceof IntruderTrackingSimpleSensorAgent) {
			paintRoutesForwarderSensorAgent(node,
					(ForwarderSensorAgent) abstractagent, g, canvas);
		}
	}

	/**
	 * Paints the routes for directed diffusion
	 * 
	 * @param node
	 * @param abstractagent
	 * @param g
	 * @param canvas
	 */
	public void paintRoutesDDGradients(SensorNode node,
			DirectedDiffusionAgent agent, Graphics2D g, VisualCanvas canvas) {
		// paint the interest gradients
		for (String interestType : agent.getSensorInterestManagers().keySet()) {
			Interest interest = agent.getSensorInterestManagers()
					.get(interestType).getInterest();
			for (Gradient gradient : interest.getGradientCollection()) {
				SensorNode receiver = sensorNetworkWorld
						.lookupSensorNodeByName(gradient.getSensorName());
				if (receiver == null) { // assume it is the sink
					if (gradient.getSensorName().equals(
							sensorNetworkWorld.getSinkNode().getName())) {
						receiver = sensorNetworkWorld.getSinkNode();
					}
				}
				paintRoute(node, receiver, g, canvas);
			}
		}
	}

	/**
	 * @param node
	 * @param abstractagent
	 * @param g
	 * @param canvas
	 */
	private void paintRoutesForwarderSensorAgent(SensorNode node,
			ForwarderSensorAgent agent, Graphics2D g, VisualCanvas canvas) {
		SensorNode receiver = sensorNetworkWorld.lookupSensorNodeByName(agent
				.getForwardingDestination());
		if (receiver == null) { // assume it is the sink
			if (agent.getForwardingDestination().equals(
					sensorNetworkWorld.getSinkNode().getName())) {
				receiver = sensorNetworkWorld.getSinkNode();
			}
		}
		paintRoute(node, receiver, g, canvas);
	}

	@Override
	public void registerParameters(
			VisualizationProperties visualizationProperties) {
		super.registerParameters(visualizationProperties);
		visualizationProperties.addVisualizationProperty(vpropPaintSensorRange,
				"Paint sensor range?", "Sensor network", new Boolean(false));
		visualizationProperties.addVisualizationProperty(
				vpropPaintTransmissionRange, "Paint transmission range?",
				"Sensor network", new Boolean(false));
		visualizationProperties.addVisualizationProperty(vpropSensorRangeColor,
				"Sensor range color", "Sensor network", Color.red);
		visualizationProperties.addVisualizationProperty(
				vpropTransmissionRangeColor, "Transmission range color?",
				"Sensor network", Color.blue);
		visualizationProperties.addVisualizationProperty(
				paintSensorNode.vpropPaintRoutes, "Paint routes?",
				"Sensor network", new Boolean(false));
		visualizationProperties.addVisualizationProperty(
				paintSensorNode.vpropRoutesColor, "Routes color?",
				"Sensor network", Color.green.darker().darker());
		visualizationProperties.addVisualizationProperty(
				paintSensorNode.vpropRoutesThickness, "Routes thickness?",
				"Sensor network", new Double(1.0));
		visualizationProperties.addVisualizationProperty(vpropSensorPaintLabel,
				"Paint sensor label?", "Sensor network", new Boolean(false));
	}

}
