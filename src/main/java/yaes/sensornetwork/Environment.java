/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Dec 30, 2010
 
   storeanddump.Environment
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import yaes.world.physical.location.Location;
import yaes.world.physical.location.NamedLocation;
import yaes.world.physical.map.SimpleFreeGround;
import yaes.world.physical.path.PathGenerator;
import yaes.world.physical.path.PlannedPath;

/**
 * 
 * Describes the environment considered by a specific paper
 * 
 * <code>yaes.sensornetwork.Environment</code>
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class Environment implements Serializable {

	private static final long serialVersionUID = -943748873823130938L;

	private Map<String, PlannedPath> roads = new HashMap<>();
	private Map<String, PlannedPath> paths = new HashMap<>();
	private Map<String, NamedLocation> landmarks = new HashMap<>();
	private SimpleFreeGround theMap;

	/**
	 * The full area considered
	 */
	private Rectangle2D.Double fullArea;

	/**
	 * The interest area considered
	 */
	private Rectangle2D.Double interestArea = null;

	/**
	 * The sensor distribution area
	 */
	private Rectangle2D.Double sensorDistributionArea;

	private Map<String, Rectangle2D.Double> protectedAreas = new HashMap<String, Rectangle2D.Double>();

	public Environment() {
	}

	public void addLandmark(String name, double x, double y) {
		NamedLocation lm = new NamedLocation(new Location(x, y), name);
		landmarks.put(name, lm);
	}

	public void addPath(String name, PlannedPath plannedPath) {
		paths.put(name, plannedPath);
	}

	public void addProtectedArea(String name, Rectangle2D.Double area) {
		protectedAreas.put(name, area);
	}

	public void addRoad(String name, PlannedPath plannedPath) {
		roads.put(name, plannedPath);
	}

	public PlannedPath createPathFromLandmarks(String... landmarks) {
		ArrayList<Location> locations = new ArrayList<>();
		for (String name : landmarks) {
			locations.add(getLandmark(name));
		}
		PlannedPath retval = PathGenerator.createPathFromNodes(locations);
		return retval;
	}

	/**
	 * @return the fullArea
	 */
	public Rectangle2D.Double getFullArea() {
		return fullArea;
	}

	/**
	 * @return the interestArea
	 */
	public Rectangle2D.Double getInterestArea() {
		return interestArea;
	}

	public Location getLandmark(String string) {
		Location retval = landmarks.get(string);
		if (retval == null) {
			throw new Error("Could not find landmark: " + string);
		}
		return retval;
	}

	public Map<String, NamedLocation> getLandmarks() {
		return landmarks;
	}

	/**
	 * @return the paths
	 */
	public Map<String, PlannedPath> getPaths() {
		return paths;
	}

	public Map<String, Rectangle2D.Double> getProtectedAreas() {
		return protectedAreas;
	}

	/**
	 * @return the roads
	 */
	public Map<String, PlannedPath> getRoads() {
		return roads;
	}

	/**
	 * @return the sensorDistributionArea
	 */
	public Rectangle2D.Double getSensorDistributionArea() {
		return sensorDistributionArea;
	}

	/**
	 * @return the theMap
	 */
	public SimpleFreeGround getTheMap() {
		return theMap;
	}

	/**
	 * @param fullArea
	 *            the fullArea to set
	 */
	public void setFullArea(Rectangle2D.Double fullArea) {
		this.fullArea = fullArea;
	}

	/**
	 * @param interestArea
	 *            the interestArea to set
	 */
	public void setInterestArea(Rectangle2D.Double interestArea) {
		this.interestArea = interestArea;
	}

	/**
	 * @param sensorDistributionArea
	 *            the sensorDistributionArea to set
	 */
	public void setSensorDistributionArea(
			Rectangle2D.Double sensorDistributionArea) {
		this.sensorDistributionArea = sensorDistributionArea;
	}

	/**
	 * @param theMap
	 *            the theMap to set
	 */
	public void setTheMap(SimpleFreeGround theMap) {
		this.theMap = theMap;
	}

}
