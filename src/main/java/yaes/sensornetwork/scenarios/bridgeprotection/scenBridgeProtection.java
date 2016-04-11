package yaes.sensornetwork.scenarios.bridgeprotection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import yaes.framework.simulation.Simulation;
import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.parametersweep.ExperimentPackage;
import yaes.framework.simulation.parametersweep.ParameterSweep;
import yaes.framework.simulation.parametersweep.ParameterSweep.ParameterSweepType;
import yaes.framework.simulation.parametersweep.ParameterSweepHelper;
import yaes.framework.simulation.parametersweep.ScenarioDistinguisher;
import yaes.sensornetwork.constSensorNetwork;

public class scenBridgeProtection implements BpaConstants {

	public static final File outputDir = new File(
			"data/bridgeProtection/output");
	public static final File graphDir = new File("data/bridgeProtection/graphs");
	public static final File logDir = new File("data/bridgeProtection/log");

	static {
		outputDir.mkdirs();
		graphDir.mkdirs();
		logDir.mkdirs();
	}

	/**
	 * Creates the default simulation input
	 * 
	 * @return
	 */
	public static SimulationInput createDefaultSimulationInput() {
		SimulationInput model = new SimulationInput();
		model.setContextClass(BpaContext.class);
		model.setSimulationClass(BpaSimulation.class);
		model.setStopTime(5000);
		model.setParameter(constSensorNetwork.SensorDeployment_SensorRange, 150.0);
		model.setParameter(constSensorNetwork.SensorDeployment_TransmissionRange, 130.0);
		model.setParameter(constSensorNetwork.SensorDeployment_SensorNodeCount,80);
		model.setParameter(constSensorNetwork.SensorDeployment_SinkNodeX, 1100.0);
		model.setParameter(constSensorNetwork.SensorDeployment_SinkNodeY, 600.0);
		model.setParameter(SensorArrangement.GRID_WITH_NOISE); // GRID
		model.setParameter(constSensorNetwork.Intruders_MovementRandomSeed, 100);
		model.setParameter(VisualDisplay.NO);
		model.setParameter(constSensorNetwork.SimControl_VoMessageInspector, 0);
		model.setParameter(constSensorNetwork.SimControl_OutputDir, outputDir.toString());
		model.setParameter(SimControl_KeepTimeSeries, 0);
		model.setParameter(constSensorNetwork.Intruders_Number, 5);
		model.setParameter(constSensorNetwork.SensorAgentClass.BridgeProtectionAlgorithm);
		model.setParameter(AgentParameter_SimpleIntruderTracking_Interval, 10.0);
		model.setParameter(BpaConstants.BRIDGE_PROTECTION_ACTIVE, 1);
		model.setParameter(BpaConstants.RNG_ROUTING, 0);
		model.setParameter(BpaConstants.GREEDY_ROUTING, 0);
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
		sip.setParameter(BRIDGE_PROTECTION_ACTIVE, 0);
		sip.setParameter(RNG_ROUTING, 0);
		sip.setParameter(GREEDY_ROUTING, 0);
		sip.setStopTime(150);
		final BpaContext context = new BpaContext();
		logDir.mkdirs();
		sip.setParameter(VisualDisplay.NO);
		Simulation.simulate(sip, BpaSimulation.class, context,
				logDir);
	}
	
	/**
	 * The simple run using relative neighborhood graph for path planning
	 * after catastrohpe
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	public static void doSimpleRNGRun() throws InstantiationException,
			IllegalAccessException, IOException {
		final SimulationInput sip = createDefaultSimulationInput();
		sip.setParameter(BRIDGE_PROTECTION_ACTIVE, 1);
		sip.setParameter(RNG_ROUTING, 1);
		sip.setParameter(GREEDY_ROUTING, 0);
		sip.setStopTime(150);
		final BpaContext context = new BpaContext();
		logDir.mkdirs();
		sip.setParameter(VisualDisplay.NO);
		Simulation.simulate(sip, BpaSimulation.class, context,
				logDir);
	}
	
	/**
	 * The simple run using geographical based greedy routing for path planning
	 * after the catastrophe
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IOException
	 */
	public static void doSimpleGreedyRun() throws InstantiationException,
			IllegalAccessException, IOException {
		final SimulationInput sip = createDefaultSimulationInput();
		sip.setParameter(BRIDGE_PROTECTION_ACTIVE, 1);
		sip.setParameter(RNG_ROUTING, 0);
		sip.setParameter(GREEDY_ROUTING, 1);
		sip.setStopTime(150);
		final BpaContext context = new BpaContext();
		logDir.mkdirs();
		sip.setParameter(VisualDisplay.NO);
		Simulation.simulate(sip, BpaSimulation.class, context,
				logDir);
}
	/**
	 * We are comparing a simple intruder tracking agent with the bridge
	 * protection algorithm
	 * 
	 * @return
	 */
	public static ParameterSweep getAgentTypes() {
		ParameterSweep sweepDiscrete = new ParameterSweep("protocols");
		// simple interval based agent
		ScenarioDistinguisher sd = new ScenarioDistinguisher("SP");
		sd.setDistinguisher(BpaConstants.BRIDGE_PROTECTION_ACTIVE, 0);
		sweepDiscrete.addDistinguisher(sd);
		sd = new ScenarioDistinguisher("BPA-SP");
		sd.setDistinguisher(BpaConstants.BRIDGE_PROTECTION_ACTIVE, 1);
		sweepDiscrete.addDistinguisher(sd);
//		sd = new ScenarioDistinguisher("BPA-GEO");
//		sd.setDistinguisher(BpaConstants.BRIDGE_PROTECTION_ACTIVE, 1);
//		sd.setDistinguisher(BpaConstants.RNG_ROUTING, 0);
//		sd.setDistinguisher(BpaConstants.GREEDY_ROUTING, 1);
//		sweepDiscrete.addDistinguisher(sd);
		sd = new ScenarioDistinguisher("BPA-RNG");
		sd.setDistinguisher(BpaConstants.BRIDGE_PROTECTION_ACTIVE, 1);
		sd.setDistinguisher(BpaConstants.RNG_ROUTING, 1);
		sd.setDistinguisher(BpaConstants.GREEDY_ROUTING, 0);
		sweepDiscrete.addDistinguisher(sd);
		return sweepDiscrete;
	}

	/**
	 * Sweeping the transmission range of sensor nodes for experimentation
	 * @return
	 */
	public static ParameterSweep getAgentRangeTypes() {
		ParameterSweep sweepDiscrete = new ParameterSweep("sensor-range");
		// simple interval based agent
		ScenarioDistinguisher sd = new ScenarioDistinguisher("Sensor Transmission Range 120m");
		sd.setDistinguisher(constSensorNetwork.SensorDeployment_TransmissionRange, 120.0);
		sweepDiscrete.addDistinguisher(sd);
		sd = new ScenarioDistinguisher("Sensor Transmission Range 130m");
		sd.setDistinguisher(constSensorNetwork.SensorDeployment_TransmissionRange, 130.0);
		sweepDiscrete.addDistinguisher(sd);
		sd = new ScenarioDistinguisher("Sensor Transmission Range 140m");
		sd.setDistinguisher(constSensorNetwork.SensorDeployment_TransmissionRange, 140.0);
		sweepDiscrete.addDistinguisher(sd);
		return sweepDiscrete;
	}


	/**
	 * This method compares different strategies employed for BPA routing
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

	/**
	 * This method compares simple BPA for different transmission ranges
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static void runFullSimpleSimulation() throws FileNotFoundException,
		IOException, InstantiationException, IllegalAccessException,
		ClassNotFoundException {
		SimulationInput sip = createDefaultSimulationInput();
		sip.setStopTime(150);
		sip.setParameter(BRIDGE_PROTECTION_ACTIVE, 1);
		sip.setParameter(RNG_ROUTING, 0);
		sip.setParameter(GREEDY_ROUTING, 0);
		compareVariableTransmissionRanges(sip);
	}
	
	/**
	 * This method compares the RNG BPA for different node transmission range
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static void runFullRNGSimulation() throws FileNotFoundException,
			IOException, InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		SimulationInput sip = createDefaultSimulationInput();
		sip.setStopTime(150);
		sip.setParameter(BRIDGE_PROTECTION_ACTIVE, 1);
		sip.setParameter(RNG_ROUTING, 1);
		sip.setParameter(GREEDY_ROUTING, 0);
		compareVariableTransmissionRanges(sip);
	}
	
	/**
	 * This method compares the Greedy BPA for different node transmission ranges
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static void runFullGreedySimulation() throws FileNotFoundException,
			IOException, InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		SimulationInput sip = createDefaultSimulationInput();
		sip.setStopTime(150);
		sip.setParameter(BRIDGE_PROTECTION_ACTIVE, 1);
		sip.setParameter(RNG_ROUTING, 0);
		sip.setParameter(GREEDY_ROUTING, 1);
		compareVariableTransmissionRanges(sip);
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
				.generateParameterSweepInteger("Number of mobile targets",
						constSensorNetwork.Intruders_Number, 1, 45, 5);
		pack.addParameterSweep(sweepIntruders);
		
        ParameterSweep sweepRandom =
                ParameterSweepHelper.generateParameterSweepInteger("label",
                		constSensorNetwork.Intruders_MovementRandomSeed, 0, 10); // was 50
        sweepRandom.setType(ParameterSweepType.Repetition);
        pack.addParameterSweep(sweepRandom);
		
		pack.initialize();
		pack.run();
		pack.generateGraph(Metrics_MessagesSentSum,
				"Number of messages", "number_of_messages");
		pack.generateGraph("SensorNetwork_TransmissionEnergy_S-43-SUM", "Bridge energy", "bridge_energy");
		pack.generateGraph("SensorNetwork_TransmissionEnergy_S-31-SUM", "energy", "energy31");
		pack.generateGraph("SensorNetwork_TransmissionEnergy_S-44-SUM", "energy", "energy44");
		pack.generateGraph("SensorNetwork_TransmissionEnergy_S-45-SUM", "energy", "energy45");
		pack.generateGraph("HighestFanout", "Highest fanout consumption", "highest_fanout_energy");
		// FIXME: other messages as well
	}
	
	/**
	 * This method is used to set the sweep parameters for transmission ranges
	 * @param model
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private static void compareVariableTransmissionRanges(SimulationInput model)
			throws FileNotFoundException, IOException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		ExperimentPackage pack = new ExperimentPackage(outputDir, graphDir);
		pack.setModel(model);
 		ParameterSweep sweepDiscrete = getAgentRangeTypes();
		pack.addParameterSweep(sweepDiscrete);
		ParameterSweep sweepIntruders = ParameterSweepHelper
				.generateParameterSweepInteger("Number of mobile targets",
						constSensorNetwork.Intruders_Number, 1, 45, 5);
		pack.addParameterSweep(sweepIntruders);
		
        ParameterSweep sweepRandom =
                ParameterSweepHelper.generateParameterSweepInteger("label",
                		constSensorNetwork.Intruders_MovementRandomSeed, 0, 10); // was 50
        sweepRandom.setType(ParameterSweepType.Repetition);
        pack.addParameterSweep(sweepRandom);
        
        
		pack.initialize();
		pack.run();
		pack.generateGraph(Metrics_MessagesSentSum,
				"Number of messages", "number_of_messages");
		pack.generateGraph("SensorNetwork_TransmissionEnergy_S-43-SUM", "Bridge energy", "bridge_energy");
		pack.generateGraph("SensorNetwork_TransmissionEnergy_S-31-SUM", "energy", "energy31");
		pack.generateGraph("SensorNetwork_TransmissionEnergy_S-44-SUM", "energy", "energy44");
		pack.generateGraph("SensorNetwork_TransmissionEnergy_S-45-SUM", "energy", "energy45");
		pack.generateGraph("HighestFanout", "Highest fanout consumption", "highest_fanout_energy");
		// FIXME: other messages as well
	}
	
}
