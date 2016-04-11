/*
   This file is part of the Yet Another Extensible Simulator
   Created on: Sep 29, 2009
 
   yaes.world.sensornetwork.SensorNetworkMessageConstants
 
   Copyright (c) 2008-2009 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.model;

/**
 * 
 * <code>yaes.world.sensornetwork.SensorNetworkMessageConstants</code>
 * 
 * Message constants used for the communication between sensor nodes.
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public interface SensorNetworkMessageConstants {
	static final String FIELD_CONTENT = "content";
	static final String FIELD_DURATION = "duration";
	static final String FIELD_EMPTY_INTERVAL = "emptyInterval";
	static final String FIELD_INSTANCE = "instance";
	static final String FIELD_INTENSITY = "intensity";
	static final String FIELD_INTERVAL = "interval"; // interval of
														// time messages
														// are sent
	static final String FIELD_LOCATION = "location";
	static final String FIELD_PERCEPTION_ID = "perceptionId";
	static final String FIELD_RECTANGLE = "rectangle";
	static final String FIELD_TIMESTAMP = "timestamp";
	// message fields for interest
	static final String FIELD_TYPE = "type"; // the type of
												// the intruder
	static final String MESSAGE_CANNOT_SEND = "CannotSend"; // for try and
															// bounce
	static final String MESSAGE_DATA = "Data";
	static final String MESSAGE_HEARTBEAT = "HeartBeat";
	// message types ---> to be put in the content field
	static final String MESSAGE_INTEREST = "Interest";
}
