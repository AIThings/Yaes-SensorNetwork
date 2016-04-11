/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Jan 4, 2011
 
   storeanddump.knowledge.LinearEstimator
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.knowledge;

import java.util.Collections;
import java.util.List;

import yaes.world.physical.location.Location;

/**
 * 
 * <code>storeanddump.knowledge.LinearEstimator</code>
 * 
 * A linear estimator, creates a linear interpolation / extrapolation for the
 * locations.
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class LinearEstimator extends SimpleEstimator {

	private static final long serialVersionUID = 1743986913711468217L;

	/**
	 * Linear estimator
	 */
	@Override
	public Location getIntruderLocation(IntruderSightingHistory hist,
			String intruderName, double time) {
		List<IntruderSighting> sightings = hist
				.getSightingsOfIntruder(intruderName);
		List<IntruderSighting> previous = IntruderSighingHelper.sightedBetween(
				sightings, 0, time);
		List<IntruderSighting> after = IntruderSighingHelper.sightedBetween(
				sightings, time, Double.MAX_VALUE);
		// FIXME: it does not handle the case if we don't have previous but we
		// have a post!
		if (previous.isEmpty()) {
			return null;
		}
		IntruderSighting sightPreviousLast = Collections.max(previous,
				IntruderSighingHelper.sightingOrder());
		// Case 1: we have an observation exactly at the time
		if (sightPreviousLast.getTimeSighting() == time) {
			return sightPreviousLast.getLocationIntruder();
		}
		// Case 2 - Interpolation: we have an observation before and after
		if (!after.isEmpty()) {
			IntruderSighting sightAfterFirst = Collections.min(after,
					IntruderSighingHelper.sightingOrder());
			return interpolate(sightPreviousLast, sightAfterFirst, time);
		}
		// Case 3 - Extrapolation: no observation
		previous.remove(sightPreviousLast);
		IntruderSighting sightPrePre = Collections.max(previous,
				IntruderSighingHelper.sightingOrder());
		return interpolate(sightPrePre, sightPreviousLast, time);
	}

	/**
	 * @param sightPreviousLast
	 * @param sightAfterFirst
	 * @param time
	 * @return
	 */
	private Location interpolate(IntruderSighting s1, IntruderSighting s2,
			double time) {
		double deltaX = s2.getLocationIntruder().getX()
				- s1.getLocationIntruder().getX();
		double deltaY = s2.getLocationIntruder().getY()
				- s1.getLocationIntruder().getY();
		double fraction = (time - s1.getTimeSighting())
				/ (s2.getTimeSighting() - s1.getTimeSighting());
		double newX = s1.getLocationIntruder().getX() + fraction * deltaX;
		double newY = s1.getLocationIntruder().getY() + fraction * deltaY;
		Location loc = new Location(newX, newY);
		return loc;
	}

}
