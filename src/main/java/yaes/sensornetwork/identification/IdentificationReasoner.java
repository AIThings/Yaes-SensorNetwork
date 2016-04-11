package yaes.sensornetwork.identification;

import java.util.ArrayList;
import java.util.List;

import hypothesis.Hypothesis;
import mass.exact.MassFunction;
import yaes.ui.format.Formatter;
import yaes.ui.text.TextUi;
import yaes.ui.text.TextUiHelper;

public class IdentificationReasoner {

	
	public static void reason(List<IdentificationProperties> observations) {
		List<MassFunction<IntruderNodeType>> evidences = new ArrayList<MassFunction<IntruderNodeType>>();
		for(IdentificationProperties ipObs: observations) {
			MassFunction<IntruderNodeType> evidence = MassFunctions.createMfFromObservation(ipObs);
			evidences.add(evidence);
		}
		MassFunction<IntruderNodeType> mf = MassFunctions.createPrior();
		for(MassFunction<IntruderNodeType> ev: evidences) {
			mf = mf.combineConjunctive(ev);
			TextUi.println(printIdentificationMassFunction(mf));
		}
	}

	/**
	 * Pretty print function for the mass function
	 * @param mf
	 * @return
	 */
	public static String printIdentificationMassFunction(
			MassFunction<IntruderNodeType> mf) {
		Formatter fmt = new Formatter();
		fmt.add(TextUiHelper.padTo("", 20) + TextUiHelper.padTo("Belief", 10)
				+ "Plausibility");
		for (IntruderNodeType nodeType : IntruderNodeType.values()) {
			Hypothesis<IntruderNodeType> hyp = new Hypothesis<IntruderNodeType>(
					nodeType);
			String t = TextUiHelper.padTo(nodeType, 20)
					+ TextUiHelper.padTo(Formatter.fmt(mf.getBelief(hyp)), 10)
					+ TextUiHelper.padTo(
							Formatter.fmt(mf.getPlausibility(hyp)), 10);
			fmt.add(t);
		}
		return fmt.toString();
	}

	
}
