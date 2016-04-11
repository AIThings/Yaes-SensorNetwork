/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Aug 1, 2010
 
   storeanddump.KnowledgeHistory
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork;

import yaes.sensornetwork.model.constSensorNetworkWorld;

/**
 * This interface describes the common constants in the sensor network YAES
 * project
 * 
 * @author Lotzi Boloni
 * 
 */
public interface constSensorNetwork extends constSensorNetworkWorld {
	/**
	 * Describes which knowledge metric the knowledge based sensor to use
	 * 
	 * @author Lotzi Boloni
	 * 
	 */
	public enum KBSA_KnowledgeMetric {
		EstimationError, InterestAreaEstimationError, UncertaintyArea
	}

	public enum SensorAgentClass {
		SimpleIntruderTracking, KnowledgeBasedSensor, TryAndBounce,
		TryAndBounceES, DirectedDiffusion, BridgeProtectionAlgorithm, Ive,
		VirtualCoordinate, UnderWater, VideoSensorNetwork, AnjiSensorNetwork
	}

	public enum SensorArrangement {
		GRID, RANDOM, GRID_WITH_NOISE, BENCHMARK
	}

	public enum SinkType {
		DIRECTED_DIFFUSION, STEALTH
	};

	public enum VisualDisplay {
		YES, NO
	};

	// sensor deployment
	public static final String SensorDeployment_SensorNodeCount = "SensorDeployment_SensorNodeCount";
	public static final String SensorDeployment_SinkNodeX = "SensorDeployment_SinkNodeX";
	public static final String SensorDeployment_SinkNodeY = "SensorDeployment_SinkNodeY";
	public static final String SensorDeployment_InterestRectangleX = "SensorDeployment_InterestRectangleX";
	public static final String SensorDeployment_InterestRectangleY = "SensorDeployment_InterestRectangleY";
	public static final String SensorDeployment_InterestRectangleWidth = "SensorDeployment_InterestRectangleWidth";
	public static final String SensorDeployment_InterestRectangleHeight = "SensorDeployment_InterestRectangleHeight";
	public static final String SensorDeployment_TransmissionRange = "SensorDeployment_TransmissionRange";
	public static final String SensorDeployment_SensorRange = "SensorDeployment_SensorRange";
	//
	// intruders
	//
	public enum IntruderScenario {
		RANDOMLY_DISTRIBUTED_CROSSINGS, COMBING, ORBIT, UCF_PARTNERSHIP
	};

	public static final String Intruders_MovementRandomSeed = "Intruders_MovementRandomSeed";
	public static final String Intruders_Number = "Intruders_Number";
	public static final String Intruders_Speed = "Intruders_Speed";
	public static final String Intruders_RandomSeed = "Intruders_RandomSeed";
	//
	// agent parameters
	//
	public static final String AgentParameter_SimpleIntruderTracking_Interval = "AgentParameter_SimpleIntruderTracking_Interval";
	public static final String AgentParameter_TryAndBounce_TransmissionCostThreshold = "AgentParameter_TryAndBounce_TransmissionCostThreshold";
	public static final String AgentParameter_DirectedDiffusion_ReportInterval = "AgentParameters_DirectedDiffusion_ReportInterval";
	public static final String AgentParameter_KBSA_UseKnowledgePassthrough = "AgentParameter_KBSA_UseKnowledgePassthrough";
	public static final String AgentParameter_KBSA_UseKnowledgeOverheard = "AgentParameter_KBSA_UseKnowledgeOverheard";
	//
	// metrics to be measured
	//
	public static final String Metrics_OverallStealth = "Metrics_OverallStealth";
	public static final String Metrics_IndividualStealthPrefix = "Metrics_IndividualStealthPrefix_";
	public static final String Metrics_AbsoluteErrorSum = "Metrics_AbsoluteErrorSum";
	public static final String Metrics_InterestAreaErrorSum = "Metrics_InterestAreaErrorSum";
	public static final String Metrics_TransmissionEnergySum = "Metrics_TransmissionEnergySum";
	public static final String Metrics_MessagesSentSum = "Metrics_MessagesSentSum";
	public static final String Metrics_StealthMin = "Metrics_StealthMin";
	public static final String Metrics_StealthMax = "Metrics_StealthMax";
	public static final String Metrics_StealthAvg = "Metrics_StealthAvg";
	public static final String Metrics_StealthConnected = "Metrics_StealthConnected";
	public static final String Metrics_ModelDistance = "Metrics_ModelDistance";

	// internal metrics (measure the functioning of the system)
	// how many times an intruder had been reported to a node
	public static final String InternalMetrics_TryAndBounce_CountIntruderReported = "InternalMetrics_TryAndBounce_CountIntruderReported";
	// how many times an intruder had been reported and had been found to be a
	// new info
	public static final String InternalMetrics_TryAndBounce_CountIntruderReportedNewInfo = "InternalMetrics_TryAndBounce_CountIntruderReportedNewInfo";
	// how many times had the shadowing reasoning been triggered
	public static final String InternalMetrics_TryAndBounce_CountShadowing = "InternalMetrics_TryAndBounce_CountShadowing";
	// how many times an intruder had been sighted
	public static final String InternalMetrics_TryAndBounce_CountSighted = "InternalMetrics_TryAndBounce_CountSighted";
	public static final String InternalMetrics_TryAndBounce_Bounces = "InternalMetrics_TryAndBounce_Bounces";
	public static final String InternalMetrics_TryAndBounce_Confirmed = "InternalMetrics_TryAndBounce_Confirmed";
	public static final String InternalMetrics_TryAndBounce_Failed = "InternalMetrics_TryAndBounce_Failed";

	// simulation control
	public static final String SimControl_KeepTimeSeries = "SimControl_KeepTimeSeries";
	public static final String SimControl_VoMessageInspector = "SimControl_VoMessageInspector";
	public static final String SimControl_OutputDir = "SimControl_OutputDir";

	// constants require for greedy perimeteric routing
	public static final String MODE = "Routing mode";
	public static final String GREEDY = "Greedy Routing";
	public static final String PERIMETER = "Perimeter Routing";
	public static final String FIRST_EDGE = "First-Edge";
	// public static final String NEXT_EDGE = "Next-Edge";
	// public static final String NEIGHBOR_EDGE = "Neighbor-Edge";
	public static final String LP = "Location where greedy routing failed";
	public static final String LF = "Intersection Point Of Face With Line X to D";
	public static final String SINK_NODE = "The sink node";
	public static final String SENDER_NODE = "The sender Node";
	public static final String NEXT_HOP = "Next-Hop Sensor Node";
	public static final String TTL = "Time-to-Live";
	public static final String MAX_TTL = "Time-to-Live";

}
