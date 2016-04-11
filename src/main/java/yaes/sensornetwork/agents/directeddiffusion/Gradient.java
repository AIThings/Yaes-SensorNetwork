package yaes.sensornetwork.agents.directeddiffusion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Gradient implements Serializable {
	/**
     * 
     */
	private static final long serialVersionUID = 1087848972612151096L;
	private double duration;
	/**
	 * The list of full path of which the gradient is part of, used by advanced
	 * components
	 */
	private List<List<String>> fullPaths = new ArrayList<List<String>>();
	private double interval;
	// specifies whether the gradient is active, that is it is being forwarded
	// to
	private boolean isActive = true;
	private double lastEmptyMessageSent = 0;
	private double lastMessageSent = 0;
	private final String sensorName;

	public Gradient(String sensorName, double interval, double duration) {
		super();
		this.sensorName = sensorName;
		this.interval = interval;
		this.duration = duration;
	}

	public void addFullPath(List<String> fullPath) {
		fullPaths.add(fullPath);
	}

	public double getDuration() {
		return duration;
	}

	public double getInterval() {
		return interval;
	}

	public double getLastEmptyMessageSent() {
		return lastEmptyMessageSent;
	}

	public double getLastMessageSent() {
		return lastMessageSent;
	}

	public String getSensorName() {
		return sensorName;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public void setLastEmptyMessageSent(double lastEmptyMessageSent) {
		this.lastEmptyMessageSent = lastEmptyMessageSent;
	}

	/**
	 * This one also updates the last empty message sent.
	 * 
	 * @param lastMessageSent
	 */
	public void setLastMessageSent(double lastMessageSent) {
		this.lastMessageSent = lastMessageSent;
		this.lastEmptyMessageSent = lastMessageSent;
	}

	@Override
	public String toString() {
		return "Gradient to " + sensorName + " interval:" + interval
				+ "  duration: " + duration + "  last sent: " + lastMessageSent
				+ " last empty: " + lastEmptyMessageSent;
	}

	public void update(double interval, double duration) {
		this.interval = interval;
		this.duration = duration;
	}
}
