package yaes.sensornetwork.scenarios.bridgeprotection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import yaes.framework.simulation.IContext;
import yaes.framework.simulation.RandomVariable.Probe;
import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.SimulationOutput;
import yaes.sensornetwork.SensorNetworkSimulation;

public class BpaSimulation extends SensorNetworkSimulation {
	private static final long serialVersionUID = -5754819242062733905L;

	@Override
	public int update(double time, SimulationInput sip, SimulationOutput sop,
			IContext theContext) {
		if (time == 5.0) {
			((BpaContext) theContext).turnOnCatastrophicEvent();
		}
		return super.update(time, sip, sop, theContext);
	}

	/**
	 * Collect data about the data.
	 */
	@Override
	public void postprocess(SimulationInput sip, SimulationOutput sop,
			IContext theContext) {
		super.postprocess(sip, sop, theContext);
		// this is necessary but it is kinda embarassing, some solution must be
		// found
		// updates maximum energy of the nodes
		sop.update("SensorNetwork_TransmissionEnergy_S-43-SUM", sop.getValue(
				"SensorNetwork_TransmissionEnergy_S-43", Probe.SUM));
		// fanouts
		sop.update("SensorNetwork_TransmissionEnergy_S-31-SUM", sop.getValue(
				"SensorNetwork_TransmissionEnergy_S-31", Probe.SUM));
		sop.update("SensorNetwork_TransmissionEnergy_S-44-SUM", sop.getValue(
				"SensorNetwork_TransmissionEnergy_S-44", Probe.SUM));
		sop.update("SensorNetwork_TransmissionEnergy_S-45-SUM", sop.getValue(
				"SensorNetwork_TransmissionEnergy_S-45", Probe.SUM));
		
		
		List<Double> fanout = new ArrayList<>();
		fanout.add(sop.getValue("SensorNetwork_TransmissionEnergy_S-31",
				Probe.SUM));
		fanout.add(sop.getValue("SensorNetwork_TransmissionEnergy_S-44",
				Probe.SUM));
		fanout.add(sop.getValue("SensorNetwork_TransmissionEnergy_S-45",
				Probe.SUM));
		sop.update("HighestFanout", Collections.max(fanout));
	}

}
