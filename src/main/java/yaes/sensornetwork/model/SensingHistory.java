package yaes.sensornetwork.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import yaes.world.World;

/**
 * -The list of perceptions -Who sensed me -Whom did I sense -this kind of
 * stuff.
 * 
 * @author Lotzi Boloni
 * 
 */
public class SensingHistory implements Serializable {
	/**
     * 
     */
	private static final long serialVersionUID = 5037429387258835927L;
	/**
	 * If true, keeps a list of all the perceptions of the
	 */
	private final boolean keepFullHistory = true;
	private List<Perception> mailbox;
	private final List<Perception> perceptionHistory;
	private final World world;

	public SensingHistory(World world) {
		this.world = world;
		mailbox = new ArrayList<Perception>();
		perceptionHistory = new ArrayList<Perception>();
	}

	/**
	 * Add a perception
	 */
	public void addPerception(Perception perception) {
		mailbox.add(perception);
		if (keepFullHistory) {
			perceptionHistory.add(perception);
		}
	}

	/**
	 * Returns the list of perceptions and empties the mailbox.
	 * 
	 * @return
	 */
	public synchronized List<Perception> extractNewPerceptions() {
		final List<Perception> toReturn = mailbox;
		mailbox = new ArrayList<Perception>();
		return toReturn;
	}

	/**
	 * Results the list of the perceptions of the last n seconds from the full
	 * history
	 */
	public synchronized List<Perception> inspectLastPerceptions(
			int secondsToConsider) {
		final ArrayList<Perception> lastPerceptions = new ArrayList<Perception>();
		for (int i = perceptionHistory.size() - 1; i >= 0; i--) {
			final Perception p = perceptionHistory.get(i);
			if (world.getTime() - p.getTime() <= secondsToConsider) {
				lastPerceptions.add(p);
			} else {
				break;
			}
		}
		return lastPerceptions;
	}
}
