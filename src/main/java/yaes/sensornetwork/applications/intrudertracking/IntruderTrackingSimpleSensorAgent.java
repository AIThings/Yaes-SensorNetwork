/*
   This file is part of the Yet Another Extensible Simulator
   Created on: Oct 1, 2009
 
   yaes.world.sensornetwork.intrudertracking.IntruderTrackingSimpleSensorAgent
 
   Copyright (c) 2008-2009 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.applications.intrudertracking;

import java.util.HashMap;
import java.util.Map;

import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.agents.ForwarderSensorAgent;
import yaes.sensornetwork.agents.SendingDecisionInterval;
import yaes.sensornetwork.model.Perception;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.ui.format.Formatter;
import yaes.world.physical.location.INamedMoving;

/**
 * 
 * <code>yaes.world.sensornetwork.intrudertracking.IntruderTrackingSimpleSensorAgent</code>
 * 
 * the simplest possible intruder tracking sensor agent - ok but it is still
 * doing a way to collect the latest observation and send it periodically
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class IntruderTrackingSimpleSensorAgent extends ForwarderSensorAgent {

	/**
     * 
     */
	private static final long serialVersionUID = 2624709769402557340L;
	/**
     * 
     */
	protected Map<String, ACLMessage> pendingMessages = new HashMap<String, ACLMessage>();

	/**
	 * @param name
	 * @param sensorWorld
	 */
	public IntruderTrackingSimpleSensorAgent(String name,
			SensorNetworkWorld sensorWorld) {
		super(name, sensorWorld);
		sendingDecision = new SendingDecisionInterval();
	}

	/**
	 * If it is time to send, send them all
	 */
	@Override
	protected void afterProcessingPerceptions() {
		if (!sendingDecision.readyToSend(this)) {
			return;
		}
		flushPendingMessages();
		sendingDecision.sent(this);
	}

	/**
	 * Flush all the pending messages
	 * 
	 */
	protected void flushPendingMessages() {
		for (String intruderName : pendingMessages.keySet()) {
			ACLMessage report = pendingMessages.get(intruderName);
			transmit(report);
		}
		pendingMessages.clear();
	}

	/**
	 * Handles the perception of the presence of an intruder
	 * 
	 * @param iNamedMoving
	 */
	@Override
	protected void handleIntruderPresence(final Perception p) {
		INamedMoving intruder = p.getMovingObject();
		
		ACLMessage report = IntruderTrackingMessageHelper
				.createPerceptionReportMessage(getName(),
						getForwardingDestination(), "interestType", p);
		pendingMessages.put(intruder.getName(), report);
	}

	/**
	 * @param interval
	 *            the interval to set
	 */
	public void setInterval(double interval) {
		((SendingDecisionInterval) sendingDecision).setInterval(interval);
	}

	
	/**
	 * Prints out an agent for debugging purposes
	 */
	@Override
	public String toString() {
	    Formatter fmt = new Formatter();
        fmt.add("IntruderTrackingSimpleSensorAgent");
        return fmt.toString();
	}
	
}
