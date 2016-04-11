/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Oct 23, 2010
 
   storeanddumptest.testKnowledgeHistory
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package sensornetwork.knowledge;

import org.junit.Test;

import yaes.sensornetwork.knowledge.IntruderSighting;
import yaes.sensornetwork.knowledge.IntruderSightingHistory;
import yaes.ui.text.TextUi;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.ProgrammedPathMovement;

/**
 * 
 * <code>storeanddumptest.knowledge.testIntruderSightingHistory</code>
 * 
 * Some basic tests for the intruder sighting history - just making sure that it
 * works.
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class testIntruderSightingHistory {

	/**
	 * Basic test: adding some
	 */
	@Test
	public void testBasic() {
		IntruderSightingHistory ish = new IntruderSightingHistory();
		IntruderSighting is = new IntruderSighting("Node", 0, "Intruder",
				new Location(10, 10));
		ish.addSighting(is, 10);
		is = new IntruderSighting("Node", 0, "Intruder-2", new Location(20, 20));
		ish.addSighting(is, 15);
		// print the reporters
		TextUi.println("Reporters:" + ish.getSighters());
		TextUi.println("Intruders:" + ish.getIntruders());
		TextUi.println(ish);
	}

	/**
	 * Basic test: adding some
	 */
	@Test
	public void testScenarioLoad() {
		IntruderSightingHistory ish = new IntruderSightingHistory();
		ProgrammedPathMovement ppm = Scenario.createIntruderPath();
		Scenario.addPeriodicObservations(20, ppm, ish, 2);
		TextUi.println(ish);
	}

}
