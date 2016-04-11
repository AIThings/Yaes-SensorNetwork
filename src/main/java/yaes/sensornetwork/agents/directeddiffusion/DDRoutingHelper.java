package yaes.sensornetwork.agents.directeddiffusion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.agents.SensorRoutingHelper;
import yaes.sensornetwork.agents.directeddiffusion.DDSensorInterestManager.SensorInterestRole;

/**
 * 
 * 
 * <code>stealthrouting.agents.directeddiffusion.DDRoutingHelper</code>
 * 
 * This helper code creates the directed diffusion gradients in a single shot,
 * without the propagation of the interests, reinforcement etc. It is a good
 * choice when you just need the framework.
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class DDRoutingHelper {

	/**
	 * Creates the paths to the sink for one specific interest rectangle for a
	 * specific set of nodes
	 */
	public static void createGradientsForInterest(
			DDSinkInterestManager originalInterest, DDSinkAgent sinkAgent,
			List<DirectedDiffusionAgent> sensorAgents) {
		// identify the sensors which will serve as sources
		List<DirectedDiffusionAgent> sources = new ArrayList<>();
		for (DirectedDiffusionAgent sensorAgent : sensorAgents) {
			if (sensorAgent.getSensorRangeShape().intersects(
					originalInterest.getInterest().getRectangle())) {
				sources.add(sensorAgent);
			}
		}
		// for each of the sources, calculate the paths
		for (DirectedDiffusionAgent source : sources) {
			DDRoutingHelper.createPathsToSink(originalInterest, source,
					sinkAgent, sensorAgents);
		}
	}

	/**
	 * 
	 * Creates the path(s) to the sink for a certain source agent
	 * 
	 * @param originalInterest
	 * @param source
	 * @param sinkAgent
	 * @param sensorAgents
	 */
	private static void createPathsToSink(
			DDSinkInterestManager originalInterest,
			DirectedDiffusionAgent source, DDSinkAgent sinkAgent,
			List<DirectedDiffusionAgent> sensorAgents) {
		List<AbstractSensorAgent> abstractagents = new ArrayList<AbstractSensorAgent>(
				sensorAgents);
		abstractagents.add(sinkAgent);
		List<String> path = SensorRoutingHelper.getShortestPath(abstractagents,
				source, sinkAgent);
		// now create the interests in all the agents along the path
		for (int i = 0; i != path.size(); i++) {
			// the sink
			if (i == path.size() - 1) {
				// nothing here
				continue;
			}
			String nextHop = path.get(i + 1);
			DirectedDiffusionAgent current = null;
			for (DirectedDiffusionAgent dda : sensorAgents) {
				if (dda.getName().equals(path.get(i))) {
					current = dda;
					break;
				}
			}
			HashMap<String, DDSensorInterestManager> interestManagers = current
					.getSensorInterestManagers();
			DDSensorInterestManager sim = interestManagers.get(originalInterest
					.getInterest().getInterestType());
			if (sim == null) {
				// creates the interest and the first route
				sim = new DDSensorInterestManager(current, current
						.getSensorWorld());
				Interest interest = new Interest(originalInterest.getInterest()
						.getInterestType(), originalInterest.getInterest()
						.getRectangle());
				sim.setInterest(interest);
				interestManagers.put(originalInterest.getInterest()
						.getInterestType(), sim);
			}
			;
			// adds the path
			Interest interest = sim.getInterest();
			Gradient gradient = interest.getGradients().get(nextHop);
			if (gradient == null) {
				gradient = new Gradient(nextHop,
						originalInterest.getInterval(), originalInterest
								.getDuration());
				gradient.addFullPath(path);
				interest.getGradients().put(nextHop, gradient);
			}
			if (i == 0) {
				sim.setSensorInterestRole(SensorInterestRole.SensorForInterest,
						false);
			} else {
				sim.setSensorInterestRole(SensorInterestRole.Router, false);
			}
		}
	}

}
