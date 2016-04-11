/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Jan 18, 2011
 
   storeanddump.scenarios.scenValueOfDeepInspection
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.scenarios.deepinspection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import yaes.framework.simulation.Simulation;
import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.parametersweep.ExperimentPackage;
import yaes.framework.simulation.parametersweep.ParameterSweep;
import yaes.framework.simulation.parametersweep.ParameterSweepHelper;
import yaes.framework.simulation.parametersweep.ScenarioDistinguisher;
import yaes.sensornetwork.SensorNetworkContext;
import yaes.sensornetwork.SensorNetworkSimulation;
import yaes.sensornetwork.constSensorNetwork;

/**
 * 
 * <code>storeanddump.scenarios.scenValueOfDeepInspection</code>
 * 
 * This scenario implements a comparison study of deep packet inspection on a
 * knowledge based agent.
 * 
 * -knowledge based agent with simple estimator -with pass-through learning
 * turned on -with overhearing learning turned on -simple interval based agent -
 * reports on the nearest node
 * 
 * -all of them hop-by-hop routing, overlapping sensor ranges -variety of
 * intruders doing random waypoint movement (no roads)
 * 
 * -comparison: -tracking accuracy of the sink function of number of nodes
 * -tracking accuracy of the sink function of the speed of the nodes
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class scenValueOfDeepInspection implements constSensorNetwork {

	public static final File outputDir = new File(
			"data/valueOfDeepInspection/output");
	public static final File graphDir = new File(
			"data/valueOfDeepInspection/graphs");
	public static final File logDir = new File("data/valueOfDeepInspection/log");

	static {
		outputDir.mkdirs();
		graphDir.mkdirs();
		logDir.mkdirs();
	}

	/**
	 * @param model
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private static void compareVariableIntruderNodes(SimulationInput model)
			throws FileNotFoundException, IOException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		ExperimentPackage pack = new ExperimentPackage(outputDir, graphDir);
		pack.setModel(model);
		ParameterSweep sweepDiscrete = getAgentTypes();
		pack.addParameterSweep(sweepDiscrete);
		ParameterSweep sweepIntruders = ParameterSweepHelper
				.generateParameterSweepInteger("Intruder nodes",
						constSensorNetwork.Intruders_Number, 1, 10, 1);
		pack.addParameterSweep(sweepIntruders);

		// ParameterSweep sweepRandom = ParameterSweepHelper
		// .generateParameterSweepInteger("Random", INTRUDER_RANDOM_SEED,
		// 0, 1);
		// ParameterSweep sweepRandom = ParameterSweepHelper
		// .generateParameterSweepInteger("Random", INTRUDER_RANDOM_SEED,
		// 0, 0);
		// sweepRandom.setType(ParameterSweepType.Repetition);
		// pack.addParameterSweep(sweepRandom);
		// beautifying the graphs
		// pack.setVariableDescription(INTRUDER_NODES, "Intruder nodes");
		// pack.setVariableDescription(MODEL_DISTANCE, "Tracking accuracy");
		pack.initialize();
		pack.run();
		pack.generateGraph(Metrics_AbsoluteErrorSum, "Absolute error sum",
				"absolute_error");
		pack.generateGraph(Metrics_InterestAreaErrorSum, "Interest area error sum",
				"interest_error");
		pack.generateGraph(Metrics_StealthAvg, "Overall stealth", "overall_stealth");
		pack.generateGraph(Metrics_MessagesSentSum,
				"Number of messages", "number_of_messages");
		// pack.generateGraph(,
		// "Number of messages", "number_of_messages");
	}

	/**
	 * Creates the default simulation input
	 * 
	 * @return
	 */
	public static SimulationInput createDefaultSimulationInput() {
		SimulationInput model = new SimulationInput();
		model.setContextClass(scenValueOfDeepInspectionContext.class);
		model.setSimulationClass(SensorNetworkSimulation.class);
		model.setStopTime(5000);
		model.setParameter(constSensorNetwork.SensorDeployment_SensorRange, 150.0);
		model.setParameter(constSensorNetwork.SensorDeployment_TransmissionRange, 400.0);
		model.setParameter(constSensorNetwork.SensorDeployment_SensorNodeCount, 16);
		model.setParameter(constSensorNetwork.SensorDeployment_SinkNodeX, 450.0);
		model.setParameter(constSensorNetwork.SensorDeployment_SinkNodeY, 450.0);
		model.setParameter(SensorArrangement.GRID); // GRID
		// model.setParameter(ActuatorMovement.RANDOM_WAYPOINT); // WINDING
		model.setParameter(constSensorNetwork.Intruders_MovementRandomSeed, 100);
		model.setParameter(VisualDisplay.NO);
		model.setParameter(constSensorNetwork.SimControl_VoMessageInspector, 0);
		model.setParameter(constSensorNetwork.SimControl_OutputDir, outputDir.toString());
		model.setParameter(SimControl_KeepTimeSeries, 0);
		// knowledge based agent
		model.setParameter(KBSA_KnowledgeMetric.InterestAreaEstimationError);
		return model;
	}

	/**
	 * Performs a sample run of the value of deep inspection scenario
	 * 
	 * @param model
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IOException
	 */
	public static void doSimpleRun() throws InstantiationException,
			IllegalAccessException, IOException {
		final SimulationInput sip = createDefaultSimulationInput();
		sip.setStopTime(100);
		sip.setParameter(constSensorNetwork.SensorAgentClass.KnowledgeBasedSensor);
		// sip.setParameter(Constants.SensorAgentClass.IntruderTrackingSimpleSensorAgent);
		sip.setParameter(AgentParameter_SimpleIntruderTracking_Interval, 10.0);
		sip.setParameter(constSensorNetwork.AgentParameter_KBSA_UseKnowledgeOverheard, 0); //
		sip.setParameter(constSensorNetwork.AgentParameter_KBSA_UseKnowledgePassthrough, 0); //
		sip.setParameter(SensorArrangement.GRID_WITH_NOISE);
		final SensorNetworkContext context = new SensorNetworkContext();
		logDir.mkdirs();
		sip.setParameter(VisualDisplay.NO);
		Simulation.simulate(sip, SensorNetworkSimulation.class, context,
				logDir);
	}

	/**
	 * Creates the different types of agents
	 * 
	 * @return
	 */
	public static ParameterSweep getAgentTypes() {
		ParameterSweep sweepDiscrete = new ParameterSweep("protocols");
		// simple interval based agent
		ScenarioDistinguisher sd = new ScenarioDistinguisher("Simple");
		sd.setDistinguisher(constSensorNetwork.SensorAgentClass.SimpleIntruderTracking);
		// sd.setDistinguisher(DIF_INTERVAL, 25.0); // not quite sure this is
		// taking it
		sweepDiscrete.addDistinguisher(sd);
		// knowledge based - local knowledge
		sd = new ScenarioDistinguisher("Local knowledge only");
		sd.setDistinguisher(constSensorNetwork.SensorAgentClass.KnowledgeBasedSensor);
		sd.setDistinguisher(constSensorNetwork.AgentParameter_KBSA_UseKnowledgeOverheard, 0); //
		sd.setDistinguisher(constSensorNetwork.AgentParameter_KBSA_UseKnowledgePassthrough, 0); //
		sweepDiscrete.addDistinguisher(sd);
		// knowledge based - use passthrough knowledge
		sd = new ScenarioDistinguisher("Pass-through knowledge");
		sd.setDistinguisher(constSensorNetwork.SensorAgentClass.KnowledgeBasedSensor);
		sd.setDistinguisher(constSensorNetwork.AgentParameter_KBSA_UseKnowledgeOverheard, 0); //
		sd.setDistinguisher(constSensorNetwork.AgentParameter_KBSA_UseKnowledgePassthrough, 1); //
		sweepDiscrete.addDistinguisher(sd);
		// knowledge based - use both types of knowledge
		sd = new ScenarioDistinguisher("Pass-through and overheard");
		sd.setDistinguisher(constSensorNetwork.SensorAgentClass.KnowledgeBasedSensor);
		sd.setDistinguisher(constSensorNetwork.AgentParameter_KBSA_UseKnowledgeOverheard, 1); //
		sd.setDistinguisher(constSensorNetwork.AgentParameter_KBSA_UseKnowledgePassthrough, 1); //
		sweepDiscrete.addDistinguisher(sd);
		return sweepDiscrete;
	}

	/**
	 * @param model
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void runFullSimulation() throws FileNotFoundException,
			IOException, InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		SimulationInput model = createDefaultSimulationInput();
		model.setStopTime(100);
		compareVariableIntruderNodes(model);
	}

}
