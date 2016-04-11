package yaes.sensornetwork.scenarios.bridgeprotection;

import java.awt.Shape;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.SensorNode;

/**
 * Represents the extent of catastropic event
 * 
 * @author Lotzi Boloni
 * 
 */
public class CatastrophicEvent implements Serializable {

	private static final long serialVersionUID = -6602901373464183249L;
	private List<Shape> shapes = new ArrayList<Shape>();
	private boolean active = false;

	public CatastrophicEvent() {
	}

	/**
	 * Applies the event to all the sensor nodes
	 */
	public void applyEvent(SensorNetworkWorld snw) {
		active = true;
		for (SensorNode sn : snw.getSensorNodes()) {
			for (Shape s : shapes) {
				if (s.contains(sn.getLocation().asPoint())) {
					sn.setEnabled(false);
				}
			}
		}
	}

	/**
	 * @return the shapes
	 */
	public List<Shape> getShapes() {
		return shapes;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
