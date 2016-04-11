/**
 * 
 */
package yaes.sensornetwork.scenarios.tryandbounce;

import java.io.Serializable;

import yaes.world.physical.location.Location;

/**
 * 
 * The model of another sensor node
 * 
 * @author lboloni
 * 
 */
public class NodeModel implements Serializable {

	private static final long serialVersionUID = -8235325319379233929L;

	public enum NodeModelState {
		ALIVE, THREATENED, DEAD
	};

	private String name;
	private NodeModelState state;
	private Location location;
	private boolean inHearingRange;
	private double lastHeard;

	public NodeModel(String name) {
		this.name = name;
		this.state = NodeModelState.ALIVE;
	}

	/**
	 * @return the lastHeard
	 */
	public double getLastHeard() {
		return lastHeard;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the state
	 */
	public NodeModelState getState() {
		return state;
	}

	/**
	 * @return the inHearingRange
	 */
	public boolean isInHearingRange() {
		return inHearingRange;
	}

	/**
	 * @param inHearingRange
	 *            the inHearingRange to set
	 */
	public void setInHearingRange(boolean inHearingRange) {
		this.inHearingRange = inHearingRange;
	}

	/**
	 * @param lastHeard
	 *            the lastHeard to set
	 */
	public void setLastHeard(double lastHeard) {
		this.lastHeard = lastHeard;
	}

	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(NodeModelState state) {
		this.state = state;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Node model: [" + state + "] " + name + "\n");
		if (inHearingRange) {
			buffer.append("    in hearing range, last heard at " + lastHeard
					+ "\n");
		} else {
			buffer.append("    not in hearing range");
		}
		if (location != null) {
			buffer.append("   location :" + location + "\n");
		} else {
			buffer.append("   location unknown\n");
		}
		return buffer.toString();
	}
}
