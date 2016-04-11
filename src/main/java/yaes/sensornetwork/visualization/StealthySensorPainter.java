package yaes.sensornetwork.visualization;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import yaes.sensornetwork.model.SensorNode;
import yaes.sensornetwork.model.stealth.StealthySensorNetworkWorld;
import yaes.ui.visualization.VisualCanvas;
import yaes.ui.visualization.VisualizationProperties;
import yaes.ui.visualization.painters.PainterHelper;

/**
 * 
 * <code>stealthrouting.gui.StealthySensorPainter</code>
 * 
 * Regular sensor node painter extended with an indicator of stealthyness
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class StealthySensorPainter extends paintSensorNode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6920609207813798717L;

	/**
	 * Paints the stealth level, static to be used from try and bounce etc as
	 * well
	 * 
	 * @param g
	 * @param stealthyWorld
	 * @param node
	 * @param panel
	 */
	public static void paintStealthLevel(Graphics2D g,
			StealthySensorNetworkWorld stealthyWorld, SensorNode node,
			VisualCanvas panel) {
		double stealthLevel = stealthyWorld.getStealthModel(node)
				.getStealthLevel();
		double x = node.getLocation().getX() - 5;
		double y = node.getLocation().getY() - 10;
		double w = 20;
		double h = 5;
		Rectangle2D.Double stealthRectangle = new Rectangle2D.Double(x, y, w, h);
		Rectangle2D.Double stealthRectangleGreen = new Rectangle2D.Double(x, y,
				stealthLevel * w, h);
		Rectangle2D.Double stealthRectangleRed = new Rectangle2D.Double(x
				+ stealthLevel * w, y, (1 - stealthLevel) * w, h);
		PainterHelper.paintRectangle(stealthRectangle, true, Color.black,
				false, null, g, panel);
		PainterHelper.paintRectangle(stealthRectangleGreen, false, null, true,
				Color.green, (float) 0.5, g, panel);
		PainterHelper.paintRectangle(stealthRectangleRed, false, null, true,
				Color.red.darker().darker(), (float) 0.5, g, panel);
	}

	private StealthySensorNetworkWorld stealthyWorld;

	public StealthySensorPainter(StealthySensorNetworkWorld stealthyWorld) {
		super(stealthyWorld);
		this.stealthyWorld = stealthyWorld;
	}

	@Override
	public void paint(Graphics2D g, Object o, VisualCanvas panel) {
		super.paint(g, o, panel);
		final SensorNode node = (SensorNode) o;
		paintStealthLevel(g, stealthyWorld, node, panel);
	}

	@Override
	public void registerParameters(
			VisualizationProperties visualizationProperties) {
		super.registerParameters(visualizationProperties);
		// maybe the stealth indicator on an off here
	}

}
