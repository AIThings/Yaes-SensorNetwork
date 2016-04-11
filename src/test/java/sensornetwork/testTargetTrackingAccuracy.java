/*
   This file is part of the Yet Another Extensible Simulator
   Created on: Sep 27, 2009
 
   yaestest.world.sensornetwork.worldmodel.testTargetTrackingAccuracy
 
   Copyright (c) 2008-2009 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package sensornetwork;

import java.awt.geom.Rectangle2D;

import org.junit.Test;

import yaes.framework.simulation.SimulationOutput;
import yaes.sensornetwork.applications.intrudertracking.IntruderNode;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingWorldModel;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingWorldModel.ItwmType;
import yaes.sensornetwork.identification.IdPropFactory;
import yaes.sensornetwork.identification.IntruderNodeType;
import yaes.sensornetwork.knowledge.IntruderTrackingAccuracy;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.ui.text.TextUi;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.PlannedPath;

/**
 * 
 * <code>yaestest.world.sensornetwork.worldmodel.testTargetTrackingAccuracy</code>
 * 
 * @todo describe
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class testTargetTrackingAccuracy {

	@Test
	public void testTargetTrackingAccuracyCases() {
		Rectangle2D.Double interestRectangle = new Rectangle2D.Double(0, 0,
				100, 100);
		SensorNetworkWorld world = new SensorNetworkWorld(
				new SimulationOutput());
        IntruderTrackingWorldModel model = new IntruderTrackingWorldModel(ItwmType.LAST_KNOWN, world);
		IntruderNode intruder = new IntruderNode("Intruder", new PlannedPath(
				new Location(10, 10), new Location(20, 20)), 0, world,
				IntruderNodeType.INTRUDER_HUMAN,
				IdPropFactory.createNonFriendlyHumanIntruder());
		world.addIntruderNode(intruder);
		// both inside
		intruder.setLocation(new Location(40, 40));
		model.addIntruderAtLocation(0, "Intruder", new Location(50, 50));
		double err = IntruderTrackingAccuracy.targetTrackingAccuracy(
				"Intruder", interestRectangle, model, world);
		TextUi.println("Both inside:" + err);
		// intruder outside
		intruder.setLocation(new Location(140, 140));
		err = IntruderTrackingAccuracy.targetTrackingAccuracy("Intruder",
				interestRectangle, model, world);
		TextUi.println("Intruder outside:" + err);
		// both outside
		intruder.setLocation(new Location(140, 140));
		model.addIntruderAtLocation(1, "Intruder", new Location(140, 140));
		err = IntruderTrackingAccuracy.targetTrackingAccuracy("Intruder",
				interestRectangle, model, world);
		TextUi.println("Both outside:" + err);
		// model outside
		intruder.setLocation(new Location(40, 40));
		err = IntruderTrackingAccuracy.targetTrackingAccuracy("Intruder",
				interestRectangle, model, world);
		TextUi.println("Model outside:" + err);

	}
}
