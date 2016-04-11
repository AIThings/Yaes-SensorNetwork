/*
   This file is part of the Yet Another Extensible Simulator
   Created on: Sep 29, 2009
 
   yaes.world.sensornetwork.intrudertracking.IntruderTrackingMessageConstants
 
   Copyright (c) 2008-2009 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.applications.intrudertracking;

import yaes.sensornetwork.model.SensorNetworkMessageConstants;

/**
 * 
 * <code>yaes.world.sensornetwork.intrudertracking.IntruderTrackingMessageConstants</code>
 * 
 * Extends the basic sensor network message constants with constants which
 * explicitly refer to the tracking of the intruders
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public interface IntruderTrackingMessageConstants extends
		SensorNetworkMessageConstants {

	static final String FIELD_INTRUDER_LOCATION = "intruderLocation";
	static final String FIELD_INTRUDER_NAME = "intruderName";
	/**
	 * The node which made the original observation
	 */
	static final String FIELD_INTRUDER_OBSERVER = "intruderObserver";
	/**
	 * The time at which the original observation has been made
	 */
	static final String FIELD_INTRUDER_TIME = "intruderTime";
	/**
	 * The path record - used for instance in TAB
	 */
	static final String FIELD_PATH_RECORD = "pathRecord";
	// some constants for the state of interest
	static final String INTEREST_TYPE_INTRUDER = "Intruder";
}
