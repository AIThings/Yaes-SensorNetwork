/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Jan 8, 2011
 
   storeanddump.visualization.paintTrackingError
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.visualization;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import yaes.sensornetwork.knowledge.AccuracyMetric;
import yaes.sensornetwork.knowledge.IntruderTrackingAccuracy;
import yaes.sensornetwork.knowledge.TrackingError;
import yaes.sensornetwork.knowledge.UncertainMovementSegment;
import yaes.ui.text.TextUi;
import yaes.ui.visualization.VisualCanvas;
import yaes.ui.visualization.VisualizationProperties;
import yaes.ui.visualization.painters.IPainter;
import yaes.ui.visualization.painters.PainterHelper;
import yaes.world.physical.location.Location;

/**
 * 
 * <code>storeanddump.visualization.paintTrackingError</code>
 * 
 * @todo describe
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class paintTrackingError implements IPainter, Serializable {
	private paintUncertainMovementSegment paintUMS = new paintUncertainMovementSegment();
	private static final String vpropPaintTrackingError = "vpropPaintTrackingError";
	private static final String vpropPaintTrackingErrorUncertainArea = "vpropPaintTrackingErrorUncertainArea";
	private static final String vpropInterestAreaConstrained = "vpropInterestAreaConstrained";

	/*
	 * (non-Javadoc)
	 * 
	 * @see yaes.ui.visualization.painters.IPainter#getLayer()
	 */
	@Override
	public int getLayer() {
		return FOREGROUND_LAYER; // should be the absolute foreground
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see yaes.ui.visualization.painters.IPainter#paint(java.awt.Graphics2D,
	 * java.lang.Object, yaes.ui.visualization.VisualCanvas)
	 */
	@Override
	public void paint(Graphics2D g, Object o, VisualCanvas panel) {
		TrackingError te = (TrackingError) o;
		// if turn of painting
		VisualizationProperties vprops = panel.getVisualizer()
				.getVisualizationProperties();
		boolean paintTrackingError = (Boolean) vprops
				.getPropertyValue(vpropPaintTrackingError);
		boolean paintUncertainArea = (Boolean) vprops
		.getPropertyValue(vpropPaintTrackingErrorUncertainArea);
		if (!paintTrackingError) {
			return;
		}
		boolean paintInterestAreaConstrained = (Boolean) vprops
				.getPropertyValue(vpropInterestAreaConstrained);

		if (paintInterestAreaConstrained) {
			paintInterestAreaError(g, te, panel);
		} else {
			paintAbsoluteError(g, te, panel);
		}
		if (paintUncertainArea) {
		paintUncertainArea(g, te, panel);
		}

	}

	/**
	 * Paints the absolute error, without regard to an interest area
	 * 
	 * @param g
	 * @param te
	 * @param panel
	 */
	private void paintAbsoluteError(Graphics2D g, TrackingError te,
			VisualCanvas panel) {
		double time = te.time;
		for (String intruder : te.groundTruthIsh.getIntruders()) {
			Location real = te.groundTruthEst.getIntruderLocation(
					te.groundTruthIsh, intruder, time);
			Location estimate = te.estimateEst.getIntruderLocation(
					te.estimateIsh, intruder, time);
			if (estimate == null) {
				// draw a red mark around the location
				PainterHelper.paintRectangleAtLocation(real, 10, Color.red,
						null, g, panel);
			} else {
				PainterHelper.paintArrow(real, estimate, Color.red, g, panel);
			}
		}
	}

	/**
	 * Paints the error with regards to a certain interest area
	 * 
	 * @param g
	 * @param te
	 * @param panel
	 */
	private void paintInterestAreaError(Graphics2D g, TrackingError te,
			VisualCanvas panel) {
		double time = te.time;
		Rectangle2D.Double interestRectangle = te.environment.getInterestArea();
		// paints the interest area
		PainterHelper.paintRectangle(interestRectangle, true, Color.red, true,
				Color.red, (float) 0.2, g, panel);
		// paints the arrow
		for (String intruder : te.groundTruthIsh.getIntruders()) {
			Location groundTruthLocation = te.groundTruthEst
					.getIntruderLocation(te.groundTruthIsh, intruder, time);
			Location measuredLocation = te.estimateEst.getIntruderLocation(
					te.estimateIsh, intruder, time);

			// CASE 0: neither of them exists
			if ((groundTruthLocation == null) && (measuredLocation == null)) {
				continue;
			}
			// CASE 1: intruder exists, no target model
			if (measuredLocation == null) {
				// if the target is not in the interest rectangle, return 0
				if (!interestRectangle.contains(groundTruthLocation.asPoint())) {
					continue;
				} else {
					// paint a line to the distance of the inside of the
					// rectangle
					Location closestLocationOnRectangle = IntruderTrackingAccuracy
							.findClosestPointOnTheRectangle(
									groundTruthLocation, interestRectangle);
					PainterHelper.paintArrow(groundTruthLocation,
							closestLocationOnRectangle, Color.red, g, panel);
					continue;
				}
			}
			// CASE 2: target model and target are in the interest rectangle
			if (interestRectangle.contains(measuredLocation.asPoint())
					&& interestRectangle
							.contains(groundTruthLocation.asPoint())) {
				PainterHelper.paintArrow(groundTruthLocation, measuredLocation,
						Color.red, g, panel);
				continue;
			}
			// CASE 3: target model outside, intruder inside
			if (!interestRectangle.contains(measuredLocation.asPoint())
					&& interestRectangle
							.contains(groundTruthLocation.asPoint())) {
				Location closestLocationOnRectangle = IntruderTrackingAccuracy
						.findClosestPointOnTheRectangle(measuredLocation,
								interestRectangle);
				// the red arrow shows the error, the blue connects to the real
				// location
				PainterHelper.paintArrow(groundTruthLocation,
						closestLocationOnRectangle, Color.red, g, panel);
				PainterHelper.paintArrow(groundTruthLocation, measuredLocation,
						Color.blue, g, panel);
				continue;
			}
			// CASE 4: target model inside, intruder outside
			if (interestRectangle.contains(measuredLocation.asPoint())
					&& !interestRectangle.contains(groundTruthLocation
							.asPoint())) {
				Location closestLocationOnRectangle = IntruderTrackingAccuracy
						.findClosestPointOnTheRectangle(groundTruthLocation,
								interestRectangle);
				PainterHelper.paintArrow(measuredLocation,
						closestLocationOnRectangle, Color.red, g, panel);
				PainterHelper.paintArrow(groundTruthLocation, measuredLocation,
						Color.blue, g, panel);
				continue;
			}
			// CASE 5: both outside
			if (!interestRectangle.contains(measuredLocation.asPoint())
					&& !interestRectangle.contains(groundTruthLocation
							.asPoint())) {
				continue;
			}
		}
	}

	/**
	 * Paints the uncertain area
	 * 
	 * @param g
	 * @param te
	 * @param panel
	 */
	private void paintUncertainArea(Graphics2D g, TrackingError te,
			VisualCanvas panel) {
		double speed = 15.0;
		for (String intruder : te.estimateIsh.getIntruders()) {
			TextUi.println("Intruder: " + intruder);
			SimpleEntry<List<UncertainMovementSegment>, Double> value = AccuracyMetric
					.uncertaintyArea(0, te.time, intruder, speed,
							te.estimateEst, te.estimateIsh);
			for (UncertainMovementSegment ums : value.getKey()) {
				// TextUi.println(ums);
				paintUMS.paint(g, ums, panel);
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
		visualizationProperties.addVisualizationProperty(
				vpropPaintTrackingError, "Paint tracking error?",
				"Tracking error", new Boolean(false));
		visualizationProperties.addVisualizationProperty(
				vpropPaintTrackingErrorUncertainArea, "Paint uncertainty area?",
				"Tracking error", new Boolean(false));
		visualizationProperties.addVisualizationProperty(
				vpropInterestAreaConstrained, "Interest area constrained?",
				"Tracking error", new Boolean(false));
	}

}
