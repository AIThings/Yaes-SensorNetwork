package yaes.sensornetwork.scenarios.bridgeprotection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.agents.SensorRoutingHelper;
import yaes.sensornetwork.model.SensorNetworkWorld;

/**
 * Helper functions to set the roles of the nodes in the system
 * 
 * @author Lotzi Boloni
 * 
 */
public class BpaRouterHelper {

	/**
	 * Determines the near and the far side nodes, assuming it knows the bridge
	 * node
	 * 
	 * FIXME: extend to multiple bridge nodes
	 * 
	 * @param bridgeNode
	 */
	public static List<Set<AbstractSensorAgent>> determineNodeClasses(
			AbstractSensorAgent bridgeAgent, SensorNetworkWorld world) {
		List<Set<AbstractSensorAgent>> retval = new ArrayList<>();
		Set<AbstractSensorAgent> farSide = new HashSet<>();
		retval.add(farSide);
		Set<AbstractSensorAgent> nearSide = new HashSet<>();
		retval.add(nearSide);
		Set<AbstractSensorAgent> gates = new HashSet<>();
		retval.add(gates);
		Set<AbstractSensorAgent> fanouts = new HashSet<>();
		retval.add(fanouts);

		// create list of abstract sensor agents enabled and a certain type
		List<AbstractSensorAgent> allAgents = SensorRoutingHelper
				.getSensorAgents(world, AbstractSensorAgent.class);
		List<AbstractSensorAgent> sourceAgents = new ArrayList<>(
				allAgents);
		allAgents.add(world.getSinkNode().getAgent());
		// if the bridge agent is on the path, the node is on the far side, if
		// it is not, it is on the near side
		for (AbstractSensorAgent source : sourceAgents) {
			List<String> path = SensorRoutingHelper.getShortestPath(allAgents,
					source, world.getSinkNode().getAgent());
			if (source.equals(bridgeAgent)) {
				continue;
			}
			//
			if (path.contains(bridgeAgent.getName())) {
				if (SensorRoutingHelper.isConnected(bridgeAgent, source)) {
					gates.add(source);
				} else {
					farSide.add(source);
				}
			} else {
				if (SensorRoutingHelper.isConnected(bridgeAgent, source)) {
					fanouts.add(source);
				} else {
					nearSide.add(source);
				}
			}
		}
		return retval;
	}
}
