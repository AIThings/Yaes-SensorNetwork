/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Jan 6, 2011
 
   storeanddumptest.knowledge.testUncertainMovementSegment
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package sensornetwork.knowledge;

import org.junit.Test;

import yaes.sensornetwork.knowledge.UncertainMovementSegment;
import yaes.sensornetwork.visualization.paintUncertainMovementSegment;
import yaes.ui.text.TextUi;
import yaes.ui.visualization.VisualCanvas;
import yaes.ui.visualization.painters.paintMobileNode;
import yaes.world.physical.location.Location;
import yaes.world.physical.location.NamedLocation;

/**
 * 
 * <code>storeanddumptest.knowledge.testUncertainMovementSegment</code>
 * 
 * Tests the calculations for the uncertain movement segment
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class testUncertainMovementSegment {

	/**
	 * Test visualization
	 */
	@Test
	public void testPrint() {
		VisualCanvas vc = new VisualCanvas(null, null);
		NamedLocation A = new NamedLocation(100, 100, "A");
		NamedLocation B = new NamedLocation(500, 500, "B");
		paintMobileNode nodePainter = new paintMobileNode();
		vc.addObject(A, nodePainter);
		vc.addObject(B, nodePainter);
		UncertainMovementSegment ums = new UncertainMovementSegment(A, 0, B,
				10, 70);
		vc.addObject(ums, new paintUncertainMovementSegment());
		vc.showInADialog();
	}

	@Test
	public void testSimple() {
		Location A = new Location(10, 10);
		Location B = new Location(50, 50);
		UncertainMovementSegment ums = new UncertainMovementSegment(A, 0, B,
				10, 7);
		TextUi.println("Ums:" + ums);
	}

}
