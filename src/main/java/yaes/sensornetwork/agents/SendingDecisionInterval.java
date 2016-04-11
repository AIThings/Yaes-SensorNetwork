/*
   This file is part of the Yet Another Extensible Simulator
   Created on: Jan 11, 2011
 
   yaes.world.sensornetwork.SendingDecisionInterval
 
   Copyright (c) 2008-2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.agents;

/**
 * 
 * <code>yaes.world.sensornetwork.SendingDecisionInterval</code>
 * 
 * Implements a time interval based sending decision
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class SendingDecisionInterval implements ISendingDecision {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1029870247316166821L;
	private double interval;
	private double lastSent = -Double.MAX_VALUE;

	/**
	 * This assume that if once has returned true, the sending will actually
	 * happen, so it updates the lastSent feature
	 * 
	 * @return
	 */
	@Override
	public boolean readyToSend(AbstractSensorAgent sensorAgent,
			Object... params) {
		if (sensorAgent.getWorld().getTime() - lastSent < interval) {
			return false;
		}
		return true;
	}

	/**
	 * @param sensorAgent
	 * @param params
	 */
	@Override
	public void sent(AbstractSensorAgent sensorAgent, Object... params) {
		lastSent = sensorAgent.getWorld().getTime();
	}

	/**
	 * @param interval
	 *            the interval to set
	 */
	public void setInterval(double interval) {
		this.interval = interval;
	}

}
