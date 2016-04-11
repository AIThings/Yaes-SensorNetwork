package yaes.sensornetwork.scenarios.tryandbounce;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import yaes.framework.simulation.Simulation;
import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.SimulationOutput;
import yaes.framework.simulation.parametersweep.ExperimentPackage;
import yaes.framework.simulation.parametersweep.ParameterSweep;
import yaes.framework.simulation.parametersweep.ParameterSweep.ParameterSweepType;
import yaes.framework.simulation.parametersweep.ParameterSweepHelper;
import yaes.framework.simulation.parametersweep.ScenarioDistinguisher;
import yaes.sensornetwork.constSensorNetwork;
import yaes.ui.text.TextUi;

/**
 * Scenario class for the TryAndBounce algorithm.
 * 
 * April 12, 2014: This scenario does not use the StealthRoutingContext /
 * StealthRoutingSimulation classes ...
 * 
 * @author Lotzi Boloni
 * 
 */
public class scenTryAndBounce implements constSensorNetwork, Serializable {

	private static final String MENU_COMPARE_STEALTHINESS = "Comparing the stealthiness vs intruders";
	private static final String MENU_COMPARE_STEALTH_TR = "Comparing the stealthiness vs transmission range";
	private static final String MENU_TIME_SERIES_STEALTHINESS = "Generate times series graphs";

	private static final long serialVersionUID = -200832789926786355L;
	public static final File outputDir = new File("data/tryandbounce/output");
	public static final File graphDir = new File("data/tryandbounce/graphs");
	public static final File logDir = new File("data/tryandbounce/log");
	public static final File baseDirVarIntr = new File(
			"data/tryandbounce/compare_intr");
	public static final File baseDirTimeSeries = new File(
			"data/tryandbounce/timeseries");

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
		// model.setContextClass(scenValueOfDeepInspectionContext.class);
		model.setContextClass(TABContext.class);
		model.setSimulationClass(TABSimulation.class);
		model.setStopTime(5000);
		model.setParameter(SensorDeployment_SensorRange, 50.0);
		model.setParameter(SensorDeployment_TransmissionRange, 400.0);
		model.setParameter(SensorDeployment_SensorNodeCount, 16);
		model.setParameter(SensorDeployment_SinkNodeX, 450.0);
		model.setParameter(SensorDeployment_SinkNodeY, 450.0);
		model.setParameter(SensorArrangement.GRID); // GRID
		// Interest rectangle
		model.setParameter(SensorDeployment_InterestRectangleX, 200.0);
		model.setParameter(SensorDeployment_InterestRectangleY, 200.0);
		model.setParameter(SensorDeployment_InterestRectangleWidth, 400.0);
		model.setParameter(SensorDeployment_InterestRectangleHeight, 400.0);

		// Intruder models
		model.setParameter(IntruderScenario.COMBING);
		model.setParameter(Intruders_Number, 10);
		model.setParameter(Intruders_Speed, 0.5);
		model.setParameter(Intruders_RandomSeed, 0);
		model.setParameter(Intruders_MovementRandomSeed, 100);

		// Simulation parameters
		model.setParameter(VisualDisplay.NO);
		model.setParameter(SimControl_VoMessageInspector, 0);
		model.setParameter(SimControl_OutputDir, outputDir.toString());
		model.setParameter(SimControl_KeepTimeSeries, 0);
		// parameters of the agents
		model.setParameter(
				AgentParameter_TryAndBounce_TransmissionCostThreshold, 0.002);
		model.setParameter(AgentParameter_DirectedDiffusion_ReportInterval,
				100.0);
		model.setParameter(AgentParameter_SimpleIntruderTracking_Interval, 1.0);
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
		SimulationInput sip = createDefaultSimulationInput();
		sip.setStopTime(500);
		// intruders
		sip.setParameter(IntruderScenario.ORBIT);
		sip.setParameter(Intruders_Number, 10);
		sip.setParameter(Intruders_Speed, 1.0);
		// agent 
		sip.setParameter(SensorAgentClass.TryAndBounce);
		sip.setParameter(AgentParameter_TryAndBounce_TransmissionCostThreshold,
				0.3);
		// sip.setParameter(SensorArrangement.GRID_WITH_NOISE);
		TABContext context = new TABContext();
		logDir.mkdirs();
		sip.setParameter(VisualDisplay.NO);
		SimulationOutput sop = Simulation.simulate(sip, TABSimulation.class,
				context, logDir);
		TextUi.println(sop);
	}

	/**
	 * Runs all the comparative simulation studies, generates the graphs etc. As
	 * this is likely to be long, for the time being, keep it separate.
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws FileNotFoundException
	 */
	public static void runFullSimulation() throws FileNotFoundException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException, IOException {
		SimulationInput model = createDefaultSimulationInput();
		List<String> menu = new ArrayList<>();
		menu.add(MENU_COMPARE_STEALTHINESS);
		menu.add(MENU_COMPARE_STEALTH_TR);
		menu.add(MENU_TIME_SERIES_STEALTHINESS);
		String result = TextUi.menu(menu, MENU_COMPARE_STEALTHINESS, "Choose:");
		if (result.equals(MENU_COMPARE_STEALTHINESS)) {
			doCompareStealthiness(model);
		}
		if (result.equals(MENU_COMPARE_STEALTH_TR)) {
			doCompareStealthinessSensorRange(model);
		}
		if (result.equals(MENU_TIME_SERIES_STEALTHINESS)) {
			doGenerateTimeSeriesStealthiness(model);
		}
	}

	/**
	 * @param model
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private static void doCompareStealthiness(SimulationInput model)
			throws FileNotFoundException, IOException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		ExperimentPackage pack = new ExperimentPackage(baseDirVarIntr, graphDir);
		pack.setModel(model);
		ParameterSweep sweepDiscrete = getAgentTypes();
		pack.addParameterSweep(sweepDiscrete);
		ParameterSweep sweepIntruders = ParameterSweepHelper
				.generateParameterSweepInteger("Intruder nodes",
						Intruders_Number, 5, 80, 5);
		pack.addParameterSweep(sweepIntruders);

		// ParameterSweep sweepRandom = ParameterSweepHelper
		// .generateParameterSweepInteger("Random", INTRUDER_RANDOM_SEED,
		// 0, 1);
		ParameterSweep sweepRandom = ParameterSweepHelper
				.generateParameterSweepInteger("Random", Intruders_RandomSeed,
						0, 0);
		sweepRandom.setType(ParameterSweepType.Repetition);
		// pack.addParameterSweep(sweepRandom);
		// beautifying the graphs
		pack.setVariableDescription(Intruders_Number, "Intruder nodes");
		pack.setVariableDescription(Metrics_TransmissionEnergySum,
				"Energy consumption (pJ)");
		pack.setVariableDescription(Metrics_StealthAvg, "Stealth");
		pack.setVariableDescription(Metrics_ModelDistance, "Tracking accuracy");
		pack.initialize();
		pack.run();
		pack.generateGraph(Metrics_ModelDistance, null, "utility");
		pack.generateGraph(Metrics_StealthAvg, null, "stealth");
		// pack.generateGraph(STEALTH_MIN,
		// null, "stealthmin");
		pack.generateGraph(Metrics_TransmissionEnergySum, null, "energy");
		pack.generateGraph(Metrics_MessagesSentSum, "Messages sent", "messages");
	}

	/**
	 * @param model
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private static void doCompareStealthinessSensorRange(SimulationInput model)
			throws FileNotFoundException, IOException, InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		File basedir = new File("data/compare_sr");
		ExperimentPackage pack = new ExperimentPackage(basedir, outputDir);
		model.setParameter(Intruders_Number, 40); // 80 intruders, a
													// lot
		pack.setModel(model);
		ParameterSweep sweepDiscrete = getAgentTypes();
		pack.addParameterSweep(sweepDiscrete);
		ParameterSweep sweepSensorRange = ParameterSweepHelper
				.generateParameterSweepDouble("Sensing range",
						SensorDeployment_SensorRange, 10, 40, 80); // was step 5
		pack.addParameterSweep(sweepSensorRange);
		// ParameterSweep sweepRandom = ParameterSweepHelper
		// .generateParameterSweepInteger("Random", INTRUDER_RANDOM_SEED,
		// 0, 0);
		// sweepRandom.setType(ParameterSweepType.Repetition);
		// pack.addParameterSweep(sweepRandom);
		pack.initialize();
		pack.run();
		pack.generateGraph(Metrics_ModelDistance,
				"Tracking accuracy function of intruder nodes", "utility_sr");
		pack.generateGraph(Metrics_StealthAvg,
				"Average stealth function of intruder nodes", "stealth_sr");
		pack.generateGraph(Metrics_StealthMin,
				"Minimum stealth function of intruder nodes", "stealthmin_sr");
		pack.generateGraph(Metrics_TransmissionEnergySum,
				"Transmission energy", "energy_sr");
		pack.generateGraph(Metrics_MessagesSentSum, "Messages sent",
				"messages_sr");
	}

	/**
	 * @param model
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private static void doGenerateTimeSeriesStealthiness(SimulationInput model)
			throws FileNotFoundException, IOException, ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		model.setParameter(SimControl_KeepTimeSeries, 1);
		model.setParameter(Intruders_Number, 40);

		ExperimentPackage pack = new ExperimentPackage(baseDirTimeSeries,
				outputDir);
		pack.setModel(model);
		ParameterSweep sweepDiscrete = getAgentTypes();
		pack.addParameterSweep(sweepDiscrete);
		pack.initialize();
		pack.run();
		pack.generateTimeSeriesGraph(Metrics_StealthAvg, "Data loss ratio",
				"Time", "stealth");
		pack.generateTimeSeriesGraph(Metrics_ModelDistance, "Average delay",
				"Time", "error");

	}

	/**
	 * Creates the different types of agents
	 * 
	 * @return
	 */
	public static ParameterSweep getAgentTypes() {
		ParameterSweep sweepDiscrete = new ParameterSweep("protocols");
		ScenarioDistinguisher sd = new ScenarioDistinguisher(
				"Directed diffusion 25");
		sd.setDistinguisher(SensorAgentClass.DirectedDiffusion);
		sd.setDistinguisher(AgentParameter_DirectedDiffusion_ReportInterval,
				25.0); //
		// sweepDiscrete.addDistinguisher(sd);
		sd = new ScenarioDistinguisher("Directed diffusion 10");
		sd.setDistinguisher(SensorAgentClass.DirectedDiffusion);
		sd.setDistinguisher(AgentParameter_DirectedDiffusion_ReportInterval,
				10.0); //
		// sweepDiscrete.addDistinguisher(sd);
		// TAB variants
		sd = new ScenarioDistinguisher("TAB 0.001");
		sd.setDistinguisher(SensorAgentClass.TryAndBounce);
		sd.setDistinguisher(
				AgentParameter_TryAndBounce_TransmissionCostThreshold, 0.001);
		// sweepDiscrete.addDistinguisher(sd);
		sd = new ScenarioDistinguisher("TAB 0.002");
		sd.setDistinguisher(SensorAgentClass.TryAndBounce);
		sd.setDistinguisher(
				AgentParameter_TryAndBounce_TransmissionCostThreshold, 0.002);
		sweepDiscrete.addDistinguisher(sd);
		sd = new ScenarioDistinguisher("TAB 0.003");
		sd.setDistinguisher(SensorAgentClass.TryAndBounce);
		sd.setDistinguisher(
				AgentParameter_TryAndBounce_TransmissionCostThreshold, 0.003);
		// sweepDiscrete.addDistinguisher(sd);
		sd = new ScenarioDistinguisher("TAB 0.004");
		sd.setDistinguisher(SensorAgentClass.TryAndBounce);
		sd.setDistinguisher(
				AgentParameter_TryAndBounce_TransmissionCostThreshold, 0.004);
		sweepDiscrete.addDistinguisher(sd);
		// TAB variants
		sd = new ScenarioDistinguisher("TAB-ES 0.001");
		sd.setDistinguisher(SensorAgentClass.TryAndBounceES);
		sd.setDistinguisher(
				AgentParameter_TryAndBounce_TransmissionCostThreshold, 0.001);
		// sweepDiscrete.addDistinguisher(sd);
		sd = new ScenarioDistinguisher("TAB-ES 0.003");
		sd.setDistinguisher(SensorAgentClass.TryAndBounceES);
		sd.setDistinguisher(
				AgentParameter_TryAndBounce_TransmissionCostThreshold, 0.003);
		// sweepDiscrete.addDistinguisher(sd);

		// simple agent variants
		sd = new ScenarioDistinguisher("Directed diffusion 5");
		sd.setDistinguisher(SensorAgentClass.SimpleIntruderTracking);
		sd.setDistinguisher(AgentParameter_SimpleIntruderTracking_Interval, 5.0);
		sweepDiscrete.addDistinguisher(sd);
		sd = new ScenarioDistinguisher("Directed diffusion 10");
		sd.setDistinguisher(SensorAgentClass.SimpleIntruderTracking);
		sd.setDistinguisher(AgentParameter_SimpleIntruderTracking_Interval,
				10.0);
		sweepDiscrete.addDistinguisher(sd);

		return sweepDiscrete;
	}

}
