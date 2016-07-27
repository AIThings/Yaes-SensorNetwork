package yaes.sensornetwork.scenarios.bridgeprotection;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yaes.framework.simulation.SimulationInput;
import yaes.framework.simulation.SimulationOutput;
import yaes.sensornetwork.Environment;
import yaes.sensornetwork.EnvironmentGenerator;
import yaes.sensornetwork.SensorNetworkContext;
import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.agents.ForwarderSensorAgent;
import yaes.sensornetwork.agents.SensorRoutingHelper;
import yaes.sensornetwork.applications.intrudertracking.IntruderGenerator;
import yaes.sensornetwork.model.SensorNode;
import yaes.sensornetwork.scenarios.bridgeprotection.BpaAgent.BpaState;
import yaes.sensornetwork.visualization.paintBPANode;
import yaes.sensornetwork.visualization.paintCatastrophicEvent;
import yaes.sensornetwork.visualization.paintSensorNode;
import yaes.ui.text.TextUi;
import yaes.ui.visualization.Visualizer;

public class BpaContext extends SensorNetworkContext implements BpaConstants {
	final static Logger logger = LoggerFactory.getLogger(SensorNetworkContext.class);
	private static final long serialVersionUID = 7706523352468637638L;
	public CatastrophicEvent catastrophicEvent;

	/**
	 * Applies the BPA algorithm by modifying the state of the nodes in the
	 * appropriate way.
	 * 
	 * In the current version, the bridge is determined manually, but the other
	 * ones are not.
	 * 
	 * @param sip
	 */
	public void applyBPA() {
		if (!sip.getParameterEnum(SensorAgentClass.class).equals(SensorAgentClass.BridgeProtectionAlgorithm)) {
			return;
		}
		if (sip.getParameterInt(BRIDGE_PROTECTION_ACTIVE) == 0) {
			return;
		}
		// FIXME: choose the bridge here
		String bridgeName = "S-43";
		AbstractSensorAgent bridgeAgent = null;
		for (SensorNode candidate : sensorWorld.getSensorNodes()) {
			if (candidate.getName().equals(bridgeName)) {
				bridgeAgent = candidate.getAgent();
				break;
			}
		}
		List<Set<AbstractSensorAgent>> classes = BpaRouterHelper.determineNodeClasses(bridgeAgent, sensorWorld);
		Set<AbstractSensorAgent> farSide = classes.get(0);
		Set<AbstractSensorAgent> nearSide = classes.get(1);
		Set<AbstractSensorAgent> gates = classes.get(2);
		Set<AbstractSensorAgent> fanouts = classes.get(3);
		for (SensorNode node : sensorWorld.getSensorNodes()) {
			if (!node.isEnabled()) {
				continue;
			}
			BpaAgent agent = (BpaAgent) node.getAgent();
			if (agent.equals(bridgeAgent)) {
				agent.setBpaState(BpaState.BRIDGE);
				List<String> fanoutNames = new ArrayList<>();
				for (AbstractSensorAgent fanoutSenorAgent : fanouts) {
					fanoutNames.add(fanoutSenorAgent.getName());
				}
				agent.setFanoutNodes(fanoutNames);
				continue;
			}
			if (farSide.contains(agent)) {
				agent.setBpaState(BpaState.FARSIDE);
				continue;
			}
			if (nearSide.contains(agent)) {
				agent.setBpaState(BpaState.NEARSIDE);
				continue;
			}
			if (gates.contains(agent)) {
				agent.setBpaState(BpaState.GATE);
				continue;
			}
			if (fanouts.contains(agent)) {
				agent.setBpaState(BpaState.FANOUT);
				continue;
			}
			TextUi.println("Node " + agent.getName() + " is unclassified!");
		}
		routePaths();
	}

	protected void routePaths() {
		// set the fanout nodes forwarding nodes to be disjoint
		List<AbstractSensorAgent> sourceAgents = SensorRoutingHelper.getSensorAgents(sensorWorld,
				ForwarderSensorAgent.class);
		Set<String> fanoutNext = new HashSet<>();
		for (AbstractSensorAgent source : sourceAgents) {
			if (((BpaAgent) source).getBpaState() != BpaAgent.BpaState.FANOUT) {
				continue;
			}
			// all the fanout nodes
			List<AbstractSensorAgent> reminderAgents = new ArrayList<>();
			for (AbstractSensorAgent asa : sourceAgents) {
				if (((BpaAgent) asa).getBpaState() != BpaAgent.BpaState.NEARSIDE) {
					continue;
				}
				if (fanoutNext.contains(asa.getName())) {
					continue;
				}
				reminderAgents.add(asa);
				reminderAgents.add(source);
			}
			reminderAgents.add(theSinkNode.getAgent());
			List<String> path = SensorRoutingHelper.getShortestPath(reminderAgents, source, theSinkNode.getAgent());

			if (sip.getParameterInt(RNG_ROUTING) == 1)
				path = SensorRoutingHelper.getRNGShortestPath(reminderAgents, source, theSinkNode.getAgent());

			else if (sip.getParameterInt(GREEDY_ROUTING) == 1)
				path = SensorRoutingHelper.getGreedyNeighborPaths(reminderAgents, source, theSinkNode.getAgent());

			if (path != null) {
				String forwardingDestination = path.get(1);
				((ForwarderSensorAgent) source).setForwardingDestination(forwardingDestination);
				fanoutNext.add(forwardingDestination);
			}
		}
	}

	@Override
	protected Environment createEnvironment() {
		Environment retval = EnvironmentGenerator.genenvSensorCoveredInterestArea();
		return retval;
	}

	/**
	 * Creates the intruder nodes: in this case, a bunch of animals
	 */
	@Override
	protected void createIntruderNodes() {
		IntruderGenerator.genintrForagingAnimals(this);
	}

	/**
	 * Add the catastrophic event
	 */
	@Override
	public void createVisualRepresentation(Visualizer existingVisualizer) {
		super.createVisualRepresentation(existingVisualizer);
		visualizer.addObject(catastrophicEvent, new paintCatastrophicEvent());
		visualizer.getVisualizationProperties().setPropertyValue(paintSensorNode.vpropPaintRoutes, new Boolean(true));
	}

	@Override
	public void initialize(SimulationInput sip, SimulationOutput sop) {
		super.initialize(sip, sop);
		// create the catastrophic event
		catastrophicEvent = new CatastrophicEvent();
		// catastrophicEvent.setActive(true);
		// center must be 600,
		// Shape p1 = new Ellipse2D.Double(290, -10, 400, 400);
		Shape p1 = new Ellipse2D.Double(350, -10, 400, 400);
		catastrophicEvent.getShapes().add(p1);
		// Shape p2 = new Ellipse2D.Double(310, 500, 400, 400);
		Shape p2 = new Ellipse2D.Double(350, 500, 400, 400);
		catastrophicEvent.getShapes().add(p2);
		// changing the painter
		painterNode = new paintBPANode(sensorWorld);
		//
	}

	/**
	 * Turns on the catastrophic event and applies whatever changes are
	 * necessary.
	 */
	public void turnOnCatastrophicEvent() {
		catastrophicEvent.applyEvent(sensorWorld);
		// redoing the paths
		SensorRoutingHelper.createPathsForForwarderSensorAgents(theSinkNode.getAgent(), sensorWorld);
		if (sip.getParameterInt(RNG_ROUTING) == 1)
			SensorRoutingHelper.createRNGPathsForForwarderSensorAgents(theSinkNode.getAgent(), sensorWorld);
		else if (sip.getParameterInt(GREEDY_ROUTING) == 1)
			SensorRoutingHelper.createGreedyPathsForForwarderSensorAgents(theSinkNode.getAgent(), sensorWorld);

		if (sip.getParameterEnum(SensorAgentClass.class).equals(SensorAgentClass.BridgeProtectionAlgorithm)) {
			applyBPA();
		}
	}

	public void turnOnNodePlacementEvent() {
		logger.info("Turning on the node placement");
		String bridgeName = "S-30";
		AbstractSensorAgent bridgeAgent = null;
		for (SensorNode candidate : sensorWorld.getSensorNodes()) {
			if (candidate.getName().equals(bridgeName)) {
				bridgeAgent = candidate.getAgent();
				candidate.setEnabled(true);
				break;
			}
		}
		List<Set<AbstractSensorAgent>> classes = BpaRouterHelper.determineNodeClasses(bridgeAgent, sensorWorld);
		Set<AbstractSensorAgent> farSide = classes.get(0);
		Set<AbstractSensorAgent> nearSide = classes.get(1);
		Set<AbstractSensorAgent> gates = classes.get(2);
		Set<AbstractSensorAgent> fanouts = classes.get(3);
		for (SensorNode node : sensorWorld.getSensorNodes()) {
			if (!node.isEnabled()) {
				continue;
			}
			BpaAgent agent = (BpaAgent) node.getAgent();
			if (agent.equals(bridgeAgent)) {
				agent.setBpaState(BpaState.BRIDGE);
				List<String> fanoutNames = new ArrayList<>();
				for (AbstractSensorAgent asa : fanouts) {
					fanoutNames.add(asa.getName());
				}
				agent.setFanoutNodes(fanoutNames);
				continue;
			}
			// if (farSide.contains(agent)) {
			// agent.setBpaState(BpaState.FARSIDE);
			// continue;
			// }
			// if (nearSide.contains(agent)) {
			// agent.setBpaState(BpaState.NEARSIDE);
			// continue;
			// }
			// if (gates.contains(agent)) {
			// agent.setBpaState(BpaState.GATE);
			// continue;
			// }
			// if (fanouts.contains(agent)) {
			// agent.setBpaState(BpaState.FANOUT);
			// continue;
			// }
			TextUi.println("Node " + agent.getName() + " is unclassified!");
		}
		routePaths();
	}
}
