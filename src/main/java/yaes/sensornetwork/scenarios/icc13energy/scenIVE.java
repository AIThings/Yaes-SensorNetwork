package yaes.sensornetwork.scenarios.icc13energy;

import java.io.File;
import java.io.IOException;

import yaes.framework.simulation.Simulation;
import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.parametersweep.ExperimentPackage;
import yaes.framework.simulation.parametersweep.ParameterSweep;
import yaes.framework.simulation.parametersweep.ParameterSweepHelper;
import yaes.framework.simulation.parametersweep.ScenarioDistinguisher;
import yaes.sensornetwork.SensorNetworkSimulation;
import yaes.sensornetwork.constSensorNetwork;
import yaes.ui.simulationcontrol.SimulationControlGui;

/**
 * The main class implementing the behavior for the work where information value
 * is traded for energy efficiency (planned submission to ICC-2013)
 * 
 * @author Lotzi Boloni
 * 
 */
public class scenIVE implements IveConstants {
    public static final File outputDir = new File("data/ive/output");
    public static final File output2Dir = new File("data/ive/output2");
    public static final File graphDir = new File("data/ive/graphs");
    public static final File logDir = new File("data/ive/log");

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
        model.setContextClass(IveContext.class);
        model.setSimulationClass(SensorNetworkSimulation.class);
        model.setStopTime(5000);
        model.setParameter(constSensorNetwork.SensorDeployment_SensorRange,
                150.0);
        model.setParameter(
                constSensorNetwork.SensorDeployment_TransmissionRange, 130.0);
        model.setParameter(constSensorNetwork.SensorDeployment_SensorNodeCount,
                80);
        model.setParameter(constSensorNetwork.SensorDeployment_SinkNodeX,
                1100.0);
        model.setParameter(constSensorNetwork.SensorDeployment_SinkNodeY, 600.0);
        model.setParameter(SensorArrangement.GRID_WITH_NOISE); // GRID
        model.setParameter(
                constSensorNetwork.Intruders_MovementRandomSeed, 100);
        model.setParameter(VisualDisplay.NO);
        model.setParameter(constSensorNetwork.SimControl_VoMessageInspector, 0);
        model.setParameter(constSensorNetwork.SimControl_OutputDir,
                outputDir.toString());
        model.setParameter(SimControl_KeepTimeSeries, 0);
        model.setParameter(constSensorNetwork.Intruders_Number, 5);
        model.setParameter(constSensorNetwork.SensorAgentClass.Ive);
        model.setParameter(AgentParameter_SimpleIntruderTracking_Interval, 10.0);
        // IVE specific parameters - turn on everything to strongest value
        model.setParameter(IveSensorEstimation.INERTIAL);
        model.setParameter(IveSinkEstimation.INERTIAL);
        model.setParameter(IveSensorNodeReasoner_Occlusion.USE_OCCLUSION);
        model.setParameter(IveTransmissionPolicy.PERIODIC);// VOI_THRESHOLD);
        
        model.setParameter(IveSensorNodeReasoner_Interval, 10.0);
        model.setParameter(IveSensorNodeReasoner_VoiThreshold, 10.0);        
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
        sip.setStopTime(150);
        IveContext context = new IveContext();
        boolean interactive = true;
        if (!interactive) {
            Simulation.simulate(sip, SensorNetworkSimulation.class, context,
                    logDir);
        } else {
            sip.setParameter(VisualDisplay.YES);
            SimulationControlGui.simulate(sip, SensorNetworkSimulation.class,
                    context);
        }
    }

    /**
     * 
     * Add the different variants
     * 
     * @return
     */
    public static ParameterSweep getAgentTypes() {
        ParameterSweep sweepDiscrete = new ParameterSweep("protocols");
        ScenarioDistinguisher sd = null;
        // ODPR agents 10 second
        sd = new ScenarioDistinguisher("C-LK+S-ODPR-10");
        sd.setDistinguisher(IveSinkEstimation.LAST_KNOWN);
        sd.setDistinguisher(IveSensorEstimation.LAST_KNOWN);
        sd.setDistinguisher(IveTransmissionPolicy.PERIODIC);
        sd.setDistinguisher(IveSensorNodeReasoner_Interval, 10.0);
        //sweepDiscrete.addDistinguisher(sd);
        // ODPR agents 20 second
        sd = new ScenarioDistinguisher("C-LK+S-ODPR-50");
        sd.setDistinguisher(IveSinkEstimation.LAST_KNOWN);
        sd.setDistinguisher(IveSensorEstimation.LAST_KNOWN);
        sd.setDistinguisher(IveTransmissionPolicy.PERIODIC);
        sd.setDistinguisher(IveSensorNodeReasoner_Interval, 50.0);
        //sweepDiscrete.addDistinguisher(sd);
        // ODPR agents 200 second
        sd = new ScenarioDistinguisher("C-LK+S-ODPR-200");
        sd.setDistinguisher(IveSinkEstimation.LAST_KNOWN);
        sd.setDistinguisher(IveSensorEstimation.LAST_KNOWN);
        sd.setDistinguisher(IveTransmissionPolicy.PERIODIC);
        sd.setDistinguisher(IveSensorNodeReasoner_Interval, 200.0);
        sweepDiscrete.addDistinguisher(sd);
        
        // ScenarioDistinguisher sd = new ScenarioDistinguisher("Simple");
        // sweepDiscrete.addDistinguisher(sd);

        // Last-Known sink estimator, Last-Known sensor estimator, No occlusion
        // inference

        sd = new ScenarioDistinguisher("C-LK+S-IVE-LK-0");
        sd.setDistinguisher(IveSinkEstimation.LAST_KNOWN);
        sd.setDistinguisher(IveSensorEstimation.LAST_KNOWN);
        sd.setDistinguisher(IveTransmissionPolicy.VOI_THRESHOLD);
        sd.setDistinguisher(IveSensorNodeReasoner_VoiThreshold, 0.0);
        //sweepDiscrete.addDistinguisher(sd);
        
        sd = new ScenarioDistinguisher("C-LK+S-IVE-LK-1");
        sd.setDistinguisher(IveSinkEstimation.LAST_KNOWN);
        sd.setDistinguisher(IveSensorEstimation.LAST_KNOWN);
        sd.setDistinguisher(IveTransmissionPolicy.VOI_THRESHOLD);
        sd.setDistinguisher(IveSensorNodeReasoner_VoiThreshold, 1.0);
        //sweepDiscrete.addDistinguisher(sd);

        sd = new ScenarioDistinguisher("C-LK+S-IVE-LK-5");
        sd.setDistinguisher(IveSinkEstimation.LAST_KNOWN);
        sd.setDistinguisher(IveSensorEstimation.LAST_KNOWN);
        sd.setDistinguisher(IveTransmissionPolicy.VOI_THRESHOLD);
        sd.setDistinguisher(IveSensorNodeReasoner_VoiThreshold, 5.0);
        //sweepDiscrete.addDistinguisher(sd);
        
        
        
        // Inertial sink estimator, Last-Known sensor estimator, No occlusion
        // inference
        sd = new ScenarioDistinguisher("C-IE+S-IVE-IE-0");
        sd.setDistinguisher(IveSinkEstimation.INERTIAL);
        sd.setDistinguisher(IveSensorEstimation.INERTIAL);
        sd.setDistinguisher(IveTransmissionPolicy.VOI_THRESHOLD);
        sd.setDistinguisher(IveSensorNodeReasoner_VoiThreshold, 0.0);
        sweepDiscrete.addDistinguisher(sd);

        // Inertial sink estimator, Inertial sensor estimator, No occlusion
        // inference
        sd = new ScenarioDistinguisher("C-IE+S-IVE-IE-1");
        sd.setDistinguisher(IveSinkEstimation.INERTIAL);
        sd.setDistinguisher(IveSensorEstimation.INERTIAL);
        sd.setDistinguisher(IveTransmissionPolicy.VOI_THRESHOLD);
        sd.setDistinguisher(IveSensorNodeReasoner_VoiThreshold, 1.0);
        //sweepDiscrete.addDistinguisher(sd);

        // Inertial sink estimator, Inertial sensor estimator, Occlusion
        // inference
        sd = new ScenarioDistinguisher("C-IE+S-IVE-IE-5");
        sd.setDistinguisher(IveSinkEstimation.INERTIAL);
        sd.setDistinguisher(IveSensorEstimation.INERTIAL);
        sd.setDistinguisher(IveTransmissionPolicy.VOI_THRESHOLD);
        sd.setDistinguisher(IveSensorNodeReasoner_VoiThreshold, 5.0);
        // sweepDiscrete.addDistinguisher(sd);

        return sweepDiscrete;
    }

    /**
     * @param model
     */
    public static void runFullSimulation() {
        SimulationInput model = createDefaultSimulationInput();
        model.setStopTime(7200);
        // variable intruder nodes
        compareVariableIntruderNodes(model);
        // timescale
        plotEvolutionOfVoI(model);
    }
    
    
    
    /**
     * Creates the graphs for plotting the different values function of the 
     * number of intruder nodes
     * 
     * @param model
     */
    private static void compareVariableIntruderNodes(SimulationInput model) {
        ExperimentPackage pack = new ExperimentPackage(outputDir, graphDir);
        pack.setModel(model);
        ParameterSweep sweepDiscrete = getAgentTypes();
        pack.addParameterSweep(sweepDiscrete);
        ParameterSweep sweepIntruders =
                ParameterSweepHelper
                        .generateParameterSweepInteger("Intruder nodes",
                                constSensorNetwork.Intruders_Number,
                                1, 40, 5);
        pack.addParameterSweep(sweepIntruders);
        pack.setVariableDescription(
                constSensorNetwork.Intruders_Number,
                "Number of intruders in the region");
        pack.setVariableDescription(
                Metrics_IVE_ValueOfInformation,
                "Value of information");
        pack.setVariableDescription(
                Metrics_MessagesSentSum,
                "No. of messages sent");
        pack.setVariableDescription(
                Metrics_TransmissionEnergySum,
                "Sum of transmission energy (J)");
        pack.initialize();
        pack.run();
        pack.generateGraph(Metrics_MessagesSentSum, "Number of messages",
                "number_of_messages");
        pack.generateGraph(Metrics_TransmissionEnergySum,
                "Transmission energy", "transmission_energy");
        pack.generateGraph(Metrics_IVE_ValueOfInformation,
                "Value of Information", "value_of_information");
    }

    /**
     * @param model
     */
    private static void plotEvolutionOfVoI(SimulationInput rootModel) {
        SimulationInput model = new SimulationInput(rootModel);
        model.setParameter(constSensorNetwork.Intruders_Number, 40);
        ExperimentPackage pack = new ExperimentPackage(output2Dir, graphDir);
        pack.setModel(model);
        ParameterSweep sweepDiscrete = getAgentTypes();
        pack.addParameterSweep(sweepDiscrete);
        pack.initialize();
        pack.run();
        pack.generateTimeSeriesGraph(Metrics_IVE_DeltaValueOfInformation, "Value of information", "Time", "ts_voi");
        pack.generateTimeSeriesGraph(Metrics_IVE_VoIInstantRatio, "Achieved VoI / Max VoI", "Time", "ts_voiratio");
    }

    
}
