/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Dec 30, 2010
 
   storeanddump.visualization.paintEnvironment
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.visualization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import yaes.sensornetwork.Environment;
import yaes.ui.visualization.VisualCanvas;
import yaes.ui.visualization.VisualizationProperties;
import yaes.ui.visualization.painters.IPainter;
import yaes.ui.visualization.painters.PainterHelper;
import yaes.ui.visualization.painters.paintPath;
import yaes.world.physical.location.NamedLocation;
import yaes.world.physical.path.PlannedPath;

/**
 * 
 * <code>storeanddump.visualization.paintEnvironment</code>
 * 
 * @todo describe
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class paintEnvironment implements IPainter, Serializable {

	protected final String vpropBackgroundMap = "vpropBackgroundMap";
	protected final String vpropInterestArea = "vpropInterestArea";
	protected final String vpropLandmarks = "vpropLandmarks";
	protected final String vpropProtectedAreas = "vpropProtectedAreas";
	protected final String vpropRoads = "vpropRoads";
	protected final String vpropPaths = "vpropPaths";

	private paintPath pathPainter;

	public paintEnvironment() {
		Stroke stroke = new BasicStroke(10);
		pathPainter = new paintPath(Color.red.brighter().brighter(), stroke);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see yaes.ui.visualization.painters.IPainter#getLayer()
	 */
	@Override
	public int getLayer() {
		return BACKGROUND_LAYER;
	}

	@Override
	public void paint(Graphics2D g, Object o, VisualCanvas panel) {
		VisualizationProperties vprops = panel.getVisualizer()
				.getVisualizationProperties();
		boolean paintBackgroundMap = (Boolean) vprops
				.getPropertyValue(vpropBackgroundMap);
		boolean paintInterestArea = (Boolean) vprops
				.getPropertyValue(vpropInterestArea);
		boolean paintLandmarks = (Boolean) vprops
				.getPropertyValue(vpropLandmarks);
		boolean paintProtectedAreas = (Boolean) vprops
				.getPropertyValue(vpropProtectedAreas);
		boolean paintRoads = (Boolean) vprops.getPropertyValue(vpropRoads);
		boolean paintPaths = (Boolean) vprops.getPropertyValue(vpropPaths);

		Environment env = (Environment) o;
		//
		// Paint the background map
		//
		if (paintBackgroundMap) {
			Image theImage = env.getTheMap().getBackgroundImage();
			if (theImage != null) {
				g.drawImage(theImage, panel.getTheTransform(), null);
			}
		}
		//
		// Paint the interest area
		//
		if (paintInterestArea) {
			PainterHelper.paintRectangle(env.getInterestArea(), false, null,
					true, new Color(255, 255, 240), g, panel);
		}
		//
		// Paint the protected areas
		//
		if (paintProtectedAreas) {
			for (Rectangle2D.Double rect : env.getProtectedAreas().values()) {
				PainterHelper.paintRectangle(rect, false, null, true,
						Color.cyan.brighter().brighter(), g, panel);
			}
		}
		//
		// Paints the landmarks
		//
		if (paintLandmarks) {
			for (NamedLocation namedLocation : env.getLandmarks().values()) {
				PainterHelper.paintRectangleAtLocation(namedLocation, 10,
						Color.black, Color.blue, g, panel);
			}
		}
		//
		// Paints the roads (should be thicker)
		//
		if (paintRoads) {
			for (PlannedPath road : env.getRoads().values()) {
				pathPainter.paint(g, road, panel);
			}
		}
		//
		// Paints the paths
		//
		if (paintPaths) {
			for (PlannedPath road : env.getRoads().values()) {
				pathPainter.paint(g, road, panel);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see yaes.ui.visualization.painters.IPainter#registerParameters(yaes.ui.
	 * visualization.VisualizationProperties)
	 */
	@Override
	public void registerParameters(
			VisualizationProperties visualizationProperties) {
		visualizationProperties.addVisualizationProperty(vpropBackgroundMap,
				"Paint background map?", "Environment", new Boolean(false));
		visualizationProperties.addVisualizationProperty(vpropInterestArea,
				"Paint interest area?", "Environment", new Boolean(true));
		visualizationProperties.addVisualizationProperty(vpropLandmarks,
				"Paint landmarks?", "Environment", new Boolean(false));
		visualizationProperties.addVisualizationProperty(vpropProtectedAreas,
				"Paint protected areas?", "Environment", new Boolean(false));
		visualizationProperties.addVisualizationProperty(vpropRoads,
				"Paint roads?", "Environment", new Boolean(false));
		visualizationProperties.addVisualizationProperty(vpropPaths,
				"Paint paths?", "Environment", new Boolean(false));

	}

}
