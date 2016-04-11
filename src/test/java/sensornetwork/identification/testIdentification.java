package sensornetwork.identification;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import mass.exact.MassFunction;
import yaes.sensornetwork.identification.IdentificationReasoner;
import yaes.sensornetwork.identification.IntruderNodeType;
import yaes.sensornetwork.identification.MassFunctions;
import yaes.ui.text.TextUi;

public class testIdentification {

	@Test
	public void testTheIdentification() {
		List<MassFunction<IntruderNodeType>> evidence = new ArrayList<MassFunction<IntruderNodeType>>();
		evidence.add(MassFunctions.createProbablyHuman(0.3));
		
		evidence.add(MassFunctions.createProbablyHuman(0.3));
		evidence.add(MassFunctions.createProbablyHuman(0.3));
		evidence.add(MassFunctions.createProbablyMetallic(0.3));
		evidence.add(MassFunctions.createProbablyHuman(0.3));
		MassFunction<IntruderNodeType> mf = MassFunctions.createPrior();
		for(MassFunction<IntruderNodeType> ev: evidence) {
			mf = mf.combineConjunctive(ev);
			TextUi.println(IdentificationReasoner.printIdentificationMassFunction(mf));
		}
	}

}
