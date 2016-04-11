/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Jan 8, 2011
 
   storeanddump.agents.IntruderTrackingSink
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.agents;

import java.awt.geom.Rectangle2D;

import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingMessageConstants;
import yaes.sensornetwork.knowledge.IntruderSighingHelper;
import yaes.sensornetwork.knowledge.IntruderSighting;
import yaes.sensornetwork.knowledge.IntruderSightingHistory;
import yaes.sensornetwork.model.Perception;
import yaes.sensornetwork.model.SensorNetworkMessageConstants;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.SinkNode;
import yaes.world.physical.location.Location;

/**
 * 
 * <code>storeanddump.agents.IntruderTrackingSink</code> An intruder tracking
 * sink - a quiet one.
 * 
 * Basically just contains a knowledge model
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class SDIntruderTrackingSinkAgent extends AbstractSensorAgent implements
		IntruderTrackingMessageConstants {

	private static final long serialVersionUID = -1580649450646199303L;

	/**
	 * Creates a sink node with the corresponding sink agent
	 * 
	 * @param name
	 * @param sensorWorld
	 * @param transmissionRange
	 * @param location
	 * @return
	 */
	public static SinkNode createSinkNode(String name,
			SensorNetworkWorld sensorWorld, double transmissionRange,
			Location location, Rectangle2D.Double overallInterestRectangle) {
		final SinkNode sinkNode = new SinkNode();
		sinkNode.setName(name);
		// not quite the right solution
		SDIntruderTrackingSinkAgent sinkAgent = new SDIntruderTrackingSinkAgent(
				name, sensorWorld, overallInterestRectangle);
		sinkAgent.setTransmissionRange(transmissionRange);
		sinkNode.setAgent(sinkAgent);
		sinkAgent.setNode(sinkNode);
		sinkNode.setLocation(location);
		sensorWorld.getDirectory().addAgent(sinkAgent);
		sensorWorld.setSinkNode(sinkNode);
		return sinkNode;
	}

	private Rectangle2D.Double interestRectangle;
	private IntruderSightingHistory intruderSightingHistory;

	/**
	 * Creates a sink agent. Takes as parameter the name of the
	 * agent, the sensor network world manager and the list of the interest
	 * rectangles.
	 * 
	 * @param name
	 * @param sensorWorld
	 */
	public SDIntruderTrackingSinkAgent(String name,
			SensorNetworkWorld sensorWorld, Rectangle2D.Double interestRectangle) {
		super(name, sensorWorld);
		this.interestRectangle = interestRectangle;
		intruderSightingHistory = new IntruderSightingHistory();
	}

	/**
	 * @return the interestRectangle
	 */
	public Rectangle2D.Double getInterestRectangle() {
		return interestRectangle;
	}

	/**
	 * @return the intruderSightingHistory
	 */
	public IntruderSightingHistory getIntruderSightingHistory() {
		return intruderSightingHistory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * yaes.world.sensornetwork.AbstractSensorAgent#handleIntruderPresence(yaes
	 * .world.sensornetwork.Perception)
	 */
	@Override
	protected void handleIntruderPresence(Perception p) {
		// doesn't handle intruder presence directly
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * yaes.world.sensornetwork.AbstractSensorAgent#handleOverheardMessage(yaes
	 * .framework.agent.ACLMessage)
	 */
	@Override
	protected void handleOverheardMessage(ACLMessage message) {
		// doesn't handle overheard messages
	}

	@Override
	protected void handleReceivedMessage(final ACLMessage m) {
		if (m.getValue(SensorNetworkMessageConstants.FIELD_CONTENT).equals(
				SensorNetworkMessageConstants.MESSAGE_DATA)) {
			Object intruderName = m
					.getValue(IntruderTrackingMessageConstants.FIELD_INTRUDER_NAME);
			if (intruderName != null) {
				IntruderSighting is = IntruderSighingHelper
						.extractSightingFromMessage(m);
				intruderSightingHistory.addSighting(is, getSensorWorld()
						.getTime());
			} else {
				// heartbeat message arrived
			}
		}
	}

}
