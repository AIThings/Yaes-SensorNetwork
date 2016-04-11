/*
   This file is part of the Yet Another Extensible Simulator
   Created on: Sep 26, 2009
 
   yaes.world.sensornetwork.IntruderNode
 
   Copyright (c) 2008 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.applications.intrudertracking;

import yaes.sensornetwork.identification.IdentificationProperties;
import yaes.sensornetwork.identification.IntruderNodeType;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.ui.format.Formatter;
import yaes.ui.format.ToStringDetailed;
import yaes.world.physical.location.AbstractNamedMoving;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.PPMTraversal;
import yaes.world.physical.path.PathTraversal;
import yaes.world.physical.path.PlannedPath;
import yaes.world.physical.path.ProgrammedPathMovement;

/**
 * 
 * <code>yaes.world.sensornetwork.IntruderNode</code>
 * 
 * The intruder node is the simplest of the
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class IntruderNode extends AbstractNamedMoving implements
		ToStringDetailed {
	private static final long serialVersionUID = -7062527643331074004L;
	private IdentificationProperties identificationProperties;
	// the real type of the intruder node
	private IntruderNodeType intruderNodeType = IntruderNodeType.ANIMAL;
	private double maxSpeed;
	private PathTraversal pathTraversal;
	private PlannedPath plannedPath;
	@SuppressWarnings("unused")
	private ProgrammedPathMovement ppm;
	private PPMTraversal ppmTraversal;
	private double speed;

	public double getSpeed() {
		return speed;
	}

	private SensorNetworkWorld world;

	/**
	 * 
	 * Creates an intruder node which traverses a path with a constant speed
	 * 
	 * @param string
	 * @param plannedPath
	 * @param i
	 * @param world
	 */
	public IntruderNode(String name, PlannedPath plannedPath, double speed,
			SensorNetworkWorld world, IntruderNodeType intruderNodeType,
			IdentificationProperties identificationProperties) {
		super(name, plannedPath.getSource());
		this.plannedPath = plannedPath;
		this.speed = speed;
		this.world = world;
		pathTraversal = new PathTraversal(plannedPath);
		this.intruderNodeType = intruderNodeType;
		this.identificationProperties = identificationProperties;
	}

	/**
	 * Creates an intruder node which performs a programmed path movement.
	 * 
	 * @param name
	 * @param ppm
	 * @param world
	 */
	public IntruderNode(String name, ProgrammedPathMovement ppm,
			SensorNetworkWorld world) {
		super(name, new Location(0, 0));
		this.ppm = ppm;
		this.world = world;
		ppmTraversal = new PPMTraversal(ppm, 0);
	}

	/**
	 * Where do we call this???
	 */
	public void action() {
		if (plannedPath != null) {
			setLocation(pathTraversal.travel(speed));
		} else {
			setLocation(ppmTraversal.getLocation(world.getTime()));
		}
		world.move(this);
	}

	/**
	 * @return the identificationProperties
	 */
	public IdentificationProperties getIdentificationProperties() {
		return identificationProperties;
	}

	/**
	 * @return the intruderNodeType
	 */
	public IntruderNodeType getIntruderNodeType() {
		return intruderNodeType;
	}

	/**
	 * @return the maxSpeed
	 */
	public double getMaxSpeed() {
		return maxSpeed;
	}

	/**
	 * @param intruderNodeType
	 *            the intruderNodeType to set
	 */
	public void setIntruderNodeType(IntruderNodeType intruderNodeType) {
		this.intruderNodeType = intruderNodeType;
	}

	/**
	 * @param maxSpeed
	 *            the maxSpeed to set
	 */
	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	/**
	 * Describes a node in detail
	 */
	@Override
	public String toStringDetailed(int detailLevel) {
		Formatter fmt = new Formatter();
		fmt.add("IntruderNode: " + getName() + " type:" + getIntruderNodeType());
		fmt.indent();
		fmt.is("Location", getLocation());
		fmt.is("maxSpeed", getMaxSpeed());
		fmt.is("speed", getSpeed());
		if (getIdentificationProperties() != null) {
			fmt.add("identificationProperties");
			fmt.addIndented(getIdentificationProperties().toString());
		}
		//
		// this is the case if the node is plannedPath based
		//
		if (plannedPath != null) {
			fmt.add("plannedPath");
			fmt.addIndented(plannedPath.toString());
			fmt.add(ppmTraversal);
			fmt.addIndented(ppmTraversal.toString());
		}
		//
		// this is the case if the node is programmed path movement based
		//
		if (ppm != null) {
			fmt.add("ProgrammedPathMovement (ppm)");
			fmt.addIndented(ppm.toString());
			fmt.add(ppmTraversal);
			fmt.addIndented(ppmTraversal.toString());
		}
		return fmt.toString();
	}

}
