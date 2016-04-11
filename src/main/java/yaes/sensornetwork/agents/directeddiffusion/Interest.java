package yaes.sensornetwork.agents.directeddiffusion;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import yaes.ui.text.TextUiHelper;
import yaes.world.physical.location.Location;

/**
 * This class manages both the gradients - which are directions towards nodes
 * which are interested - as well as records incoming observations
 */
public class Interest implements Serializable {
	/**
     * 
     */
	private static final long serialVersionUID = -3498887674191638234L;
	private final HashMap<String, Gradient> gradients = new HashMap<String, Gradient>();
	protected final String interestType;
	private double interval = Integer.MAX_VALUE;
	private double maxDuration = 0;
	private final Map<Integer, List<String>> processedPerceptions = new TreeMap<Integer, List<String>>();

	private final Rectangle2D.Double rectangle;

	/**
	 * Creates an interest without gradients, to be used in the sink
	 */
	public Interest(String interestType, Rectangle2D.Double rectangle) {
		this.interestType = interestType;
		this.rectangle = rectangle;
	}

	/**
	 * Creates an interest with the given observation type, and it creates the
	 * first gradient to the specified node
	 * 
	 * @param interestType
	 */
	public Interest(String interestType, String sensorName, int interval,
			int maxDuration, Rectangle2D.Double rectangle) {
		this.interestType = interestType;
		gradients.put(sensorName, new Gradient(sensorName, interval,
				maxDuration));
		this.maxDuration = maxDuration;
		this.interval = interval;
		this.rectangle = rectangle;
	}

	public void enableGradientsToNode(String nodeName, boolean enable) {
		for (Gradient gradient : getGradientCollection()) {
			if (gradient.getSensorName() == nodeName) {
				gradient.setActive(enable);
			}
		}
	}

	/**
	 * Returns an unmodifiable collection of the gradients
	 * 
	 * @return
	 */
	public Collection<Gradient> getGradientCollection() {
		return Collections.unmodifiableCollection(gradients.values());
	}

	public HashMap<String, Gradient> getGradients() {
		return gradients;
	}

	public String getInterestType() {
		return interestType;
	}

	/**
	 * @return the interval
	 */
	public double getInterval() {
		return interval;
	}

	/**
	 * @return the maxDuration
	 */
	public double getMaxDuration() {
		return maxDuration;
	}

	public Map<Integer, List<String>> getProcessedPerceptions() {
		return processedPerceptions;
	}

	public Rectangle2D.Double getRectangle() {
		return rectangle;
	}

	/**
	 * Verifies whether this perception was previously processed
	 * 
	 * @param perceptionId
	 * @return true if the perception has been previously seen
	 */
	public boolean handlePreviouslyProcessedPerception(int perceptionId,
			String sender) {
		List<String> previousReports = processedPerceptions.get(perceptionId);
		if (previousReports != null) {
			previousReports.add(sender);
			return true;
		}
		previousReports = new ArrayList<String>();
		previousReports.add(sender);
		processedPerceptions.put(perceptionId, previousReports);
		return false;
	}

	/**
	 * Returns true if the location is of interest for this interest.
	 * 
	 * @param location
	 * @return
	 */
	public boolean isLocationOfInterest(Location location) {
		final Point2D.Double loc = location.asPoint();
		return rectangle.contains(loc);
	}

	/**
	 * Refreshes a gradient to a neighbor.
	 * 
	 * At the same time, extends the duration (if necessary) and the reduces the
	 * reporting interval (if necessary)
	 * 
	 * @param sensorName
	 * @param interval
	 * @param duration
	 * @return if the interest was changed
	 */
	public boolean refreshGradient(String sensorName, int interval, int duration) {
		boolean retval = false;
		Gradient theGradient = gradients.get(sensorName);
		if (theGradient == null) {
			theGradient = new Gradient(sensorName, interval, duration);
			gradients.put(sensorName, theGradient);
			// retval = false;
		}
		theGradient.update(interval, duration);
		if (this.interval < interval) {
			this.interval = interval;
			retval = true;
		}
		if (duration > maxDuration) {
			maxDuration = duration;
			retval = true;
		}
		return retval;
	}

	/**
	 * Prints out the interest for debugging purposes
	 */
	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("Interest: "
				+ interestType);
		buffer.append("  maxDuration: " + maxDuration + " interval: "
				+ interval);
		for (final Gradient gradient : gradients.values()) {
			buffer.append("\n"
					+ TextUiHelper.indent(gradient.toString()));
		}
		return buffer.toString();
	}

	/**
	 * Discards the expired gradients
	 */
	public void updateInterestBasedOnTime(double time) {
		final List<String> toRemove = new ArrayList<String>();
		for (final Gradient gradient : gradients.values()) {
			if (gradient.getDuration() < time) {
				toRemove.add(gradient.getSensorName());
			}
		}
		for (final String sensorName : toRemove) {
			gradients.remove(sensorName);
		}
		// if anything was removed recalculate maxDuration and smallestInterval
		if (toRemove.size() != 0) {
			maxDuration = 0;
			interval = Integer.MAX_VALUE;
			for (final Gradient gradient : gradients.values()) {
				if (maxDuration < gradient.getDuration()) {
					maxDuration = gradient.getDuration();
				}
				if (interval > gradient.getInterval()) {
					interval = gradient.getInterval();
				}
			}
		}
	}

}
