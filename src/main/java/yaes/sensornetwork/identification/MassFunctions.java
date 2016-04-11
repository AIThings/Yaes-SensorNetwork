package yaes.sensornetwork.identification;

import hypothesis.Hypothesis;
import mass.exact.MassFunction;

public class MassFunctions {

	public static MassFunction<IntruderNodeType> createMfFromObservation(
			IdentificationProperties ip) {
		MassFunction<IntruderNodeType> retval = null;
		if (ip.weight >= 0) {
			if (ip.weight < 50) {
				// FIXME make this dependent on how small it is
				retval = createProbablySmall(0.8);
			} else {
				// FIXME make this dependent on how big it is
				retval = createProbablyLarge(0.8);
			}
		}
		return retval;
	}

	/**
	 * Creates a mass function which shows that the intruder is probably human
	 * 
	 * @param value
	 *            - the confidence (0.0 --- 1.0)
	 * @return
	 */
	public static MassFunction<IntruderNodeType> createProbablyHuman(
			double value) {
		MassFunction<IntruderNodeType> mf = new MassFunction<IntruderNodeType>();
		mf.add(new Hypothesis<IntruderNodeType>(
				IntruderNodeType.FRIENDLY_HUMAN,
				IntruderNodeType.INTRUDER_HUMAN), 0.5 + value / 2.0);
		mf.add(new Hypothesis<IntruderNodeType>(IntruderNodeType.ANIMAL,
				IntruderNodeType.SMALL_UGV, IntruderNodeType.VEHICLE),
				0.5 - value / 2.0);
		return mf;
	}

	/**
	 * Creates a mass function which shows that the intruder is probably human
	 * 
	 * @param value
	 *            - the confidence (0.0 --- 1.0)
	 * @return
	 */
	public static MassFunction<IntruderNodeType> createProbablyMetallic(
			double value) {
		MassFunction<IntruderNodeType> mf = new MassFunction<IntruderNodeType>();
		mf.add(new Hypothesis<IntruderNodeType>(
				IntruderNodeType.INTRUDER_HUMAN, IntruderNodeType.SMALL_UGV,
				IntruderNodeType.VEHICLE), 0.5 + value / 2.0);
		mf.add(new Hypothesis<IntruderNodeType>(IntruderNodeType.ANIMAL,
				IntruderNodeType.FRIENDLY_HUMAN), 0.5 - value / 2.0);
		return mf;
	}

	/**
	 * Creates a mass function which shows that the intruder is probably small
	 * 
	 * @param value
	 *            - the confidence (0.0 --- 1.0)
	 * @return
	 */
	public static MassFunction<IntruderNodeType> createProbablySmall(
			double value) {
		MassFunction<IntruderNodeType> mf = new MassFunction<IntruderNodeType>();
		mf.add(new Hypothesis<IntruderNodeType>(IntruderNodeType.ANIMAL,
				IntruderNodeType.SMALL_UGV), 0.5 + value / 2.0);
		mf.add(new Hypothesis<IntruderNodeType>(
				IntruderNodeType.INTRUDER_HUMAN,
				IntruderNodeType.FRIENDLY_HUMAN, IntruderNodeType.VEHICLE),
				0.5 - value / 2.0);
		return mf;
	}

	/**
	 * Creates a mass function which shows that the intruder is probably large
	 * 
	 * @param value
	 *            - the confidence (0.0 --- 1.0)
	 * @return
	 */
	public static MassFunction<IntruderNodeType> createProbablyLarge(
			double value) {
		MassFunction<IntruderNodeType> mf = new MassFunction<IntruderNodeType>();
		mf.add(new Hypothesis<IntruderNodeType>(IntruderNodeType.ANIMAL,
				IntruderNodeType.SMALL_UGV), 0.5 - value / 2.0);
		mf.add(new Hypothesis<IntruderNodeType>(
				IntruderNodeType.INTRUDER_HUMAN,
				IntruderNodeType.FRIENDLY_HUMAN, IntruderNodeType.VEHICLE),
				0.5 + value / 2.0);
		return mf;
	}

	public static MassFunction<IntruderNodeType> createPrior() {
		MassFunction<IntruderNodeType> mf = new MassFunction<IntruderNodeType>();
		mf.add(new Hypothesis<IntruderNodeType>(IntruderNodeType.values()), 1.0);
		return mf;
	}

}
