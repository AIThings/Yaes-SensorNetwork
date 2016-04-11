/*
 * Created on Aug 6, 2007
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sensornetwork;

import org.junit.Test;

import yaes.sensornetwork.model.stealth.TransmissionDisclosureEvent;
import yaes.ui.text.TextUi;

public class testTransmissionDisclosureEvent {

	@Test
	public void testDistance() {
		// double distance = 30;
		double transmissionRange = 30;
		double pathLossIndex = 3;
		double pL_over_pG = 0.1;
		double p_attention = 1;
		TextUi.println("Disclosure probability for various values of distance");
		for (double distance = 0; distance < 100; distance = distance + 1) {
			TransmissionDisclosureEvent event = new TransmissionDisclosureEvent(
					distance, transmissionRange, pathLossIndex, pL_over_pG,
					p_attention);
			TextUi.println(distance + " --> "
					+ event.getDisclosureProbability());
		}
	}

	@Test
	public void testPathLossIndex() {
		double distance = 60;
		double transmissionRange = 30;
		// double pathLossIndex = 3;
		double pL_over_pG = 0.1;
		double p_attention = 1;
		TextUi.println("Disclosure probability for various values of path loss index");
		for (double pathLossIndex = 2; pathLossIndex <= 4; pathLossIndex = pathLossIndex + 0.1) {
			TransmissionDisclosureEvent event = new TransmissionDisclosureEvent(
					distance, transmissionRange, pathLossIndex, pL_over_pG,
					p_attention);
			TextUi.println(pathLossIndex + " --> "
					+ event.getDisclosureProbability());
		}
	}

}
