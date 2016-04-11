/*
 * This class assumes that there is a 
 * 
 * 
 */
package yaes.sensornetwork.model.stealth;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import yaes.sensornetwork.model.SensorNode;
import yaes.sensornetwork.model.SinkNode;

public class StealthMeasurementHelper {

	/**
	 * Returns true if the sensor network described by the system is connected
	 * to the sink. The way in which this is implemented has an O(n^2)
	 * complexity, it can be improved.
	 * 
	 * 
	 * @param nodes
	 * @param sink
	 * @return
	 */
	public static boolean isConnected(List<SensorNode> nodes, SinkNode sink) {
		ArrayList<SensorNode> inside = new ArrayList<SensorNode>();
		inside.add(sink);
		ArrayList<SensorNode> outside = new ArrayList<SensorNode>();
		outside.addAll(nodes);
		while (!outside.isEmpty()) {
			SensorNode nextIn = null;
			for (SensorNode candidate : outside) {
				for (SensorNode insideCandidate : inside) {
					if (candidate.getLocation().distanceTo(
							insideCandidate.getLocation()) <= candidate
							.getAgent().getTransmissionRange()) {
						// move the candidate inside
						nextIn = candidate;
						break;
					}
				}
				if (nextIn != null) {
					break;
				}
			}
			if (nextIn == null) {
				return false;
			}
			outside.remove(nextIn);
			inside.add(nextIn);
		}
		return true;
	}

	/**
	 * Returns true if the sensor network described by the system is connected
	 * to the sink. The way in which this is implemented has an O(n^2)
	 * complexity, it can be improved.
	 * 
	 * 
	 * @param nodes
	 * @param sink
	 * @return
	 */
	private static boolean isConnectedWithTerminal(List<SensorNode> nodes,
			List<SensorNode> terminalOnlyNodes, SinkNode sink) {
		ArrayList<SensorNode> inside = new ArrayList<SensorNode>();
		inside.add(sink);
		ArrayList<SensorNode> outside = new ArrayList<SensorNode>();
		outside.addAll(nodes);
		while (!outside.isEmpty()) {
			SensorNode nextIn = null;
			for (SensorNode candidate : outside) {
				for (SensorNode insideCandidate : inside) {
					if (candidate.getLocation().distanceTo(
							insideCandidate.getLocation()) <= candidate
							.getAgent().getTransmissionRange()) {
						// move the candidate inside
						nextIn = candidate;
						break;
					}
				}
				if (nextIn != null) {
					break;
				}
			}
			if (nextIn == null) {
				return false;
			}
			outside.remove(nextIn);
			inside.add(nextIn);
		}
		// now let us see whether all the terminal only nodes are connected
		// to some of the inside nodes.
		for (SensorNode candidate : terminalOnlyNodes) {
			boolean isConnected = false;
			for (SensorNode insideCandidate : inside) {
				if (candidate.getLocation().distanceTo(
						insideCandidate.getLocation()) <= candidate.getAgent()
						.getTransmissionRange()) {
					isConnected = true;
					break;
				}
			}
			if (isConnected == false) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the minimum stealth level
	 * 
	 * @param sensorWorld
	 * @return
	 */
	public static double stealthAverage(StealthySensorNetworkWorld sensorWorld) {
		double sum = 0;
		double count = 0;
		for (SensorNode node : sensorWorld.getSensorNodes()) {
			double stealth = sensorWorld.getStealthModel(node)
					.getStealthLevel();
			sum = sum + stealth;
			count = count + 1.0;
		}
		return sum / count;
	}

	/**
	 * Returns the lowest stealth level for which the network is still connected
	 * 
	 * NOTE: we need a different definition of connectivity here, which involves
	 * who can be terminal node vs. transit node.
	 * 
	 */
	public static double stealthForConnectivity(
			StealthySensorNetworkWorld sensorWorld) {
		TreeSet<Double> stealthValues = new TreeSet<Double>();
		stealthValues.add(0.0);
		stealthValues.add(1.0);
		// gather all the possible stealth values
		for (SensorNode node : sensorWorld.getSensorNodes()) {
			double stealth = sensorWorld.getStealthModel(node)
					.getStealthLevel();
			stealthValues.add(stealth);
		}

		// now for each value, check if the resulting set is connected. for
		for (Iterator<Double> it = stealthValues.descendingIterator(); it
				.hasNext();) {
			double val = it.next();
			List<SensorNode> reduced = new ArrayList<SensorNode>();
			List<SensorNode> terminal = new ArrayList<SensorNode>();
			for (SensorNode node : sensorWorld.getSensorNodes()) {
				double stealth = sensorWorld.getStealthModel(node)
						.getStealthLevel();
				if (stealth >= val) {
					reduced.add(node);
				} else {
					terminal.add(node);
				}
			}
			if (reduced.isEmpty()) {
				continue;
			}
			boolean connected = isConnectedWithTerminal(reduced, terminal,
					sensorWorld.getSinkNode());
			if (connected) {
				return val;
			}
		}
		return 0;
	}

	/**
	 * Returns the maximum stealth level
	 * 
	 * @param sensorWorld
	 * @return
	 */
	public static double stealthMaximum(StealthySensorNetworkWorld sensorWorld) {
		double retVal = 0;
		for (SensorNode node : sensorWorld.getSensorNodes()) {
			double stealth = sensorWorld.getStealthModel(node)
					.getStealthLevel();
			if (stealth > retVal) {
				retVal = stealth;
			}
		}
		return retVal;
	}

	/**
	 * Returns the minimum stealth level
	 * 
	 * @param sensorWorld
	 * @return
	 */
	public static double stealthMinimum(StealthySensorNetworkWorld sensorWorld) {
		double retVal = 1.0;
		for (SensorNode node : sensorWorld.getSensorNodes()) {
			double stealth = sensorWorld.getStealthModel(node)
					.getStealthLevel();
			if (stealth < retVal) {
				retVal = stealth;
			}
		}
		return retVal;
	}
}
