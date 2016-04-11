/**
 * 
 */
package yaes.sensornetwork.agents.directeddiffusion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * 
 *         A set of helper functions related to the interest
 * 
 */
public class InterestHelper {

	/**
	 * Finds the most trustworthy nodes related to an interest and returns a
	 * sorted list, with the most trustworthy ones at the beginning.
	 * 
	 * Here we are just adding them in the order of the first reporting, but
	 * later we can score them based on how much they loose and so on
	 * 
	 * @return
	 */

	public static List<String> findTrustworthyNodes(Interest interest) {
		List<String> trustworthyNodes = new ArrayList<>();
		Map<Integer, List<String>> processedPerceptions = interest
				.getProcessedPerceptions();
		for (List<String> reporters : processedPerceptions.values()) {
			for (String reporter : reporters) {
				if (!trustworthyNodes.contains(reporter)) {
					trustworthyNodes.add(reporter);
				}

			}
		}
		return trustworthyNodes;
	}

	public static void processTimeForInterests(
			HashMap<String, DDSensorInterestManager> interestManagers) {
		for (DDSensorInterestManager sim : interestManagers.values()) {
			sim.processTime();
		}
	}

	/**
	 * Performs processing based on the passing of time, such us the expiration
	 * of interestManagers and so on.
	 */
	public static void purgeExpiredInterests(
			HashMap<String, DDSensorInterestManager> interestManagers) {
		// purge finished ones
		final ArrayList<DDSensorInterestManager> toDelete = new ArrayList<>();
		for (final DDSensorInterestManager interest : interestManagers.values()) {
			if (interest.getSensorInterestRole().equals(
					DDSensorInterestManager.SensorInterestRole.Expired)) {
				toDelete.add(interest);
			}
		}
		// remove the expired interestManagers
		for (final DDSensorInterestManager interest : toDelete) {
			interestManagers.remove(interest.getInterest().getInterestType());
		}
	}
}