/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Jan 5, 2011
 
   storeanddumptest.knowledge.Scenario
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package sensornetwork.knowledge;

import yaes.sensornetwork.identification.IdPropObservationFactory;
import yaes.sensornetwork.identification.IdPropObservationFactory.SensorType;
import yaes.sensornetwork.identification.IdentificationProperties;
import yaes.sensornetwork.knowledge.IntruderSighting;
import yaes.sensornetwork.knowledge.IntruderSightingHistory;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.PPMTraversal;
import yaes.world.physical.path.PlannedPath;
import yaes.world.physical.path.ProgrammedPathMovement;

/**
 * 
 * <code>storeanddumptest.knowledge.Scenario</code>
 * 
 * Creates an intruder scenario for the benefit of the tests
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class Scenario {

	/**
	 * Adds some periodic observations about the intruder to the intruder
	 * sighting history.
	 * 
	 * The assumption here is perfect quality observation from a full knowledge
	 * agent who can see the whole field
	 */
	public static void addPeriodicObservations(double endtime,
			ProgrammedPathMovement ppm, IntruderSightingHistory ish,
			double period) {
		double delay = 5;
		double collect = period + 1;
		PPMTraversal ppmt = new PPMTraversal(ppm, 0);
		for (double time = 0; time <= endtime; time++) {
			Location l = ppmt.getLocation(time);
			if (collect >= period) {
				IntruderSighting is = new IntruderSighting("N0", time, "P1", l);
				ish.addSighting(is, time + delay);
				collect = 0;
			} else {
				collect += 1.0;
			}
		}
	}

	/**
	 * Adds some periodic observations about the intruder to the intruder
	 * sighting history.
	 * 
	 * The assumption here is perfect quality observation from a full knowledge
	 * agent who can see the whole field
	 */
	public static void addPeriodicObservationsFromSensor(double endtime,
			String intruderName, IdentificationProperties intruderProperties,
			ProgrammedPathMovement ppm, IntruderSightingHistory ish,
			double period, Location sensorLocation, String sensorName,
			SensorType sensorType, double sensorRange) {
		double delay = 5;
		double collect = period + 1;
		PPMTraversal ppmt = new PPMTraversal(ppm, 0);
		for (double time = 0; time <= endtime; time++) {
			Location l = ppmt.getLocation(time);
			if (collect >= period) {
				double distance = l.distanceTo(sensorLocation);
				if (distance <= sensorRange) {
					IntruderSighting is = new IntruderSighting(sensorName,
							time, intruderName, l);
					IdentificationProperties observedProperties = IdPropObservationFactory
							.getSensorReading(intruderProperties, sensorType,
									distance, sensorRange);
					is.setIdentificationProperties(observedProperties);
					ish.addSighting(is, time + delay);
				}
				collect = 0;
			} else {
				collect += 1.0;
			}
		}
	}

	/**
	 * creates a simple intruder path
	 * 
	 * @return
	 */
	public static final ProgrammedPathMovement createIntruderPath() {
		ProgrammedPathMovement ppm = new ProgrammedPathMovement();
		Location A = new Location(10, 10);
		Location B = new Location(50, 50);
		Location C = new Location(0, 100);

		ppm.addSetLocation(A);
		PlannedPath pp = new PlannedPath(A, B);
		pp.addLocation(A);
		pp.addLocation(B);
		ppm.addFollowPath(pp, 10);
		pp = new PlannedPath(B, C);
		pp.addLocation(B);
		pp.addLocation(C);
		ppm.addFollowPath(pp, 20);
		return ppm;
	}

}
