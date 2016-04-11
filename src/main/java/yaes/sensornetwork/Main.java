/*
   This file is part of the Intelligent Store and Dump project
   an application of the YAES simulator.
    
   Created on: Aug 1, 2010
 
   storeanddump.KnowledgeHistory
 
   Copyright (c) 2010 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork;

import java.awt.Toolkit;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import yaes.Version;
import yaes.sensornetwork.model.constSensorNetworkWorld;
import yaes.sensornetwork.scenarios.bridgeprotection.scenBridgeProtection;
import yaes.sensornetwork.scenarios.deepinspection.scenValueOfDeepInspection;
import yaes.sensornetwork.scenarios.icc13energy.scenIVE;
import yaes.sensornetwork.scenarios.tryandbounce.scenTryAndBounce;
import yaes.ui.simulationcontrol.SimulationReplayTxt;
import yaes.ui.text.TextUi;

public class Main implements constSensorNetwork, constSensorNetworkWorld, Serializable {
	private static final long serialVersionUID = -5762874448443924627L;
	private static final String MENU_DEEP_INSPECTION_RUN =
            "Value of deep inspection - simple run";
    private static final String MENU_DEEP_INSPECTION_VIEW =
            "Value of deep inspection - view simple run";
    private static final String MENU_DEEP_INSPECTION_SPAN =
            "Value of deep inspection - span the simulation";
    private static final String MENU_TAB_RUN = "Try and bounce - simple run";
    private static final String MENU_TAB_VIEW =
            "Try and bounce - view simple run";
    private static final String MENU_TAB_SPAN =
            "Try and bounce - span the simulation";
    private static final String MENU_BPA_RUN = "Bridge protection - simple run";
    private static final String MENU_BPA_RNG_RUN = "Bridge protection - relative neighbor graph run";
    private static final String MENU_BPA_GREEDY_RUN = "Bridge protection - geographical greedy run";


    private static final String MENU_BPA_VIEW =
            "Bridge protection - view simple run";
    private static final String MENU_BPA_SPAN =
            "Bridge protection - span the simulation for comparing different techniques";
    private static final String MENU_BPA_SIMPLE_SPAN =
            "Bridge protection - span the simulation varying tranmission ranges for Simple BPA";
    private static final String MENU_BPA_RNG_SPAN =
            "Bridge protection - span the simulation varying tranmission ranges for RNG BPA";
    private static final String MENU_BPA_GREEDY_SPAN =
            "Bridge protection - span the simulation varying tranmission ranges for Greedy BPA";

    private static final String MENU_IVE_RUN =
            "Information-value energy (ICC-13) - simple run";
    private static final String MENU_IVE_VIEW =
            "Information-value energy (ICC-13) - view simple run";
    private static final String MENU_IVE_SPAN =
            "Information-value energy (ICC-13) - span the simulation";

    /**
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        TextUi.println(Version.versionString());
        final List<String> menu = new ArrayList<>();
        // String result = Main.MENU_IVE_RUN;
        String result = null;
        String defaultChoice = Main.MENU_IVE_SPAN;
        // menu.add("-Value of deep inspection");
        menu.add(Main.MENU_DEEP_INSPECTION_RUN);
        menu.add(Main.MENU_DEEP_INSPECTION_VIEW);
        menu.add(Main.MENU_DEEP_INSPECTION_SPAN);
        menu.add(Main.MENU_TAB_RUN);
        menu.add(Main.MENU_TAB_VIEW);
        menu.add(Main.MENU_TAB_SPAN);
        menu.add(Main.MENU_BPA_RUN);
        menu.add(Main.MENU_BPA_RNG_RUN);
        menu.add(Main.MENU_BPA_GREEDY_RUN);
        menu.add(Main.MENU_BPA_VIEW);
        menu.add(Main.MENU_BPA_SPAN);
        menu.add(Main.MENU_BPA_SIMPLE_SPAN);
        menu.add(Main.MENU_BPA_RNG_SPAN);
        menu.add(Main.MENU_BPA_GREEDY_SPAN);
        
        menu.add(Main.MENU_IVE_RUN);
        menu.add(Main.MENU_IVE_VIEW);
        menu.add(Main.MENU_IVE_SPAN);
        if (result == null) {
            result = TextUi.menu(menu, defaultChoice, "Choose:");
        }
        switch (result) {
        // Value of deep inspection scenario
        case Main.MENU_DEEP_INSPECTION_RUN: {
            scenValueOfDeepInspection.doSimpleRun();
            break;
        }
        case Main.MENU_DEEP_INSPECTION_VIEW: {
            SimulationReplayTxt sr =
                    new SimulationReplayTxt(scenValueOfDeepInspection.logDir);
            sr.mainLoop();
            break;
        }
        case Main.MENU_DEEP_INSPECTION_SPAN: {
            scenValueOfDeepInspection.runFullSimulation();
            break;
        }
        // Try and bounce scenario
        case Main.MENU_TAB_RUN: {
            scenTryAndBounce.doSimpleRun();
            break;
        }
        case Main.MENU_TAB_VIEW: {
            SimulationReplayTxt sr =
                    new SimulationReplayTxt(scenTryAndBounce.logDir);
            sr.mainLoop();
            break;
        }
        case Main.MENU_TAB_SPAN: {
            scenTryAndBounce.runFullSimulation();
            break;
        }
        // Bridge protection
        case Main.MENU_BPA_RUN: {
            scenBridgeProtection.doSimpleRun();
            break;
        }
        case Main.MENU_BPA_RNG_RUN: {
            scenBridgeProtection.doSimpleRNGRun();
            break;
        }
        case Main.MENU_BPA_GREEDY_RUN: {
            scenBridgeProtection.doSimpleGreedyRun();
            break;
        }
        case Main.MENU_BPA_VIEW: {
            SimulationReplayTxt sr =
                    new SimulationReplayTxt(scenBridgeProtection.logDir);
            sr.mainLoop();
            break;
        }
        case Main.MENU_BPA_SPAN: {
            scenBridgeProtection.runFullSimulation();
            break;
        }
        case Main.MENU_BPA_SIMPLE_SPAN: {
            scenBridgeProtection.runFullSimpleSimulation();
            break;
        }
        case Main.MENU_BPA_RNG_SPAN: {
            scenBridgeProtection.runFullRNGSimulation();
            break;
        }
        case Main.MENU_BPA_GREEDY_SPAN: {
            scenBridgeProtection.runFullGreedySimulation();
            break;
        }
        // Information-value energy balance
        case Main.MENU_IVE_RUN: {
            scenIVE.doSimpleRun();
            break;
        }
        case Main.MENU_IVE_VIEW: {
            SimulationReplayTxt sr = new SimulationReplayTxt(scenIVE.logDir);
            sr.mainLoop();
            break;
        }
        case Main.MENU_IVE_SPAN: {
            scenIVE.runFullSimulation();
            break;
        }
		default:
			break;
        }
        Toolkit.getDefaultToolkit().beep();
        TextUi.println("Done, exiting");
        System.exit(0);
    }
}
