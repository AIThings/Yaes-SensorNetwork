package yaes.sensornetwork.model;

import java.io.Serializable;

import yaes.framework.agent.ACLMessage;
import yaes.world.physical.location.INamedMoving;

public class Perception implements Serializable {
	public enum PerceptionType {
		SinkNodePrescene, IntruderPresence, NoPerception, Overhearing, ReceivedMessage
	}

	// counter for the unique identifier of the perceptions
	private static int idCounter = 0;;

	private static final long serialVersionUID = -8997162617790384362L;
	private int id;
	private ACLMessage message;
	private INamedMoving movingObject;
	private final double time;
	private final PerceptionType type;

	/**
	 * Used mainly to create a message perception
	 * 
	 * @param type
	 * @param message
	 */
	public Perception(PerceptionType type, ACLMessage message, double time) {
		this.type = type;
		this.message = message;
		this.time = time;
		this.id = Perception.idCounter++;
	}

	/**
	 * This is for the other one
	 * 
	 * @param type
	 * @param time
	 */
	public Perception(PerceptionType type, double time) {
		this.type = type;
		this.time = time;
		this.id = Perception.idCounter++;
	}

	/**
	 * This is for actuator perception
	 * 
	 * @param type
	 * @param time
	 */
	public Perception(PerceptionType type, double time,
			INamedMoving movingObject) {
		this.type = type;
		this.time = time;
		this.movingObject = movingObject;
		this.id = Perception.idCounter++;
	}

	public int getId() {
		return id;
	}

	public ACLMessage getMessage() {
		return message;
	}

	/**
	 * @return the movingObject
	 */
	public INamedMoving getMovingObject() {
		return movingObject;
	}

	public double getTime() {
		return time;
	}

	public PerceptionType getType() {
		return type;
	}

	@Override
	public String toString() {
		final StringBuffer buff = new StringBuffer("Perception: " + type
				+ " at: " + time + "\n");
		return buff.toString();
	}
}
