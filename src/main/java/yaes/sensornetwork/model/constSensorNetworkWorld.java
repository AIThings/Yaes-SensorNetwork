/*
   This file is part of the Yet Another Extensible Simulator
   Created on: Sep 21, 2009
 
   yaes.world.sensornetwork.SensorNetworkConstants
 
   Copyright (c) 2008 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.model;

/**
 * 
 * <code>yaes.world.sensornetwork.SensorNetworkConstants</code>
 * 
 * constants for the sensor network world internal implementation
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public interface constSensorNetworkWorld {
	// input constants

	public static final String SENSORNETWORK_MESSAGES_OVERHEARD = "SensorNetwork_MessagesOverheard";
	public static final String SENSORNETWORK_MESSAGES_RECEIVED = "SensorNetwork_MessagesReceived";
	// output constants
	public static final String SENSORNETWORK_MESSAGES_SENT = "SensorNetwork_MessagesSent";
	public static final String SENSORNETWORK_PERCEPTIONS_INTRUDERPRESENCE = "SensorNetwork_Perceptions_IntruderPresence";
	public static final String SENSORNETWORK_TRANSMISSION_ENERGY = "SensorNetwork_TransmissionEnergy";
}
