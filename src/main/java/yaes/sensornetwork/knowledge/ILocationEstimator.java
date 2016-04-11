/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Jan 4, 2011
 
   storeanddump.knowledge.ILocationEstimator
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.knowledge;

import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import yaes.world.physical.location.Location;

/**
 * 
 * <code>storeanddump.knowledge.ILocationEstimator</code>
 * 
 * Interface for all the estimators which, based on the sighting history can
 * make a prediction of where an intruder is
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public interface ILocationEstimator extends Serializable {

	/**
	 * The estimator returns where it believes the node to be at time t
	 * 
	 * @param hist
	 *            the sightings based on which the estimator forms its judgement
	 * @param intruderName
	 * @param currentTime
	 * @return
	 */
	Location getIntruderLocation(IntruderSightingHistory hist,
			String intruderName, double time);

	/**
	 * The returns the path it believes that the node traversed from startTime
	 * to endTime
	 * 
	 * @param hist
	 *            the sightings based on which the estimator forms its judgement
	 * @param intruderName
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	List<SimpleEntry<Location, Double>> getIntruderPath(
			IntruderSightingHistory hist, String intruderName,
			double startTime, double endTime);

}
