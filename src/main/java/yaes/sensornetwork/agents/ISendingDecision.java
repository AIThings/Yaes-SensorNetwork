/*
   This file is part of the Yet Another Extensible Simulator
   Created on: Jan 11, 2011
 
   yaes.world.sensornetwork.intrudertracking.ISendingDecision
 
   Copyright (c) 2008-2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.agents;

import java.io.Serializable;

/**
 * 
 * <code>yaes.world.sensornetwork.intrudertracking.ISendingDecision</code> The
 * interface of the classes which implement the sending decision. This can be
 * shared among a number of different agent types, that is why it is kept here.
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public interface ISendingDecision extends Serializable {

	boolean readyToSend(AbstractSensorAgent sensorAgent, Object... params);

	void sent(AbstractSensorAgent sensorAgent, Object... params);

}
