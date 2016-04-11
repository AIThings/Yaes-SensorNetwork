/*
   This file is part of the Yet Another Extensible Simulator
   Created on: Mar 16, 2009
 
   yaes.world.sensornetwork.worldmodel.IntruderTrackingAccuracy
 
   Copyright (c) 2008 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.knowledge;

import java.awt.geom.Rectangle2D;

import yaes.sensornetwork.applications.intrudertracking.IntruderNode;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingWorldModel;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.world.physical.location.Location;

/**
 * 
 * <code>yaes.world.sensornetwork.worldmodel.IntruderTrackingAccuracy</code>
 * 
 * @todo describe
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class IntruderTrackingAccuracy {

	/**
	 * 
	 * @param worldLocation
	 * @param interestRectangle
	 */
	private static double distanceFromRectangleInside(Location worldLocation,
			Rectangle2D.Double interestRectangle) {
		double minDistance = Double.MAX_VALUE;
		minDistance = Math.min(minDistance, worldLocation.asPoint().getX()
				- interestRectangle.x);
		minDistance = Math.min(minDistance, -worldLocation.asPoint().getX()
				+ interestRectangle.x + interestRectangle.width);
		minDistance = Math.min(minDistance, worldLocation.asPoint().getY()
				- interestRectangle.y);
		minDistance = Math.min(minDistance, -worldLocation.asPoint().getY()
				+ interestRectangle.y + interestRectangle.height);
		return minDistance;
	}

	/**
	 * Find closest point on rectangle
	 * 
	 * Takes a rectangle and a location which is inside the rectangle. Returns
	 * the point on the rectangle which is the closest to the location on the
	 * rectangel
	 * 
	 * @param worldLocation
	 * @param interestRectangle
	 */
	public static Location findClosestPointOnTheRectangle(
			Location worldLocation, Rectangle2D.Double interestRectangle) {
		double x = 0;
		double y = 0;
		if ((worldLocation.getX() > interestRectangle.getMinX())
				&& (worldLocation.getX() < interestRectangle.getMaxX())) {
			x = worldLocation.getX();
		} else {
			if (Math.abs(interestRectangle.getMaxX() - worldLocation.getX()) < Math
					.abs(worldLocation.getX() - interestRectangle.getMinX())) {
				x = interestRectangle.getMaxX();
			} else {
				x = interestRectangle.getMinX();
			}
		}
		if ((worldLocation.getY() > interestRectangle.getMinY())
				&& (worldLocation.getY() < interestRectangle.getMaxY())) {
			y = worldLocation.getY();
		} else {
			if (Math.abs(interestRectangle.getMaxY() - worldLocation.getY()) < Math
					.abs(worldLocation.getY() - interestRectangle.getMinY())) {
				y = interestRectangle.getMaxY();
			} else {
				y = interestRectangle.getMinY();
			}
		}
		return new Location(x, y);
	}

	/**
	 * Calculates the distance between the actuator node, inside the interest
	 * rectangle.
	 * 
	 * If the actuator node is not in the interest rectangle, returns 0.
	 * 
	 * If there is no model, returns the smallest distance of the node to the
	 * sides of the interest rectangle.
	 * 
	 * @param name
	 * @param interestRectangle
	 * @param model
	 * @param world
	 * @return
	 */
	public static double targetTrackingAccuracy(String name,
			Rectangle2D.Double interestRectangle,
			IntruderTrackingWorldModel model, SensorNetworkWorld world) {
		Location worldLocation = null;
		for (IntruderNode node : world.getIntruderNodes()) {
			if (node.getName().equals(name)) {
				worldLocation = node.getLocation();
				break;
			}
		}
		// find the model location
		
		//IntruderModel targetModel = model.getIntruders().get(name);
		//Location targetLocation = null;
		//if (targetModel != null) {
		//	targetLocation = targetModel.getLocation();
		//}
		Location targetLocation = model.estimateIntruderLocation(name);
		
		return IntruderTrackingAccuracy.targetTrackingAccuracyDirect(
				worldLocation, targetLocation, interestRectangle);
	}

	/**
	 * Calculates the distance between the actuator node, inside the interest
	 * rectangle.
	 * 
	 * If the actuator node is not in the interest rectangle, returns 0.
	 * 
	 * If there is no model, returns the smallest distance of the node to the
	 * sides of the interest rectangle.
	 * 
	 * @param name
	 * @param interestRectangle
	 * @param model
	 * @param world
	 * @return
	 */
	public static double targetTrackingAccuracyDirect(
			Location groundTruthLocation, Location measuredLocation,
			Rectangle2D.Double interestRectangle) {
		// find the model location
		// CASE 0: neither of them exists
		if ((groundTruthLocation == null) && (measuredLocation == null)) {
			return 0;
		}
		// CASE 1: intruder exists, no target model
		if (measuredLocation == null) {
			// if the target is not in the interest rectangle, return 0
			if (!interestRectangle.contains(groundTruthLocation.asPoint())) {
				return 0;
			} else {
				return IntruderTrackingAccuracy.distanceFromRectangleInside(
						groundTruthLocation, interestRectangle);
			}
		}
		// CASE 2: target model and target are in the interest rectangle
		if (interestRectangle.contains(measuredLocation.asPoint())
				&& interestRectangle.contains(groundTruthLocation.asPoint())) {
			return measuredLocation.distanceTo(groundTruthLocation);
		}
		// CASE 3: target model outside, intruder inside
		if (!interestRectangle.contains(measuredLocation.asPoint())
				&& interestRectangle.contains(groundTruthLocation.asPoint())) {
			return IntruderTrackingAccuracy.distanceFromRectangleInside(
					groundTruthLocation, interestRectangle);
		}
		// CASE 4: target model inside, intruder outside
		if (interestRectangle.contains(measuredLocation.asPoint())
				&& !interestRectangle.contains(groundTruthLocation.asPoint())) {
			return IntruderTrackingAccuracy.distanceFromRectangleInside(
					measuredLocation, interestRectangle);
		}
		// CASE 5: both outside
		if (!interestRectangle.contains(measuredLocation.asPoint())
				&& !interestRectangle.contains(groundTruthLocation.asPoint())) {
			return 0;
		}
		throw new Error("We should never get here!");
	}
}