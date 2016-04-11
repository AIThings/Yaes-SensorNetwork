/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Aug 1, 2010
 
   storeanddump.KnowledgeHistory
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork;

import java.io.Serializable;

import yaes.framework.simulation.IContext;
import yaes.framework.simulation.ISimulationCode;
import yaes.framework.simulation.RandomVariable.Probe;
import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.SimulationOutput;
import yaes.sensornetwork.applications.intrudertracking.IntruderNode;
import yaes.sensornetwork.model.SensorNode;
import yaes.sensornetwork.model.constSensorNetworkWorld;
import yaes.sensornetwork.model.stealth.StealthySensorNetworkWorld;

public class SensorNetworkSimulation implements ISimulationCode, constSensorNetwork,
		constSensorNetworkWorld, Serializable {
	public static String simulationLabel = "unlabelled";
	public static String label = "unspecified";
	

	/**
	 * Collect data about the data.
	 */
	@Override
	public void postprocess(SimulationInput sip, SimulationOutput sop,
			IContext theContext) {
		sop.update(Metrics_TransmissionEnergySum,
				sop.getValue(SENSORNETWORK_TRANSMISSION_ENERGY, Probe.SUM));
		sop.update(Metrics_MessagesSentSum,
				sop.getValue(SENSORNETWORK_MESSAGES_SENT, Probe.SUM));
	}

	@Override
	public void setup(SimulationInput sip, SimulationOutput sop,
			IContext theContext) {
		final SensorNetworkContext context = (SensorNetworkContext) theContext;
		context.initialize(sip, sop);
		// create the label
		label = "simulation " + getClass().getName() + " with context " + context.getClass().getName();
		if (simulationLabel != null) {
		    label = label + " run label: " + simulationLabel;
		}
	}

	@Override
	public String toString() {
	    return label;
	}

	/**
	 * Updates the simulation step.
	 */
	@Override
	public int update(double time, SimulationInput sip, SimulationOutput sop,
			IContext theContext) {
		final SensorNetworkContext context = (SensorNetworkContext) theContext;
		final StealthySensorNetworkWorld sensorWorld = context.getWorld();
		context.getWorld().setTime((int) time);
		for (final SensorNode element : sensorWorld.getSensorNodes()) {
			if (element.isEnabled()) {
				element.update();
			}
		}
		sensorWorld.getSinkNode().update();
		for (final IntruderNode element : sensorWorld.getIntruderNodes()) {
			element.action();
		}
		if (context.getVisualizer() != null) {
			context.getVisualizer().update();
			try {
				Thread.sleep(10);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		sensorWorld.messageFlush();
		context.updateGroundTruth();
		return 1;
	}
}
