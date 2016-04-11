package yaes.sensornetwork.visualization;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;

import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.SensorNode;
import yaes.sensornetwork.scenarios.bridgeprotection.BpaAgent;
import yaes.ui.visualization.VisualCanvas;
import yaes.ui.visualization.painters.PainterHelper;

/**
 * Visualization for the Bridge Protection Node
 * 
 * Basically a regular sensor node, color coded for the Bridge
 * 
 * @author Lotzi Boloni
 * 
 */
public class paintBPANode extends paintSensorNode implements Serializable {
	private static final long serialVersionUID = -803323147380609839L;

	/**
	 * For deserialization
	 */
	public paintBPANode() {
	}

	public paintBPANode(SensorNetworkWorld sensorNetworkWorld) {
		super(sensorNetworkWorld);
	}

	@Override
	protected void paintNode(Graphics2D g, Object o, VisualCanvas panel) {
		SensorNode node = (SensorNode) o;
		BpaAgent agent = (BpaAgent) node.getAgent();
		if (node.isEnabled()) {
			switch (agent.getBpaState()) {
			case NORMAL: {
				// black border, white interior
				PainterHelper.paintRectangleAtLocation(node.getLocation(), 10,
						Color.black, Color.white, g, panel);
				break;
			}
			case BRIDGE: {
				// black center, margin
				PainterHelper.paintRectangleAtLocation(node.getLocation(), 20,
						Color.black, Color.black, g, panel);
				//PainterHelper.paintRectangleAtLocation(node.getLocation(), 10,
				//		Color.white, Color.white, g, panel);
				break;
			}
			case FARSIDE: {
				// lightgray interior
				PainterHelper.paintRectangleAtLocation(node.getLocation(), 10,
						Color.black, Color.lightGray, g, panel);
				break;
			}
			case GATE: {
				PainterHelper.paintRectangleAtLocation(node.getLocation(), 20,
						Color.black, Color.black, g, panel);
				PainterHelper.paintRectangleAtLocation(node.getLocation(), 10,
						Color.lightGray, Color.lightGray, g, panel);
				break;
			}
			case FANOUT: {
				PainterHelper.paintRectangleAtLocation(node.getLocation(), 20,
						Color.black, Color.black, g, panel);
				PainterHelper.paintRectangleAtLocation(node.getLocation(), 10,
						Color.white, Color.white, g, panel);
				break;
			}
			case NEARSIDE: {
				// like normal
				PainterHelper.paintRectangleAtLocation(node.getLocation(), 10,
						Color.black, Color.white, g, panel);
				break;
			}
			default:
				break;
			}
		} else {
			// node is not enabled
			PainterHelper.paintRectangleAtLocation(node.getLocation(), 4,
					Color.black, Color.black, g, panel);
		}
	}

	
	/**
	 * Paints the routes. 
	 * 
	 * Changes the rules for the BridgeNode
	 * 
	 * @param node
	 * @param abstractagent
	 * @param g
	 * @param canvas
	 */
	@Override
	public void paintRoutes(SensorNode node, AbstractSensorAgent abstractagent,
			Graphics2D g, VisualCanvas canvas) {
		if (!node.isEnabled())
			return;
		BpaAgent agent = (BpaAgent) abstractagent;
		switch (agent.getBpaState()) {
		case BRIDGE: {
			for(String fanout: agent.getFanoutNodes()) {
				for(SensorNode candidate: sensorNetworkWorld.getSensorNodes()) {
					if (candidate.getName().equals(fanout)) {						
						paintRoute(agent.getNode(), candidate, g, canvas);
						break;
					}
				}
			}
			break;
		}
		case FANOUT:
		case FARSIDE:
		case GATE:
		case NEARSIDE:
		case NORMAL:
		default: {
			super.paintRoutes(node, abstractagent, g, canvas);
			break;
		}
		}
	}
	
}
