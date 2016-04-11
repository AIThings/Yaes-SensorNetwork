/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Jan 6, 2011
 
   storeanddump.visualization.paintUncertainMovementSegment
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.visualization;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.Serializable;

import yaes.sensornetwork.knowledge.UncertainMovementSegment;
import yaes.ui.visualization.VisualCanvas;
import yaes.ui.visualization.VisualizationProperties;
import yaes.ui.visualization.painters.IPainter;
import yaes.ui.visualization.painters.PaintSpec;
import yaes.ui.visualization.painters.PainterHelper;

/**
 * 
 * <code>storeanddump.visualization.paintUncertainMovementSegment</code> paints
 * an uncertain movement component
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class paintUncertainMovementSegment implements IPainter, Serializable {

	private PaintSpec umsSpec = PaintSpec.createDraw(Color.blue.darker()
			.darker());

	/*
	 * (non-Javadoc)
	 * 
	 * @see yaes.ui.visualization.painters.IPainter#getLayer()
	 */
	@Override
	public int getLayer() {
		return BACKGROUND_LAYER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see yaes.ui.visualization.painters.IPainter#paint(java.awt.Graphics2D,
	 * java.lang.Object, yaes.ui.visualization.VisualCanvas)
	 */
	@Override
	public void paint(Graphics2D g, Object o, VisualCanvas panel) {
		UncertainMovementSegment ums = (UncertainMovementSegment) o;
		Shape s = ums.getShape();
		PainterHelper.paintShape(s, umsSpec, g, panel);
		PainterHelper.paintArrow(ums.getLocationFrom(), ums.getLocationTo(),
				umsSpec.getBorderColor(), g, panel);
		PainterHelper.paintHtmlLabel("" + ums.getTimeTo(), ums.getLocationTo(),
				5, 5, Color.white, false, 0, null, false, g, panel);
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
		// nothing here
	}

}
