/*
   This file is part of the Yet Another Extensible Simulator
   Created on: Mar 16, 2009
 
   yaes.world.sensornetwork.worldmodel.IntruderModel
 
   Copyright (c) 2008 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.applications.intrudertracking;

import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import yaes.ui.text.TextUi;
import yaes.world.physical.location.Location;

/**
 * Intruder tracking model.
 * 
 * Supports last event and inertial estimation
 * 
 * <code>yaes.world.sensornetwork.worldmodel.IntruderModel</code>
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class IntruderModel implements Serializable {
	private static final long serialVersionUID = -1425773916321276067L;
	private static final double inertialEstimateTime = 10;
	private List<SimpleEntry<Location, Double>> locations = new ArrayList<>();
	private String name;
	private IntruderTrackingWorldModel.ItwmType type;

	/**
	 * @param name
	 */
	public IntruderModel(String name, IntruderTrackingWorldModel.ItwmType type) {
		this.name = name;
		this.type = type;
	}

	/**
	 * @return the location
	 */
	public Location getLocationEstimate(double time) {
		switch (type) {
		case LAST_KNOWN: {
			return locations.get(0).getKey();
		}
		case INERTIAL: {
			int last = locations.size() - 1;
			if (last == -1) {
				TextUi.println("Asking for estimate, but settings are empty");
				System.exit(1);
			}
			SimpleEntry<Location, Double> entryLast = locations.get(last);
			if (last == 0) {
				// one single setting
				return entryLast.getKey();
			}
			double timeLast = entryLast.getValue();
			last--;
			SimpleEntry<Location, Double> entryPreviousEstimator = null;
			double timePreviousEstimator = -1;
			while (last >= 0) {
				SimpleEntry<Location, Double> entryCurrent = locations
						.get(last);
				double timeCurrent = entryCurrent.getValue();
				if (entryPreviousEstimator == null) {
					entryPreviousEstimator = entryCurrent;
					timePreviousEstimator = timeCurrent;
				} else {
					// if too far in the past... break
					if (timeLast - timeCurrent > inertialEstimateTime) {
						break;
					}
					// otherwise replace the estimate base
					entryPreviousEstimator = entryCurrent;
					timePreviousEstimator = timeCurrent;
				}
				last--;
			}
			// ok, use the opportunity to do a bit of garbage collecting
			List<SimpleEntry<Location, Double>> toDelete = new ArrayList<>();
			for (SimpleEntry<Location, Double> entryTest : locations) {
				if (entryTest.getValue() < timeLast - inertialEstimateTime) {
					toDelete.add(entryTest);
				}
			}
			locations.removeAll(toDelete);
			// at this moment, we have the values
			if (time < timeLast) {
				TextUi.println("Trying to estimate in the past - wrong!!!");
				System.exit(1);
			}
			// estimate the speed
			double deltat = timeLast - timePreviousEstimator;
			if (deltat == 0) {
				// no previous estimator (eg. all of them are there)
				return entryLast.getKey();
			}
			Location locationLast = entryLast.getKey();
			Location locationPrevious = entryPreviousEstimator.getKey();
			double vx = (locationLast.getX() - locationPrevious.getX())
					/ deltat;
			double vy = (locationLast.getY() - locationPrevious.getY())
					/ deltat;
			double newx = locationLast.getX() + vx * (time - timeLast);
			double newy = locationLast.getY() + vy * (time - timeLast);
			return new Location(newx, newy);
		}
		default:
			throw new Error("Invalid type");
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Considers a report, updates the target if it is there
	 * 
	 * @param time
	 * @param location
	 * @param speed
	 */
	public boolean addIntruderAtLocation(double time, Location location) {
		SimpleEntry<Location, Double> entry = new SimpleEntry<>(location, time);
		switch (type) {
		case LAST_KNOWN: {
			if (locations.isEmpty()) {
				locations.add(entry);
				return true;
			}
			double lastUpdate = locations.get(0).getValue();
			if (time >= lastUpdate) {
				locations.clear();
				locations.add(entry);
				return true;
			}
			return false;
		}
		case INERTIAL: {
			// find the appropriate place to insert - assume it is sorted
			int insertPoint = 0;
			double timeLast = -1;
			for (int i = locations.size() - 1; i >= 0; i--) {
				SimpleEntry<Location, Double> e = locations.get(i);
				if (timeLast < 0) {
					timeLast = e.getValue();
					if (time < timeLast - inertialEstimateTime) {
						// don't insert, no change
						return false;
					}
				}
				if (time > e.getValue()) {
					insertPoint = i + 1;
					break;
				}
			}
			locations.add(insertPoint, entry);
			return true;
		}
		default:
			break;
		}
		throw new Error("One should never get here.");
	}

}