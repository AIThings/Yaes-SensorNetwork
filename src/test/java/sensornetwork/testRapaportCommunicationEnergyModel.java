package sensornetwork;
import junit.framework.TestCase;
import yaes.sensornetwork.energymodel.RapaportCommunicationEnergyModel;
import yaes.ui.text.TextUi;

public class testRapaportCommunicationEnergyModel extends TestCase {
	public static void main(String[] args) {
		junit.textui.TestRunner.run(testRapaportCommunicationEnergyModel.class);
	}

	public void testEnergyModel() {
		TextUi.println("Transmission energy in nanoJoules");
		final RapaportCommunicationEnergyModel em1 = new RapaportCommunicationEnergyModel(
				RapaportCommunicationEnergyModel.PowerConsumptionScenario.LOW_PATH_LOSS);
		final RapaportCommunicationEnergyModel em2 = new RapaportCommunicationEnergyModel(
				RapaportCommunicationEnergyModel.PowerConsumptionScenario.HIGH_PATH_LOSS);
		int b = 1000; // bytes
		double dist = 1;
		TextUi.println("Transmission energy for variable distance, package 1000 bytes");
		for (dist = 1; dist != 1000; dist++) {
			final String p1 = "" + (int) (em1.powerTx(dist, b) * 1e9);
			final String p2 = "" + (int) (em2.powerTx(dist, b) * 1e9);
			TextUi.println(dist + " m\t" + p1 + " nJ\t\t" + p2 + " nJ");
		}
		TextUi.println("Transmission energy for variable package size, distance 20 meters");
		dist = 20;
		for (b = 1; b < 1500; b = b + 20) {
			final String p1 = "" + (int) (em1.powerTx(dist, b) * 1e9);
			final String p2 = "" + (int) (em2.powerTx(dist, b) * 1e9);
			TextUi.println(b + " bytes\t" + p1 + " nJ\t\t" + p2 + " nJ");
		}
	}
}
