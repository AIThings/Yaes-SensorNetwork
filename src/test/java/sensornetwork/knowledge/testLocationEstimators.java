/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Jan 5, 2011
 
   storeanddumptest.knowledge.testSimpleEstimator
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package sensornetwork.knowledge;

import static org.junit.Assert.*;

import org.junit.Test;

import yaes.sensornetwork.knowledge.ILocationEstimator;
import yaes.sensornetwork.knowledge.IntruderSightingHistory;
import yaes.sensornetwork.knowledge.LinearEstimator;
import yaes.sensornetwork.knowledge.SimpleEstimator;
import yaes.ui.format.Formatter;
import yaes.ui.text.TextUi;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.PPMTraversal;
import yaes.world.physical.path.ProgrammedPathMovement;

/**
 * 
 * <code>storeanddumptest.knowledge.testSimpleEstimator</code>
 * 
 * Tests the simple estimator
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class testLocationEstimators {

	public double calculateAvgError(ILocationEstimator est) {
		double endTime = 20;
		IntruderSightingHistory ish = new IntruderSightingHistory();
		ProgrammedPathMovement ppm = Scenario.createIntruderPath();
		Scenario.addPeriodicObservations(endTime, ppm, ish, 3);
		double distSum = 0.0;
		int count = 0;
		PPMTraversal ppmt = new PPMTraversal(ppm, 0);
		for (double time = 0; time <= endTime; time++) {
			Location real = ppmt.getLocation(time);
			Location estimated = est.getIntruderLocation(ish, "P1", time);
			double dist = real.distanceTo(estimated);
			count++;
			distSum += dist;
			TextUi.println("Real: " + real + "  Est:" + estimated + "  error ="
					+ String.format("%6.2f", dist));
		}
		double averageError = distSum / count;
		return averageError;
	}

	@Test
	public void testBasic() {
		ILocationEstimator estimatorSimple = new SimpleEstimator();		
		ILocationEstimator estimatorLinear = new LinearEstimator();
		double avgErrorSimple = calculateAvgError(estimatorSimple);
		double avgErrorLinear = calculateAvgError(estimatorLinear);
		TextUi.println("Avg error simple: " + Formatter.fmt(avgErrorSimple));
		TextUi.println("Avg error linear: " + Formatter.fmt(avgErrorLinear));
		assertTrue(avgErrorLinear < avgErrorSimple);
	}

}
