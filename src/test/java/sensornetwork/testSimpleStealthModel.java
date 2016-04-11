/*
 * Created on Aug 6, 2007
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sensornetwork;

import org.junit.Test;

import yaes.sensornetwork.model.stealth.SimpleStealthModel;
import yaes.sensornetwork.model.stealth.TransmissionDisclosureEvent;
import yaes.ui.text.TextUi;

public class testSimpleStealthModel {

	@Test
	public void testThreeEvents() {
		double p_deploy = 0.05;
		double p_ad = 0.01;
		SimpleStealthModel model = new SimpleStealthModel(p_deploy, p_ad);
		double transmissionRange = 30;
		double pathLossIndex = 3;
		double pL_over_pG = 0.1;
		double p_attention = 1;
		double distance1 = 80;
		double distance2 = 60;
		double distance3 = 40;
		TransmissionDisclosureEvent event1 = new TransmissionDisclosureEvent(
				distance1, transmissionRange, pathLossIndex, pL_over_pG,
				p_attention);
		TransmissionDisclosureEvent event2 = new TransmissionDisclosureEvent(
				distance2, transmissionRange, pathLossIndex, pL_over_pG,
				p_attention);
		TransmissionDisclosureEvent event3 = new TransmissionDisclosureEvent(
				distance3, transmissionRange, pathLossIndex, pL_over_pG,
				p_attention);
		for (double time = 0; time != 30; time = time + 1) {
			model.update(time);
			if (time == 10) {
				model.updateEvent(event1);
			}
			if (time == 15) {
				model.updateEvent(event2);
			}
			if (time == 20) {
				model.updateEvent(event3);
			}
			TextUi.println(time + " ---> " + model.getStealthLevel());
		}
	}
}
