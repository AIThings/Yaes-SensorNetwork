/*
   This file is part of the Yet Another Extensible Simulator
   Created on: Oct 1, 2009
 
   stealthrouting.agents.directeddiffusion.DDSinkInterestManager
 
   Copyright (c) 2008-2009 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.agents.directeddiffusion;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingMessageConstants;
import yaes.sensornetwork.model.SensorNetworkMessageConstants;
import yaes.world.physical.location.Location;

/**
 * 
 * <code>stealthrouting.agents.directeddiffusion.DDSinkInterestManager</code>
 * 
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class DDSinkInterestManager implements IntruderTrackingMessageConstants,
		Serializable {
	/**
     * 
     */
	private static final long serialVersionUID = -5033785962409070242L;
	protected DDSinkAgent agent;
	private double duration = 0;
	private Interest interest;
	private double interval = Integer.MAX_VALUE;
	private int receivedDataCount = 0;

	/**
	 * @return the receivedDataCount
	 */
	public int getReceivedDataCount() {
		return receivedDataCount;
	}

	public DDSinkInterestManager(String interestType,
			Rectangle2D.Double rectangle, DDSinkAgent agent) {
		interest = new Interest(interestType, rectangle);
		this.agent = agent;
	}

	public double getDuration() {
		return duration;
	}

	/**
	 * @return the interest
	 */
	public Interest getInterest() {
		return interest;
	}

	public double getInterval() {
		return interval;
	}

	/**
	 * Processes the received data.
	 * 
	 * @param m
	 */
	void processReceivedData(ACLMessage m) {
		final int perceptionId = (Integer) m
				.getValue(SensorNetworkMessageConstants.FIELD_PERCEPTION_ID);
		final String name = (String) m
				.getValue(IntruderTrackingMessageConstants.FIELD_INTRUDER_NAME);
		final Location location = (Location) m
				.getValue(IntruderTrackingMessageConstants.FIELD_INTRUDER_LOCATION);
		final double timestamp = (Double) m
				.getValue(IntruderTrackingMessageConstants.FIELD_INTRUDER_TIME);
		String sender = m.getSender();
		getInterest().handlePreviouslyProcessedPerception(perceptionId, sender);
		receivedDataCount++;
		// update the world model
		if (name != null) {
			agent.getWorldModel().addIntruderAtLocation(timestamp, name, location);
		}
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public void setInterval(double interval) {
		this.interval = interval;
	}

}
