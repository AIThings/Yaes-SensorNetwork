/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Jan 18, 2011
 
   storeanddump.scenarios.scenValueOfDeepInspectionContext
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.scenarios.deepinspection;

import yaes.sensornetwork.SensorNetworkContext;
import yaes.sensornetwork.applications.intrudertracking.IntruderHelper;

/**
 * 
 * <code>storeanddump.scenarios.scenValueOfDeepInspectionContext</code>
 * 
 * @todo describe
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class scenValueOfDeepInspectionContext extends SensorNetworkContext {

	private static final long serialVersionUID = -8995084746975223718L;

	/**
	 * Overrides the node, some foraging animals as intruders
	 * 
	 * @param sip
	 */
	@Override
	protected void createIntruderNodes() {
		int intruderNodes = sip.getParameterInt(Intruders_Number);
		for (int i = 1; i <= intruderNodes; i++) {
			IntruderHelper.addForagingAnimal(this, random, i);
		}
	}

}
