/*
 * Created on Aug 6, 2007
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package yaes.sensornetwork.model.stealth;

public class TransmissionDisclosureEvent implements IDisclosureEvent {

	private double disclosureProbability;
	private double distance;
	private double transmissionRange;
	private double pathLossIndex;
	private double p_attention;
	private double pL_over_pG;

	public TransmissionDisclosureEvent(double distance,
			double transmissionRange, double pathLossIndex, double pL_over_pG,
			double p_attention) {
		this.distance = distance;
		this.transmissionRange = transmissionRange;
		this.pathLossIndex = pathLossIndex;
		this.pL_over_pG = pL_over_pG;
		this.p_attention = p_attention;
		calculate();
	}

	/**
	 * Calculates the disclosure probability.
	 */
	private void calculate() {
		if (distance == 0) {
			disclosureProbability = p_attention;
			return;
		}
		double t1 = Math.min(1, Math.pow(transmissionRange / distance,
				pathLossIndex))
				- pL_over_pG;
		double t2 = 1 - pL_over_pG;
		disclosureProbability = p_attention * Math.max(0, t1 / t2);
	}

	@Override
	public double getDisclosureProbability() {
		return disclosureProbability;
	}
}
