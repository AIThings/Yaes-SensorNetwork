/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Oct 24, 2010
 
   storeanddump.knowledge.AccuracyMetric
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.knowledge;

import java.awt.geom.Rectangle2D;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import yaes.world.physical.location.Location;

/**
 * 
 * <code>storeanddump.knowledge.AccuracyMetric</code>
 * 
 * Helper function for accuracy metrics defined over pairs of intruder sighting
 * histories and estimators.
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class AccuracyMetric {

	/**
	 * The average estimation error between an estimator / history vs. a ground
	 * truth estimator / history, for one target, over a certain time range
	 * 
	 * @param timeFrom
	 * @param timeTo
	 * @param target
	 * @param measuredEstimator
	 * @param measuredISH
	 * @param groundTruthEstimator
	 * @param groundTruthISH
	 * @return
	 */
	public static double averageEstimationError(double timeFrom, double timeTo,
			String target, ILocationEstimator measuredEstimator,
			IntruderSightingHistory measuredISH,
			ILocationEstimator groundTruthEstimator,
			IntruderSightingHistory groundTruthISH) {
		double sum = 0;
		double count = 0;
		for (double time = timeFrom; time <= timeTo; time = time + 1.0) {
			Location measured = measuredEstimator.getIntruderLocation(
					measuredISH, target, time);
			Location groundTruth = measuredEstimator.getIntruderLocation(
					measuredISH, target, time);
			sum += groundTruth.distanceTo(measured);
			count++;
		}
		return sum / count;
	}

	/**
	 * The average interest area estimation error between an estimator / history
	 * vs. a ground truth estimator / history, for one target, over a certain
	 * time range
	 * 
	 * @param interestRectangle
	 * @param timeFrom
	 * @param timeTo
	 * @param target
	 * @param measuredEstimator
	 * @param measuredISH
	 * @param groundTruthEstimator
	 * @param groundTruthISH
	 * @return
	 */
	public static double averageInterestAreaEstimationError(
			Rectangle2D.Double interestRectangle, double timeFrom,
			double timeTo, String target, ILocationEstimator measuredEstimator,
			IntruderSightingHistory measuredISH,
			ILocationEstimator groundTruthEstimator,
			IntruderSightingHistory groundTruthISH) {
		double sum = 0;
		double count = 0;
		for (double time = timeFrom; time <= timeTo; time = time + 1.0) {
			Location measured = measuredEstimator.getIntruderLocation(
					measuredISH, target, time);
			Location groundTruth = measuredEstimator.getIntruderLocation(
					measuredISH, target, time);
			sum += IntruderTrackingAccuracy.targetTrackingAccuracyDirect(
					groundTruth, measured, interestRectangle);
			count++;
		}
		return sum / count;
	}

	/**
	 * The current absolute estimation error. It assumes that the ground truth
	 * (more exactly, the better estimator) will have information. If the
	 * measured estimator does not have information, the information will be
	 * considered infinity
	 * 
	 * @param timeFrom
	 * @param timeTo
	 * @param target
	 * @param measuredEstimator
	 * @param measuredISH
	 * @param groundTruthEstimator
	 * @param groundTruthISH
	 * @return
	 */
	public static double currentEstimationError(double currentTime,
			String target, ILocationEstimator measuredEstimator,
			IntruderSightingHistory measuredISH,
			ILocationEstimator groundTruthEstimator,
			IntruderSightingHistory groundTruthISH) {
		Location measured = measuredEstimator.getIntruderLocation(measuredISH,
				target, currentTime);
		if (measured == null) {
			return Double.MAX_VALUE;
		}
		Location groundTruth = groundTruthEstimator.getIntruderLocation(
				groundTruthISH, target, currentTime);
		return groundTruth.distanceTo(measured);
	}

	/**
	 * The current interest area estimation error between an estimator / history
	 * vs. a ground truth estimator / history, for one target
	 * 
	 * @param interestRectangle
	 * @param timeFrom
	 * @param timeTo
	 * @param target
	 * @param measuredEstimator
	 * @param measuredISH
	 * @param groundTruthEstimator
	 * @param groundTruthISH
	 * @return
	 */
	public static double currentInterestAreaEstimationError(
			Rectangle2D.Double interestRectangle, double currentTime,
			String target, ILocationEstimator measuredEstimator,
			IntruderSightingHistory measuredISH,
			ILocationEstimator groundTruthEstimator,
			IntruderSightingHistory groundTruthISH) {
		Location measured = measuredEstimator.getIntruderLocation(measuredISH,
				target, currentTime);
		Location groundTruth = groundTruthEstimator.getIntruderLocation(
				groundTruthISH, target, currentTime);
		return IntruderTrackingAccuracy.targetTrackingAccuracyDirect(
				groundTruth, measured, interestRectangle);
	}

	/**
	 * Creates the uncertainty areas, returns them and the sum
	 * 
	 * @param timeFrom
	 * @param timeTo
	 * @param target
	 * @param groundTruthEstimator
	 * @param measuredHistory
	 * @return
	 */
	public static SimpleEntry<List<UncertainMovementSegment>, Double> uncertaintyArea(
			double timeFrom, double timeTo, String target, double maxSpeed,
			ILocationEstimator measuredEstimator,
			IntruderSightingHistory measuredHistory) {
		List<Location> locations = new ArrayList<>();
		List<Double> times = new ArrayList<>();
		// starting point
		Location locationFrom = measuredEstimator.getIntruderLocation(
				measuredHistory, target, timeFrom);
		if (locationFrom != null) {
			locations.add(locationFrom);
			times.add(timeFrom);
		}
		// sightings
		List<IntruderSighting> sightings = measuredHistory
				.getSightingsOfIntruder(target);
		Collections.sort(sightings, IntruderSighingHelper.sightingOrder());
		for (IntruderSighting is : sightings) {
			locations.add(is.getLocationIntruder());
			times.add(is.getTimeSighting());
		}
		// ending point
		// Location locationTo = measuredEstimator.getIntruderLocation(
		// measuredHistory, target, timeTo);
		// locations.add(locationTo);
		// times.add(timeTo);
		// now create the uncertainty segments and sum of area
		List<UncertainMovementSegment> list = new ArrayList<>();
		if (locations.isEmpty()) {
			return new SimpleEntry<>(
					list, 0.0);
		}
		double area = 0;
		for (int i = 0; i != locations.size() - 1; i++) {
			Location from = locations.get(i);
			Location to = locations.get(i + 1);
			double tfrom = times.get(i);
			double tto = times.get(i + 1);
			UncertainMovementSegment ums = new UncertainMovementSegment(from,
					tfrom, to, tto, maxSpeed);
			list.add(ums);
			area += ums.getArea();
		}
		SimpleEntry<List<UncertainMovementSegment>, Double> retval = new SimpleEntry<List<UncertainMovementSegment>, Double>(
				list, area);
		return retval;
	}

}
