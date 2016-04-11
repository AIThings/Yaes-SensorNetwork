/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Jan 4, 2011
 
   storeanddump.knowledge.IntruderSighingComparators
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.knowledge;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingMessageConstants;
import yaes.world.physical.location.Location;

/**
 * 
 * <code>storeanddump.knowledge.IntruderSighingComparators</code>
 * 
 * Helper functions
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class IntruderSighingHelper {

	/**
	 * Creates a message reporting the current known position.
	 * 
	 * Returns null if the location of the intruder is not known at that time.
	 * 
	 * @param intruder
	 * @param localEstimator
	 * @param localIsh
	 * @return
	 */
	public static ACLMessage createMessage(String sender, double time,
			String intruder, ILocationEstimator localEstimator,
			IntruderSightingHistory localIsh) {
		Location location = localEstimator.getIntruderLocation(localIsh,
				intruder, time);
		if (location == null) {
			return null;
		}
		ACLMessage m = new ACLMessage(sender, ACLMessage.Performative.INFORM);
		m.setValue(IntruderTrackingMessageConstants.FIELD_INTRUDER_NAME,
				intruder);
		m.setValue(IntruderTrackingMessageConstants.FIELD_INTRUDER_LOCATION,
				location);
		m.setValue(IntruderTrackingMessageConstants.FIELD_INTRUDER_TIME, time);
		m.setValue(IntruderTrackingMessageConstants.FIELD_INTRUDER_OBSERVER,
				sender);
		return m;
	}

	/**
	 * Extracts a sighting from the message
	 * 
	 * @param m
	 * @return
	 */
	public static IntruderSighting extractSightingFromMessage(final ACLMessage m) {
		String name = (String) m
				.getValue(IntruderTrackingMessageConstants.FIELD_INTRUDER_NAME);
		Location location = (Location) m
				.getValue(IntruderTrackingMessageConstants.FIELD_INTRUDER_LOCATION);
		double timestamp = (Double) m
				.getValue(IntruderTrackingMessageConstants.FIELD_INTRUDER_TIME);
		String observer = (String) m
				.getValue(IntruderTrackingMessageConstants.FIELD_INTRUDER_OBSERVER);
		IntruderSighting is = new IntruderSighting(observer, timestamp, name,
				location);
		return is;
	}

	/**
	 * A filter for an interval of the sighted between some times
	 * 
	 * @return
	 */
	public static List<IntruderSighting> sightedBetween(
			List<IntruderSighting> list, double timeStart, double timeEnd) {
		List<IntruderSighting> retval = new ArrayList<IntruderSighting>();
		for (IntruderSighting is : list) {
			double time = is.getTimeSighting();
			if ((time >= timeStart) && (time <= timeEnd)) {
				retval.add(is);
			}
		}
		return retval;
	}

	public static Comparator<IntruderSighting> sightingOrder() {
		return new Comparator<IntruderSighting>() {
			@Override
			public int compare(IntruderSighting o1, IntruderSighting o2) {
				return Double.compare(o1.getTimeSighting(), o2
						.getTimeSighting());
			}
		};
	}

}
