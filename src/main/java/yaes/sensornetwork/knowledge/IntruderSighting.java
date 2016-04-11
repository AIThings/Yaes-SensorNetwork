/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Jan 4, 2011
 
   storeanddump.knowledge.KHPointEstimate
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.knowledge;

import java.io.Serializable;

import yaes.sensornetwork.identification.IdentificationProperties;
import yaes.ui.format.Formatter;
import yaes.world.physical.location.Location;

/**
 * <code>storeanddump.knowledge.KHPointEstimate</code>
 * 
 * Describes a single primary sighting report
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class IntruderSighting implements Serializable {
	private static final long serialVersionUID = -8847320878650447107L;
	/**
	 * The location of the intruder
	 */
	private Location locationIntruder;

	/**
	 * The name of the intruder
	 */
	private String nameIntruder;
	/**
	 * The name of the sighter node
	 */
	private String nameSighter;
	/**
	 * The time when the report has been received
	 */
	private double timeReport;
	/**
	 * The time when the sighting had been done
	 */
	private double timeSighting;
	/**
	 * The identification properties of the sighting
	 */
	private IdentificationProperties identificationProperties;
	
	

	/**
	 * @return the identificationProperties
	 */
	public IdentificationProperties getIdentificationProperties() {
		return identificationProperties;
	}

	/**
	 * @param identificationProperties the identificationProperties to set
	 */
	public void setIdentificationProperties(
			IdentificationProperties identificationProperties) {
		this.identificationProperties = identificationProperties;
	}

	/**
	 * Constructor
	 * 
	 * @param nameSighter
	 * @param timeSighting
	 * @param nameIntruder
	 * @param locationIntruder
	 */
	public IntruderSighting(String nameSighter, double timeSighting,
			String nameIntruder, Location locationIntruder) {
		super();
		this.nameSighter = nameSighter;
		this.timeSighting = timeSighting;
		this.nameIntruder = nameIntruder;
		this.locationIntruder = locationIntruder;
		if (locationIntruder == null) {
			throw new Error(
					"Location of the intruder was null in the sighting!");
		}
	}

	/**
	 * @return the locationIntruder
	 */
	public Location getLocationIntruder() {
		return locationIntruder;
	}

	/**
	 * @return the nameIntruder
	 */
	public String getNameIntruder() {
		return nameIntruder;
	}

	/**
	 * @return the nameSighter
	 */
	public String getNameSighter() {
		return nameSighter;
	}

	/**
	 * @return the timeReport
	 */
	public double getTimeReport() {
		return timeReport;
	}

	/**
	 * @return the timeSighting
	 */
	public double getTimeSighting() {
		return timeSighting;
	}

	/**
	 * @param timeReport
	 *            the timeReport to set
	 */
	public void setTimeReport(double timeReport) {
		this.timeReport = timeReport;
	}

	@Override
	public String toString() {
		Formatter fmt = new Formatter();
		fmt.add("IntruderSighting");
		fmt.indent();
		fmt.is("nameSighter", nameSighter);
		fmt.is("timeSighting", timeSighting);
		fmt.is("nameIntruder", nameIntruder);
		fmt.is("locationIntruder", locationIntruder);
		fmt.is("timeReport", timeReport);
		return fmt.toString();
	}

}