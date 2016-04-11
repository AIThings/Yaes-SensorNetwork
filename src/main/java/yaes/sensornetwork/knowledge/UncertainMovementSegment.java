/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Jan 5, 2011
 
   storeanddump.knowledge.UncertainMovementSegment
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.knowledge;

import java.awt.Shape;
import java.awt.geom.Path2D;

import yaes.ui.format.Formatter;
import yaes.world.physical.location.Location;

/**
 * 
 * <code>storeanddump.knowledge.UncertainMovementSegment</code>
 * 
 * The code necessary to cover the uncertain movement segment
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class UncertainMovementSegment {

	private double a;

	private double b;
	private Location locationFrom;
	private Location locationTo;
	private double maxSpeed;
	private double timeFrom;
	private double timeTo;

	public UncertainMovementSegment(Location locationFrom, double timeFrom,
			Location locationTo, double timeTo, double maxSpeed) {
		super();
		this.locationFrom = locationFrom;
		this.timeFrom = timeFrom;
		this.locationTo = locationTo;
		this.timeTo = timeTo;
		this.maxSpeed = maxSpeed;
		// calculate h and w
		double d = locationFrom.distanceTo(locationTo);
		double dmax = maxSpeed * (timeTo - timeFrom);
		if (d > dmax) {
			// throw new Error("Not possible!!!");
			// TextUi.errorPrint("UncertainMovementSegment: Speed was underestimated, corrected.");
			dmax = d;
		}
		a = dmax / 2;
		b = Math.sqrt(dmax * dmax - d * d) / 2;
	}

	/**
	 * Returns the area of the rectangle
	 * 
	 * @return
	 */
	public double getArea() {
		return Math.PI * a * b;
	}

	/**
	 * @return the locationFrom
	 */
	public Location getLocationFrom() {
		return locationFrom;
	}

	/**
	 * @return the locationTo
	 */
	public Location getLocationTo() {
		return locationTo;
	}

	/**
	 * Returns a shape for this
	 * 
	 * @return
	 */
	public Shape getShape() {
		Path2D.Double retval = new Path2D.Double();
		double xc = (locationFrom.getX() + locationTo.getX()) / 2;
		double yc = (locationFrom.getY() + locationTo.getY()) / 2;
		double yd = locationTo.getY() - locationFrom.getY();
		double xd = locationTo.getX() - locationFrom.getX();
		double fi = Math.atan2(yd, xd);
		// initial position
		double x0 = xc + a * Math.cos(0) * Math.cos(fi) - b * Math.sin(0)
				* Math.sin(fi);
		double y0 = yc + a * Math.cos(0) * Math.sin(fi) + b * Math.sin(0)
				* Math.cos(fi);
		retval.moveTo(x0, y0);
		double step = 0.1;
		for (double t = step; t < 2 * Math.PI; t = t + step) {
			double x = xc + a * Math.cos(t) * Math.cos(fi) - b * Math.sin(t)
					* Math.sin(fi);
			double y = yc + a * Math.cos(t) * Math.sin(fi) + b * Math.sin(t)
					* Math.cos(fi);
			retval.lineTo(x, y);
		}
		retval.lineTo(x0, y0);
		return retval;
	}

	/**
	 * @return the timeFrom
	 */
	public double getTimeFrom() {
		return timeFrom;
	}

	/**
	 * @return the timeTo
	 */
	public double getTimeTo() {
		return timeTo;
	}

	@Override
	public String toString() {
		Formatter fmt = new Formatter();
		fmt.add("UncertainMovementSegment");
		fmt.indent();
		fmt.is("a", a);
		fmt.is("b", b);
		fmt.is("area", getArea());
		return fmt.toString();
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}
}
