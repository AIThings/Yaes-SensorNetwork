package yaes.sensornetwork.model;

import java.util.Observable;

import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.ui.text.TextUi;
import yaes.world.physical.location.INamedMoving;
import yaes.world.physical.location.Location;

public class SensorNode extends Observable implements INamedMoving {
	private static final long serialVersionUID = -4593944288349274132L;
	private AbstractSensorAgent agent;
	private Location location;
	private String name;
	private boolean enabled = true;

	public AbstractSensorAgent getAgent() {
		return agent;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setAgent(AbstractSensorAgent agent) {
		this.agent = agent;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void setLocation(Location location) {
		this.location = location;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * toString
	 */
	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("SensorNode " + getName()
				+ " at " + location.toString());
		if (agent == null) {
			buffer.append("\nNo controlling agent");
		} else {
			buffer.append("\n" + agent.toString());
		}
		return buffer.toString();
	}

	public void update() {
		if (agent != null) {
			agent.action();
		} else {
			TextUi.println("This node " + name
					+ " does not have a controlling agent");
		}
		setChanged();
		notifyObservers();
	}

	@Override
	public boolean equals(Object arg0) {
		if(arg0 instanceof SensorNode)
			return this.getName().equals(((SensorNode)arg0).getName());
		else
			return super.equals(arg0);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
	

}
