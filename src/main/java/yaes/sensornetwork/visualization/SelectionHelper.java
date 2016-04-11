/*
   This file is part of the Yet Another Extensible Simulator
   Created on: Feb 27, 2009
 
   yaes.ui.visualization.applicationspecific.sensornetwork.SelectionHelper
 
   Copyright (c) 2008 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.visualization;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import yaes.ui.visualization.Inspector;
import yaes.ui.visualization.VisualCanvas;
import yaes.ui.visualization.painters.PainterHelper;
import yaes.world.physical.location.Location;

/**
 * 
 * <code>yaes.ui.visualization.applicationspecific.sensornetwork.SelectionHelper</code>
 * 
 * helper to allow to mark the selection
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class SelectionHelper {

	/**
	 * Returns true if the currently selected object is also selected in the
	 * inspector
	 * 
	 * @param o
	 * @param panel
	 * @return
	 */
	public static boolean isSelectedInInspector(Object o, VisualCanvas panel) {
		Inspector inspector = panel.getVisualizer().getInspector();
		if (inspector == null) {
			return false;
		}
		if (o.equals(inspector.getCurrentlySelected())) {
			return true;
		}
		return false;
	}

	/**
	 * Paints a circular selection
	 * 
	 * @param g
	 * @param o
	 * @param panel
	 * @param location
	 * @param size
	 */
	public static void paintSelection(Graphics2D g, Object o,
			VisualCanvas panel, Location location, int size) {
		if (!SelectionHelper.isSelectedInInspector(o, panel)) {
			return;
		}
		Shape s = new Ellipse2D.Double(0, 0, size, size);
		PainterHelper.paintShapeAtLocation(location, s, Color.magenta, null, g,
				panel);
	}

}
