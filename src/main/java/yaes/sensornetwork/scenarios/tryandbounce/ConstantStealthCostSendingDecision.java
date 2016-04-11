/**
 * 
 */
package yaes.sensornetwork.scenarios.tryandbounce;

import java.util.HashMap;
import java.util.Map;

import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.agents.ISendingDecision;
import yaes.sensornetwork.model.stealth.StealthySensorNetworkWorld;

/**
 * 
 * This class implements a stealth cost sending decision system where the
 * decision is based on spending a certain average stealth cost / threat
 * 
 * The same class can be used to account not on a per/node basis, but simply
 * passing "unknown" to the getAverage
 * 
 * @author lboloni
 * 
 */
public class ConstantStealthCostSendingDecision implements ISendingDecision {

	private static final long serialVersionUID = 7521138481080159109L;
	private double runningAverageMultiplier = 0.9;
	private double runningAverageTransmissionCostThreshold = 0;

	private Map<String, Double> costPerThreat = new HashMap<String, Double>();

	/**
	 * Returns the current average cost / intruder node
	 * 
	 * @param name
	 * @return
	 */
	private double getAverageCost(String name) {
		Double cost = costPerThreat.get(name);
		if (cost == null) {
			return 0;
		}
		return cost;
	}

	/**
	 * @return the runningAverageTransmissionCostThreshold
	 */
	public double getRunningAverageTransmissionCostThreshold() {
		return runningAverageTransmissionCostThreshold;
	}

	/**
	 * Occurs a certain cost / intruder node
	 * 
	 * @param newCost
	 * @param name
	 */
	public void occurCost(double newCost, String name) {
		Double cost = costPerThreat.get(name);
		double val = 0;
		if (cost == null) {
			val = 0;
		} else {
			val = cost;
		}
		val = val + newCost;
		costPerThreat.put(name, val);
	}

	/**
	 * @param sensorAgent
	 * @return
	 */
	@Override
	public boolean readyToSend(AbstractSensorAgent sensorAgent,
			Object... params) {
		String intruderNode = (String) params[0];
		//double costOfTransmitting = ((StealthySensorNetworkWorld) sensorAgent
		//		.getSensorWorld()).costOfTransmitting(sensorAgent.getNode());
		double avgCost = getAverageCost(intruderNode);
		if (avgCost < runningAverageTransmissionCostThreshold) {
			return true;
		}
		return false;
	}

	/**
	 * @param sensorAgent
	 * @param params
	 */
	@Override
	public void sent(AbstractSensorAgent sensorAgent, Object... params) {
		String intruderNode = (String) params[0];
		double costOfTransmitting = ((StealthySensorNetworkWorld) sensorAgent
				.getSensorWorld()).costOfTransmitting(sensorAgent.getNode());
		occurCost(costOfTransmitting, intruderNode);
	}

	/**
	 * @param runningAverageTransmissionCostThreshold
	 *            the runningAverageTransmissionCostThreshold to set
	 */
	public void setRunningAverageTransmissionCostThreshold(
			double runningAverageTransmissionCostThreshold) {
		this.runningAverageTransmissionCostThreshold = runningAverageTransmissionCostThreshold;
	}

	public void updateOnTime() {
		for (String key : costPerThreat.keySet()) {
			double val = costPerThreat.get(key);
			val = val * runningAverageMultiplier;
			costPerThreat.put(key, val);
		}
	}
}
