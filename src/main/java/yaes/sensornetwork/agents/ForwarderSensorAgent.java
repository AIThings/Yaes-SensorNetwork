/*
   This file is part of the Yet Another Extensible Simulator
   Created on: Oct 1, 2009
 
   yaes.world.sensornetwork.ForwarderSensorAgent
 
   Copyright (c) 2008-2009 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.agents;

import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.model.SensorNetworkMessageConstants;
import yaes.sensornetwork.model.SensorNetworkWorld;

/**
 * 
 * <code>yaes.world.sensornetwork.ForwarderSensorAgent</code>
 * 
 * An agent which has a single forwarding destination, the one to the sink. Its
 * destinations are created by
 * SensorRoutingHelper.createPathsForForwarderSensorAgents
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public abstract class ForwarderSensorAgent extends AbstractSensorAgent {

	/**
     * 
     */
	private static final long serialVersionUID = -8690766803137243097L;
	protected String forwardingDestination;

	/**
	 * @param name
	 * @param sensorWorld
	 */
	public ForwarderSensorAgent(String name, SensorNetworkWorld sensorWorld) {
		super(name, sensorWorld);
	}

	/**
	 * This is separated here to allow being called from inherinting agents in
	 * various situations
	 * 
	 * @param message
	 */
	protected void forwardMessage(ACLMessage message) {
		if (message.getValue(SensorNetworkMessageConstants.FIELD_CONTENT)
				.equals(SensorNetworkMessageConstants.MESSAGE_DATA)) {
			ACLMessage forward = new ACLMessage(forwardingDestination,
					getName(), message);
			transmit(forward);
			return;
		}
		throw new Error("ForwarderSensorAgent: unknown message arrived:" + message);
	}


	/**
	 * @return the forwardingDestination
	 */
	public String getForwardingDestination() {
		return forwardingDestination;
	}

	/**
	 * @param message
	 */
	@Override
	protected void handleOverheardMessage(ACLMessage message) {
		// does not handle overheard messages
	}

	@Override
	protected void handleReceivedMessage(final ACLMessage message) {
		forwardMessage(message);
	}

	/**
	 * @param forwardingDestination
	 *            the forwardingDestination to set
	 */
	public void setForwardingDestination(String forwardingDestination) {
		this.forwardingDestination = forwardingDestination;
	}

	
	/**
	 * Prints the forwarding destination
	 * @return
	 */
	protected String toStringForwardingDestination() {
	    return "forwardingDestination = " + forwardingDestination;
	}
	
}
