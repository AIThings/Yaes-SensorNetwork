/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Jan 8, 2011
 
   storeanddump.knowledge.TrackingError
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.knowledge;

import java.io.Serializable;

import yaes.sensornetwork.Environment;
import yaes.world.physical.location.Location;

/**
 * 
 * <code>storeanddump.knowledge.TrackingError</code>
 * 
 * A class used in the simulation to measure the tracking error
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class TrackingError implements Serializable {
	private static final long serialVersionUID = 4151298849101169304L;
	public IntruderSightingHistory groundTruthIsh;
	public ILocationEstimator groundTruthEst;
	public IntruderSightingHistory estimateIsh;
	public ILocationEstimator estimateEst;
	public Environment environment;
	public double time;

	/**
	 * Returns the sum of the absolute tracking errors for all the intruders
	 */
	public double getSumAbsoluteTrackingErrorForAllIntruders() {
		double sumErrors = 0;
		for (String intruder : groundTruthIsh.getIntruders()) {
			Location real = groundTruthEst.getIntruderLocation(groundTruthIsh,
					intruder, time);
			Location estimate = estimateEst.getIntruderLocation(estimateIsh,
					intruder, time);
			if ((real != null) && (estimate != null)) {
				sumErrors += real.distanceTo(estimate);
			}
		}
		return sumErrors;
	}

	/**
	 * Returns the sum of the absolute tracking errors for all the intruders
	 */
	public double getSumInterestAreaTrackingErrorForAllIntruders() {
		double sumErrors = 0;
		for (String intruder : groundTruthIsh.getIntruders()) {
			double value = AccuracyMetric.currentInterestAreaEstimationError(
					environment.getInterestArea(), time, intruder, estimateEst,
					estimateIsh, groundTruthEst, groundTruthIsh);
			sumErrors += value;
		}
		return sumErrors;
	}

}
