package yaes.sensornetwork.visualization;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.List;

import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.applications.intrudertracking.IntruderNode;
import yaes.sensornetwork.model.Perception;
import yaes.sensornetwork.model.SensorNetworkMessageConstants;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.SensorNode;
import yaes.sensornetwork.model.SinkNode;
import yaes.ui.visualization.ILayers;
import yaes.ui.visualization.VisualCanvas;
import yaes.ui.visualization.VisualizationProperties;
import yaes.ui.visualization.painters.IPainter;
import yaes.ui.visualization.painters.PainterHelper;

/**
 * Paints the communication and sensing acts of the nodes
 * 
 * @author Lotzi Boloni <lboloni@eecs.ucf.edu>
 * 
 */
public class SensorNetworkWorldPainter implements IPainter,
		SensorNetworkMessageConstants {

	/**
     * 
     */
	private static final String PARAMETER_SECTION_NAME = "Sensor network";
	protected String vpropDataMessageColor = "vpropDataMessageColor";
	protected String vpropDataMessageThickness = "vpropDataMessageThickness";
	protected String vpropIntruderOverhearingColor = "vpropIntruderOverhearingColor";
	protected String vpropPaintIntruderPerceptions = "vpropPaintIntruderPerceptions";
	protected String vpropOverhearingColor = "vpropOverhearingColor";
	protected String vpropOverhearingThickness = "vpropOverhearingThickness";
	protected String vpropPaintIntruderPresence = "vpropPaintIntruderPresence";
	protected String vpropPaintMessages = "vpropPaintMessages";
	protected String vpropPaintOverhearing = "vpropPaintOverhearing";
	protected String vpropSecondsToConsider = "vpropSecondsToConsider";

	public SensorNetworkWorldPainter() {
	}

	/**
	 * /** The layer is Background + 1
	 */
	@Override
	public int getLayer() {
		return ILayers.BACKGROUND_LAYER - 1;
	}

	/**
	 * Returns a different color for different messages depending on the content
	 * of the message
	 * 
	 * @param m
	 * @return
	 */
	private Color getMessageColor(ACLMessage m, VisualizationProperties vprops) {
		Color messageColor = Color.BLACK;
		String content = (String) m
				.getValue(SensorNetworkMessageConstants.FIELD_CONTENT);
		if (content.equals(SensorNetworkMessageConstants.MESSAGE_INTEREST)) {
			messageColor = Color.cyan.darker().darker();
		}
		if (content.equals(SensorNetworkMessageConstants.MESSAGE_DATA)) {
			int intensity = (Integer) m
					.getValue(SensorNetworkMessageConstants.FIELD_INTENSITY);

			if (intensity == 0) { // for no perception messages
				messageColor = Color.black;
			} else {
				messageColor = (Color) vprops
						.getPropertyValue(vpropDataMessageColor);
			}
		}
		return messageColor;
	}

	/**
	 * Painting: basically, paint the perceptions for everybody
	 */
	@Override
	public void paint(Graphics2D g, Object o, VisualCanvas panel) {
		VisualizationProperties vprops = panel.getVisualizer()
				.getVisualizationProperties();

		final SensorNetworkWorld sensorWorld = (SensorNetworkWorld) o;
		for (final SensorNode sensorNode : sensorWorld.getSensorNodes()) {
			paintSensorPerceptions(g, panel, sensorNode, sensorWorld);
		}
		final SinkNode sinkNode = sensorWorld.getSinkNode();
		paintSensorPerceptions(g, panel, sinkNode, sensorWorld);

		if (vprops.getPropertyValue(vpropPaintIntruderPerceptions).equals(
				Boolean.TRUE)) {
			for (final IntruderNode intruderNode : sensorWorld
					.getIntruderNodes()) {
				paintIntruderPerceptions(g, panel, intruderNode, sensorWorld);
			}
		}

	}

	/**
	 * Paints the perceptions of the actuator
	 * 
	 * @param g
	 * @param node
	 * @param sensorWorld
	 */
	private void paintIntruderPerceptions(Graphics2D g, VisualCanvas panel,
			IntruderNode node, SensorNetworkWorld sensorWorld) {
		VisualizationProperties vprops = panel.getVisualizer()
				.getVisualizationProperties();
		final Point2D.Double point = node.getLocation().getRoundedLocation()
				.asPoint();
		panel.getTheTransform().transform(point, point);
		final List<Perception> perceptions = sensorWorld
				.getSensingHistory(node).inspectLastPerceptions(
						(Integer) vprops
								.getPropertyValue(vpropSecondsToConsider));
		for (final Perception p : perceptions) {
			switch (p.getType()) {
			case ReceivedMessage: {
				throw new Error(
						"Intruder received a message, it should not happen!!!");
			}
			case Overhearing: {
				final ACLMessage m = p.getMessage();
				final SensorNode sender = (SensorNode) m
						.getValue(SensorNetworkWorld.HIDDEN_FIELD_SENDER_NODE);
				double distance = node.getLocation().distanceTo(
						sender.getLocation());
				double transmissionRange = sender.getAgent()
						.getTransmissionRange();
				float transparency = (float) (0.2 * (transmissionRange - distance) / transmissionRange);
				PainterHelper.paintRange(node.getLocation(), transmissionRange,
						null, Color.red, transparency, g, panel);
				break;
			}
			case IntruderPresence:
			case NoPerception:
			case SinkNodePrescene:
			default:
				break;
			}
		}
	}

	/**
	 * Paints the perceptions
	 * 
	 * @param g
	 * @param agent
	 */
	protected void paintSensorPerceptions(Graphics2D g, VisualCanvas panel,
			SensorNode node, SensorNetworkWorld sensorWorld) {
		VisualizationProperties vprops = panel.getVisualizer()
				.getVisualizationProperties();
		final List<Perception> perceptions = sensorWorld
				.getSensingHistory(node).inspectLastPerceptions(
						(Integer) vprops
								.getPropertyValue(vpropSecondsToConsider));
		for (final Perception p : perceptions) {
			switch (p.getType()) {
			case IntruderPresence: {
				if (vprops.getPropertyValue(vpropPaintIntruderPresence).equals(
						Boolean.TRUE)) {
					PainterHelper.paintRange(node.getLocation(), node
							.getAgent().getSensorRange(), null, Color.blue,
							0.1f, g, panel);
				}
				break;
			}
			case ReceivedMessage: {
				if (vprops.getPropertyValue(vpropPaintMessages).equals(
						Boolean.TRUE)) {
					final ACLMessage m = p.getMessage();
					final SensorNode sender = (SensorNode) m
							.getValue(SensorNetworkWorld.HIDDEN_FIELD_SENDER_NODE);
					PainterHelper
							.paintArrow(
									sender.getLocation(),
									node.getLocation(),
									getMessageColor(m, vprops),
									g,
									panel,
									(Double) vprops
											.getPropertyValue(vpropDataMessageThickness));
				}
				break;
			}
			case Overhearing: {
				if (vprops.getPropertyValue(vpropPaintOverhearing).equals(
						Boolean.TRUE)) {
					final ACLMessage m = p.getMessage();
					final SensorNode sender = (SensorNode) m
							.getValue(SensorNetworkWorld.HIDDEN_FIELD_SENDER_NODE);
					PainterHelper
							.paintArrow(
									sender.getLocation(),
									node.getLocation(),
									(Color) vprops
											.getPropertyValue(vpropOverhearingColor),
									g,
									panel,
									(Double) vprops
											.getPropertyValue(vpropOverhearingThickness));
				}
			}
				break;
			case NoPerception:
			case SinkNodePrescene:
			default:
				break;
			}
		}
	}

	@Override
	public void registerParameters(
			VisualizationProperties visualizationProperties) {
		visualizationProperties.addVisualizationProperty(
				vpropSecondsToConsider, "Percepsions painted for ... seconds",
				PARAMETER_SECTION_NAME, new Integer(1));
		visualizationProperties.addVisualizationProperty(vpropOverhearingColor,
				"Overhearing color", PARAMETER_SECTION_NAME, Color.red);
		visualizationProperties.addVisualizationProperty(
				vpropOverhearingThickness, "Overhearing thickness",
				PARAMETER_SECTION_NAME, new Double(0.5));
		visualizationProperties.addVisualizationProperty(
				vpropPaintIntruderPerceptions,
				"Paint message overhearing by intruders?",
				PARAMETER_SECTION_NAME, new Boolean(false));
		visualizationProperties.addVisualizationProperty(
				vpropIntruderOverhearingColor, "Intruder overhearing color",
				PARAMETER_SECTION_NAME, Color.red);
		visualizationProperties.addVisualizationProperty(vpropDataMessageColor,
				"Data message color", PARAMETER_SECTION_NAME, Color.red);
		visualizationProperties.addVisualizationProperty(
				vpropDataMessageThickness, "Data message thickness",
				PARAMETER_SECTION_NAME, new Double(3.0));
		visualizationProperties.addVisualizationProperty(vpropPaintMessages,
				"Paint messages?", PARAMETER_SECTION_NAME, new Boolean(true));
		visualizationProperties.addVisualizationProperty(vpropPaintOverhearing,
				"Paint overhearing?", PARAMETER_SECTION_NAME,
				new Boolean(false));
		visualizationProperties.addVisualizationProperty(
				vpropPaintIntruderPresence, "Paint intruder presence?",
				PARAMETER_SECTION_NAME, new Boolean(false));
	}

}
