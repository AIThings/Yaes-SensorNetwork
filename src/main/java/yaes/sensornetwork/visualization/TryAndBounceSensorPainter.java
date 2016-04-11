/**
 * 
 */
package yaes.sensornetwork.visualization;

import java.awt.Graphics2D;
import java.io.Serializable;

import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.model.SensorNode;
import yaes.sensornetwork.model.stealth.StealthySensorNetworkWorld;
import yaes.sensornetwork.scenarios.tryandbounce.TryAndBounceAgent;
import yaes.ui.visualization.VisualCanvas;

/**
 * 
 * Painter for the try and bounce agent.
 * 
 * Overrides the route display to one appropriate for the try and bounce
 * 
 * 
 * @author lboloni
 * 
 */
public class TryAndBounceSensorPainter extends StealthySensorPainter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4800402734112925459L;

	public TryAndBounceSensorPainter(StealthySensorNetworkWorld stealthyWorld) {
		super(stealthyWorld);
	}

	@Override
	public void paintRoutes(SensorNode node, AbstractSensorAgent abstractagent,
			Graphics2D g, VisualCanvas canvas) {
		TryAndBounceAgent agent = (TryAndBounceAgent) abstractagent;
		// paint the interest gradients
		for (String hop : agent.getHopsToSink()) {
			SensorNode receiver = sensorNetworkWorld
					.lookupSensorNodeByName(hop);
			if (receiver == null) { // assume it is the sink
				if (hop.equals(sensorNetworkWorld.getSinkNode().getName())) {
					receiver = sensorNetworkWorld.getSinkNode();
				}
			}
			paintRoute(node, receiver, g, canvas);
		}
	}

}
