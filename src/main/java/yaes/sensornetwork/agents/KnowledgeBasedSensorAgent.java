/**
 * 
 */
package yaes.sensornetwork.agents;

import java.awt.geom.Rectangle2D;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.Environment;
import yaes.sensornetwork.constSensorNetwork.KBSA_KnowledgeMetric;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingMessageConstants;
import yaes.sensornetwork.knowledge.AccuracyMetric;
import yaes.sensornetwork.knowledge.ILocationEstimator;
import yaes.sensornetwork.knowledge.IntruderSighingHelper;
import yaes.sensornetwork.knowledge.IntruderSighting;
import yaes.sensornetwork.knowledge.IntruderSightingHistory;
import yaes.sensornetwork.knowledge.SimpleEstimator;
import yaes.sensornetwork.knowledge.UncertainMovementSegment;
import yaes.sensornetwork.model.Perception;
import yaes.sensornetwork.model.SensorNetworkMessageConstants;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.scenarios.tryandbounce.ConstantStealthCostSendingDecision;
import yaes.world.physical.location.INamedMoving;

/**
 * 
 * Implements the intelligent store and dump agent
 * 
 * @author lboloni
 * 
 */
public class KnowledgeBasedSensorAgent extends ForwarderSensorAgent implements
		IntruderTrackingMessageConstants {

	private static final long serialVersionUID = -1550402471120550445L;
	ConstantStealthCostSendingDecision stealthCostAccounting = new ConstantStealthCostSendingDecision();
	// True if the agent adds knowledge from messages which overheard
	private boolean useOverheardKnowledge;
	// True if the agent adds knowledge from messages which pass through
	private boolean usePassThroughKnowledge;
	// local model of intruder sighting
	private IntruderSightingHistory localIsh = new IntruderSightingHistory();
	private ILocationEstimator localEstimator = new SimpleEstimator();
	private IntruderSightingHistory sinkIsh = new IntruderSightingHistory();
	private ILocationEstimator sinkEstimator = new SimpleEstimator();
	// private double minimumImprovement = 0; // -Double.MAX_VALUE;
	private KBSA_KnowledgeMetric knowledgeMetric = KBSA_KnowledgeMetric.EstimationError;
	private Rectangle2D.Double interestArea;

	public KnowledgeBasedSensorAgent(String name,
			SensorNetworkWorld sensingManager, Environment env) {
		super(name, sensingManager);
		SendingDecisionInterval sd = new SendingDecisionInterval();
		sd.setInterval(2.0);
		sendingDecision = sd;
		interestArea = env.getInterestArea();
	}

	/**
	 * Processes all the reports
	 * 
	 */
	@Override
	protected void afterProcessingPerceptions() {
		if (!sendingDecision.readyToSend(this)) {
			return;
		}
		SimpleEntry<ACLMessage, Double> optimal = createOptimalMessage();
		ACLMessage m = optimal.getKey();
		if (m != null) {
			// make this a data message and send it to the sink
			m.setValue(SensorNetworkMessageConstants.FIELD_CONTENT,
					SensorNetworkMessageConstants.MESSAGE_DATA);
			m.setValue(SensorNetworkMessageConstants.FIELD_INTENSITY, 1);
			m.setDestination(getForwardingDestination());
			transmit(m);
			IntruderSighting sinkSighting = IntruderSighingHelper
					.extractSightingFromMessage(m);
			sinkIsh.addSighting(sinkSighting, getSensorWorld().getTime());
			sendingDecision.sent(this);
		}
	}

	@Override
	protected void beforeProcessingPerceptions() {
		// stealthCostAccounting.updateOnTime();
	}

	/**
	 * Creates the optimal message to send - returns null if there is no
	 * improvement
	 * 
	 */
	public SimpleEntry<ACLMessage, Double> createOptimalMessage() {
		double bestImprovement = -1.0;
		ACLMessage bestMessage = null;
		for (double time = 0; time < getSensorWorld().getTime(); time = time + 1.0) {
			for (String intruder : localIsh.getIntruders()) {
				ACLMessage m = IntruderSighingHelper.createMessage(getName(),
						time, intruder, localEstimator, localIsh);
				if (m == null) {
					continue;
				}
				IntruderSighting sinkSighting = IntruderSighingHelper
						.extractSightingFromMessage(m);
				double improvement = getImprovement(sinkSighting);
				if (improvement > bestImprovement) {
					bestImprovement = improvement;
					bestMessage = m;
				}
			}
		}
		return new SimpleEntry<>(bestMessage, bestImprovement);
	}

	/**
	 * 
	 * This function describes the value of the sink utility function
	 * 
	 * Currently it is a sum of the current estimation error - but it will need
	 * to be specified in several ways - overwriting etc.
	 * 
	 * @return
	 */
	public double estimateSinkError() {
		double retval = 0.0;
		switch (knowledgeMetric) {
		case EstimationError: {
			// FIXME: inefficient...
			for (String intruder : localIsh.getIntruders()) {
				double error = AccuracyMetric.currentEstimationError(
						getSensorWorld().getTime(), intruder, sinkEstimator,
						sinkIsh, localEstimator, localIsh);
				retval += Math.min(error, 1000.0);
			}
			break;
		}
		case InterestAreaEstimationError: {
			// FIXME: inefficient...
			for (String intruder : localIsh.getIntruders()) {
				double error = AccuracyMetric
						.currentInterestAreaEstimationError(interestArea,
								getSensorWorld().getTime(), intruder,
								sinkEstimator, sinkIsh, localEstimator,
								localIsh);
				retval += error;
			}
			break;
		}
		case UncertaintyArea: {
			for (String intruder : localIsh.getIntruders()) {
				SimpleEntry<List<UncertainMovementSegment>, Double> uncertaintyAreas = AccuracyMetric
						.uncertaintyArea(0, getSensorWorld().getTime(),
								intruder, 20, sinkEstimator, sinkIsh);
				retval += uncertaintyAreas.getValue();
			}
			break;
		}
		default:
			break;
		}
		return retval;
	}

	/**
	 * The improvement in the perceived sink value
	 * 
	 * @param sinkSighting
	 * @return
	 */
	private double getImprovement(IntruderSighting sinkSighting) {
		double oldError = estimateSinkError();
		sinkIsh.addSighting(sinkSighting, getSensorWorld().getTime());
		double newError = estimateSinkError();
		sinkIsh.removeSighting(sinkSighting);
		double oldoldError = estimateSinkError();
		if (oldError != oldoldError) {
			throw new Error("removeSighting is not working!!!");
		}
		double improvement = oldError - newError;
		return improvement;
	}

	/**
	 * @return the knowledgeMetric
	 */
	public KBSA_KnowledgeMetric getKnowledgeMetric() {
		return knowledgeMetric;
	}

	@Override
	protected void handleIntruderPresence(final Perception p) {
		INamedMoving threat = p.getMovingObject();
		IntruderSighting is = new IntruderSighting(getName(), world.getTime(),
				threat.getName(), threat.getLocation());
		localIsh.addSighting(is, world.getTime());
	}

	/**
	 * This agent uses the overhearing that his own transmission was confirmed
	 * 
	 * @param message
	 */
	@Override
	protected void handleOverheardMessage(final ACLMessage m) {
		// update your model of the sink's model
		IntruderSighting is = IntruderSighingHelper
				.extractSightingFromMessage(m);
		if (useOverheardKnowledge) {
			localIsh.addSighting(is, getSensorWorld().getTime());
			sinkIsh.addSighting(is, getSensorWorld().getTime());
		}
	}

	/**
	 * Handles a received message.
	 * 
	 * @param m
	 */
	@Override
	protected void handleReceivedMessage(final ACLMessage m) {
		IntruderSighting is = IntruderSighingHelper
				.extractSightingFromMessage(m);
		if (usePassThroughKnowledge) {
			localIsh.addSighting(is, getSensorWorld().getTime());
			sinkIsh.addSighting(is, getSensorWorld().getTime());
		}
		// now perform the routing
		forwardMessage(m);
	}

	/**
	 * @return the useOverheardKnowledge
	 */
	public boolean isUseOverheardKnowledge() {
		return useOverheardKnowledge;
	}

	/**
	 * @return the usePassThroughKnowledge
	 */
	public boolean isUsePassThroughKnowledge() {
		return usePassThroughKnowledge;
	}

	/**
	 * @param knowledgeMetric
	 *            the knowledgeMetric to set
	 */
	public void setKnowledgeMetric(KBSA_KnowledgeMetric knowledgeMetric) {
		this.knowledgeMetric = knowledgeMetric;
	}

	/**
	 * @param useOverheardKnowledge
	 *            the useOverheardKnowledge to set
	 */
	public void setUseOverheardKnowledge(boolean useOverheardKnowledge) {
		this.useOverheardKnowledge = useOverheardKnowledge;
	}

	/**
	 * @param usePassThroughKnowledge
	 *            the usePassThroughKnowledge to set
	 */
	public void setUsePassThroughKnowledge(boolean usePassThroughKnowledge) {
		this.usePassThroughKnowledge = usePassThroughKnowledge;
	}

	/**
	 * Prints out an agent for debugging purposes
	 */
	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer(
				"KnowledgeBasedSensorAgent: " + getName());
		return buffer.toString();
	}
}
