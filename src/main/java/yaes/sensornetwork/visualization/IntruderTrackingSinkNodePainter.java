package yaes.sensornetwork.visualization;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;

import yaes.sensornetwork.agents.directeddiffusion.DDSinkAgent;
import yaes.sensornetwork.agents.directeddiffusion.DDSinkInterestManager;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingSinkAgent;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingWorldModel;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.SensorNode;
import yaes.ui.visualization.ILayers;
import yaes.ui.visualization.VisualCanvas;
import yaes.ui.visualization.VisualizationProperties;
import yaes.ui.visualization.painters.PainterHelper;
import yaes.world.physical.location.Location;

/**
 * This is a painter for the sink node in directed diffusion mode. In addition
 * to painting itself, it also paints the interest rectangle(s).
 * 
 * 
 * @author Lotzi Boloni
 * 
 */
public class IntruderTrackingSinkNodePainter extends paintSensorNode implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3103346312537013185L;
	private static final String vpropDDInterestZoneColor = "vpropDDInterestZoneColor";
	private static final String vpropInterestZoneColor = "vpropInterestZoneColor";
	private static final String vpropPaintDDInterestZone = "vpropPaintDDInterestZone";
	private static final String vpropPaintInterestZone = "vpropPaintInterestZone";

	public IntruderTrackingSinkNodePainter(SensorNetworkWorld sensingManager) {
		super(sensingManager);
		size = 15;
		fillColor = Color.magenta;

	}

	@Override
	public int getLayer() {
		return ILayers.FOREGROUND_LAYER;
	}

	@Override
	public void paint(Graphics2D g, Object o, VisualCanvas panel) {
		super.paint(g, o, panel);
		if (!(o instanceof SensorNode)) {
			return;
		}
		final SensorNode node = (SensorNode) o;
		VisualizationProperties vprops = panel.getVisualizer()
				.getVisualizationProperties();
		if (!(node.getAgent() instanceof IntruderTrackingSinkAgent)) {
			throw new Error(
					"This painter has been erroneously attached to this agent, it should be an IntruderTrackingSinkAgent");
		}
		// For all of them
		IntruderTrackingSinkAgent sinkAgent = (IntruderTrackingSinkAgent) node
				.getAgent();
		paintWorldModel(g, sinkAgent.getWorldModel(), panel);
		if ((Boolean) vprops
				.getPropertyValue(IntruderTrackingSinkNodePainter.vpropPaintInterestZone)) {
			PainterHelper
					.paintRectangle(
							sinkAgent.getInterestRectangle(),
							true,
							Color.black,
							true,
							(Color) vprops
									.getPropertyValue(IntruderTrackingSinkNodePainter.vpropInterestZoneColor),
							(float) 0.5, g, panel);
		}

		// if it is a directed diffusion one:
		if (sinkAgent instanceof DDSinkAgent) {
			DDSinkAgent ddSinkAgent = (DDSinkAgent) node.getAgent();
			if (!((Boolean) vprops
					.getPropertyValue(IntruderTrackingSinkNodePainter.vpropPaintDDInterestZone))) {
				return;
			}
			for (DDSinkInterestManager interest : ddSinkAgent.getInterests()
					.values()) {
				PainterHelper
						.paintRectangle(
								interest.getInterest().getRectangle(),
								true,
								Color.black,
								true,
								(Color) vprops
										.getPropertyValue(IntruderTrackingSinkNodePainter.vpropDDInterestZoneColor),
								(float) 0.5, g, panel);
			}
		}
	}

	/**
	 * Paints the world model, the locations of the models of the nodes
	 * 
	 * @param g
	 * @param worldModel
	 * @param panel
	 */
	private void paintWorldModel(Graphics2D g,
			IntruderTrackingWorldModel worldModel, VisualCanvas panel) {
		// if (panel.getCurrentLayer().equals()) {
		//
		// }
		// int labelDelta = -20;
		for (String targetName : worldModel.getIntruders()) {
			Location location = worldModel.estimateIntruderLocation(targetName);
			PainterHelper.paintRectangleAtLocation(location, 10,
					Color.black, Color.black, g, panel);
			/*
			 * String labelFontName = "Sans"; String text = "<html><font face='"
			 * + labelFontName + "' size=2>" + "Model:" + target.getName() +
			 * "</font></html>"; PainterHelper.paintHtmlLabel(text, target
			 * .getLocation(), labelDelta, labelDelta, Color.white, false, true,
			 * g, panel);
			 */
		}

	}

	/**
	 * Registers the visualization parameters
	 * 
	 * @param visualizationProperties
	 */
	@Override
	public void registerParameters(
			VisualizationProperties visualizationProperties) {
		super.registerParameters(visualizationProperties);
		visualizationProperties.addVisualizationProperty(
				IntruderTrackingSinkNodePainter.vpropPaintInterestZone,
				"Paint interest zone?", "Sensor network", new Boolean(true));
		visualizationProperties.addVisualizationProperty(
				IntruderTrackingSinkNodePainter.vpropInterestZoneColor,
				"Interest zone color?", "Sensor network", new Color(204, 204,
						255));
		visualizationProperties.addVisualizationProperty(
				IntruderTrackingSinkNodePainter.vpropPaintDDInterestZone,
				"Paint the Directed Diffusion interest zone(s)?",
				"Sensor network", new Boolean(true));
		visualizationProperties.addVisualizationProperty(
				IntruderTrackingSinkNodePainter.vpropDDInterestZoneColor,
				"Directed Diffusion interest zone(s) color?", "Sensor network",
				Color.yellow.brighter());
	}
}
