/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Aug 1, 2010
 
   storeanddump.KnowledgeHistory
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.knowledge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import yaes.ui.format.Formatter;

/**
 * 
 * <code>storeanddump.KnowledgeHistory</code>
 * 
 * A representation of the knowledge history in an intruder tracking sensor
 * network.
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class IntruderSightingHistory implements Serializable {

	private List<IntruderSighting> sightings = new ArrayList<IntruderSighting>();
	private Set<String> sighters = new HashSet<String>();
	private Set<String> intruders = new HashSet<String>();
	private static final long serialVersionUID = 5452971487884282958L;

	/**
	 * Records a single point estimate of the location of a target. Estimator
	 * believes at time "estimateTime" that the target was at location
	 * "targetLocation" at time "targetTime".
	 * 
	 * @param estimator
	 * @param estimateTime
	 * @param targetName
	 * @param targetLocation
	 * @param targetTime
	 */
	public void addSighting(IntruderSighting sighting, double currentTime) {
		assert (sighting.getTimeSighting() <= currentTime);
		sighting.setTimeReport(currentTime);
		sightings.add(sighting);
		sighters.add(sighting.getNameSighter());
		intruders.add(sighting.getNameIntruder());
	}

	/**
	 * @return the intruders
	 */
	public Set<String> getIntruders() {
		return intruders;
	}

	/**
	 * @return the sighters
	 */
	public Set<String> getSighters() {
		return sighters;
	}

	/**
	 * Returns all the sightings of a particular intruder
	 * 
	 * @param intruderName
	 * @return
	 */
	public List<IntruderSighting> getSightingsOfIntruder(String intruderName) {
		List<IntruderSighting> retval = new ArrayList<IntruderSighting>();
		for (IntruderSighting is : sightings) {
			if (is.getNameIntruder().equals(intruderName)) {
				retval.add(is);
			}
		}
		return retval;
	}

	/**
	 * Removes a sighting. Note that this will still leaves the intruder
	 * there... but without a sighting
	 * 
	 * FIXME: maybe fix this?
	 * 
	 * @param sinkSighting
	 */
	public void removeSighting(IntruderSighting sinkSighting) {
		sightings.remove(sinkSighting);
	}

	/**
	 * Lists all
	 */
	@Override
	public String toString() {
		Formatter fmt = new Formatter();
		fmt.add("Intruder sighing history");
		fmt.indent();
		for (IntruderSighting is : sightings) {
			fmt.add(is);
		}
		return fmt.toString();
	}

}
