/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Jan 4, 2011
 
   storeanddump.knowledge.SimpleEstimator
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.knowledge;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import yaes.world.physical.location.Location;

/**
 * A simple implementation of the estimator
 * 
 * For estimating location, always picks the last value
 * 
 * For estimating path, simply adds all the values it has, in addition to the
 * first and the last's estimate
 * 
 * <code>storeanddump.knowledge.SimpleEstimator</code>
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class SimpleEstimator implements ILocationEstimator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6675043554663851130L;

	/**
	 * Pick the latest estimate but before the time
	 */
	@Override
	public Location getIntruderLocation(IntruderSightingHistory hist,
			String intruderName, double time) {
		List<IntruderSighting> sightings = hist
				.getSightingsOfIntruder(intruderName);
		List<IntruderSighting> previous = IntruderSighingHelper.sightedBetween(
				sightings, 0, time);
		if (previous.isEmpty()) {
			return null;
		}
		IntruderSighting sight = Collections.max(previous,
				IntruderSighingHelper.sightingOrder());
		return sight.getLocationIntruder();
	}

	/**
	 * Create a path which are the hops in the observations
	 */
	@Override
	public List<SimpleEntry<Location, Double>> getIntruderPath(
			IntruderSightingHistory hist, String intruderName,
			double startTime, double endTime) {
		// resolve source and destination
		Location source = getIntruderLocation(hist, intruderName, startTime);
		Location destination = getIntruderLocation(hist, intruderName, endTime);
		List<SimpleEntry<Location, Double>> retval = new ArrayList<>();
		List<IntruderSighting> sightings = hist
				.getSightingsOfIntruder(intruderName);
		List<IntruderSighting> inbetween = IntruderSighingHelper
				.sightedBetween(sightings, startTime, endTime);
		Collections.sort(inbetween, IntruderSighingHelper.sightingOrder());
		retval.add(new SimpleEntry<>(source, startTime));
		for (IntruderSighting is : inbetween) {
			retval.add(new SimpleEntry<>(is
					.getLocationIntruder(), is.getTimeSighting()));
		}
		retval.add(new SimpleEntry<>(destination, endTime));
		return retval;
	}

}
