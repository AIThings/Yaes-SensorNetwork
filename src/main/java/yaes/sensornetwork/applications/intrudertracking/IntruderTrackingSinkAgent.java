/*
   This file is part of the Yet Another Extensible Simulator
   Created on: Oct 1, 2009
 
   yaes.world.sensornetwork.intrudertracking.IntruderTrackingSinkAgent
 
   Copyright (c) 2008-2009 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.applications.intrudertracking;

import java.awt.geom.Rectangle2D;

import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingWorldModel.ItwmType;
import yaes.sensornetwork.model.Perception;
import yaes.sensornetwork.model.SensorNetworkMessageConstants;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.SinkNode;
import yaes.world.physical.location.Location;

/**
 * 
 * <code>yaes.world.sensornetwork.intrudertracking.IntruderTrackingSinkAgent</code>
 * 
 * the simplest possible sink agent for intruder tracking
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class IntruderTrackingSinkAgent extends AbstractSensorAgent implements
		IntruderTrackingMessageConstants {

	/**
     * 
     */
	private static final long serialVersionUID = 4795324226231303376L;

	/**
	 * Creates a sink node with the corresponding directed diffusion agent
	 * 
	 * @param name
	 * @param sensorWorld
	 * @param transmissionRange
	 * @param location
	 * @return
	 */
	public static SinkNode createSinkNode(String name,
			SensorNetworkWorld sensorWorld, double transmissionRange,
			Location location, Rectangle2D.Double overallInterestRectangle, ItwmType itwmType) {
		final SinkNode sinkNode = new SinkNode();
		sinkNode.setName(name);
		// not quite the right solution
		IntruderTrackingSinkAgent sinkAgent = new IntruderTrackingSinkAgent(
				name, sensorWorld, overallInterestRectangle, itwmType);
		sinkAgent.setTransmissionRange(transmissionRange);
		sinkNode.setAgent(sinkAgent);
		sinkAgent.setNode(sinkNode);		
		sinkNode.setLocation(location);
		
		sensorWorld.getDirectory().addAgent(sinkAgent);
		sensorWorld.setSinkNode(sinkNode);
		return sinkNode;
	}

	private Rectangle2D.Double interestRectangle;
	private IntruderTrackingWorldModel worldModel;

	/**
	 * Creates the directed diffusion agent. Takes as parameter the name of the
	 * agent, the sensor network world manager and the list of the interest
	 * rectangles.
	 * 
	 * @param name
	 * @param sensorWorld
	 */
	public IntruderTrackingSinkAgent(String name,
			SensorNetworkWorld sensorWorld, Rectangle2D.Double interestRectangle, ItwmType itwmType) {
		super(name, sensorWorld);
		this.interestRectangle = interestRectangle;
		this.worldModel = new IntruderTrackingWorldModel(itwmType, sensorWorld);
	}

	/**
	 * @return the interestRectangle
	 */
	public Rectangle2D.Double getInterestRectangle() {
		return interestRectangle;
	}

	/**
	 * @return the worldModel
	 */
	public IntruderTrackingWorldModel getWorldModel() {
		return worldModel;
	}

	/**
	 * @param p
	 */
	@Override
	protected void handleIntruderPresence(Perception p) {
		// does not handle the intruder itself
	}

	/**
	 * @param message
	 */
	@Override
	protected void handleOverheardMessage(ACLMessage message) {
		// it is not concerned with overheard messages
	}

	@Override
	protected void handleReceivedMessage(final ACLMessage m) {
		if (m.getValue(SensorNetworkMessageConstants.FIELD_CONTENT).equals(
				SensorNetworkMessageConstants.MESSAGE_DATA)) {
			// final String interestType = (String) m.getValue(FIELD_TYPE);
			// final int perceptionId = (Integer)
			// m.getValue(FIELD_PERCEPTION_ID);
			final String name = (String) m
					.getValue(IntruderTrackingMessageConstants.FIELD_INTRUDER_NAME);
			final Location location = (Location) m
					.getValue(IntruderTrackingMessageConstants.FIELD_INTRUDER_LOCATION);
			final double timestamp = (Double) m
					.getValue(IntruderTrackingMessageConstants.FIELD_INTRUDER_TIME);
			// String sender = m.getSender();
			worldModel.addIntruderAtLocation(timestamp, name, location);
		}
	}
}
