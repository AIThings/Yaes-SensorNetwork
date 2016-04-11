package yaes.sensornetwork.scenarios.tryandbounce;

import yaes.framework.simulation.IContext;
import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.SimulationOutput;
import yaes.sensornetwork.SensorNetworkSimulation;
import yaes.sensornetwork.applications.intrudertracking.IntruderNode;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingSinkAgent;
import yaes.sensornetwork.knowledge.IntruderTrackingAccuracy;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.SensorNode;

public class TABSimulation extends SensorNetworkSimulation {
	private static final long serialVersionUID = 1L;

	@Override
	public void setup(SimulationInput sip, SimulationOutput sop,
			IContext theContext) {
		// call the super
		super.setup(sip, sop, theContext);
		//
		// stealth specific stuff
		//
		TABContext context = (TABContext) theContext;
		SensorNetworkWorld sensorWorld = context.getWorld();
		sop.createVariable(Metrics_OverallStealth, true);

		boolean keepTimeSeries = false;
		if (keepTimeSeries) {
			sop.createVariable(Metrics_OverallStealth, true);
			for (final SensorNode element : sensorWorld.getSensorNodes()) {
				sop.createVariable(
						Metrics_IndividualStealthPrefix + element.getName(),
						true);
			}
			// initialize the variables with the time series
			if (sip.getParameterInt(SimControl_KeepTimeSeries) != 0) {
				sop.createVariable(Metrics_AbsoluteErrorSum, true);
			}
		}
		// initialize the variables with the time series
		if (sip.getParameterInt(SimControl_KeepTimeSeries) != 0) {
			sop.createVariable(Metrics_ModelDistance, true);
		}
	}

	@Override
	public String toString() {
		return "StealthRouting " + TABSimulation.simulationLabel;
	}

	/**
	 * Updates the simulation step.
	 */
	@Override
	public int update(double time, SimulationInput sip, SimulationOutput sop,
			IContext theContext) {
		// the generic sensor network stuff
		super.update(time, sip, sop, theContext);
		//
		// stealth specific stuff
		//
		TABContext context = (TABContext) theContext;
		SensorNetworkWorld sensorWorld = context.getWorld();
		// This calculates the model average but this needs to be factored out
		// it is fancy metric!!!
		// and by the way, it is not a correct implementation!!!
		IntruderTrackingSinkAgent sink = (IntruderTrackingSinkAgent) sensorWorld
				.getSinkNode().getAgent();
		double insideCount = 0;
		double distanceSum = 0;
		for (IntruderNode node : sensorWorld.getIntruderNodes()) {
			if (context.environment.getInterestArea().contains(
					node.getLocation().asPoint())) {
				double distance = IntruderTrackingAccuracy
						.targetTrackingAccuracy(node.getName(),
								context.environment.getInterestArea(),
								sink.getWorldModel(), sensorWorld);
				insideCount = insideCount + 1.0;
				distanceSum = distanceSum + distance;
			}
		}
		double distanceAvg = 0;
		if (insideCount == 0) {
			distanceAvg = 0;
		} else {
			distanceAvg = distanceSum / insideCount;
		}
		// TextUi.println("Distance: avg:" + distanceAvg + "  nodes inside: " +
		// insideCount);
		sop.update(Metrics_ModelDistance, distanceAvg);
		/*
		try {
			context.getVisualizer().saveImage(
					"Movie1-" + String.format("%05d", (int) time) + ".png");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		return 1;
	}
}
