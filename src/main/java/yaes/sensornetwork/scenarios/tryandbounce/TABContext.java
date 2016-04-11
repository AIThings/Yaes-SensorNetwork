package yaes.sensornetwork.scenarios.tryandbounce;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import yaes.sensornetwork.Environment;
import yaes.sensornetwork.SensorNetworkContext;
import yaes.sensornetwork.constSensorNetwork;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingSinkAgent;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingWorldModel.ItwmType;
import yaes.world.physical.location.Location;

public class TABContext extends SensorNetworkContext implements
		constSensorNetwork, Serializable {

	private static final long serialVersionUID = -4609672493486844403L;

	/**
	 * Creating the sink node - the idea here is that this is a fancier sink,
	 * with the model which does tracking etc.
	 * 
	 * @return
	 */
	@Override
	protected void createSinkNode() {
		double sinkNodeX = sip
				.getParameterDouble(constSensorNetwork.SensorDeployment_SinkNodeX);
		double sinkNodeY = sip
				.getParameterDouble(constSensorNetwork.SensorDeployment_SinkNodeY);
		Location sinkNodeLocation = new Location(sinkNodeX, sinkNodeY);
		theSinkNode = IntruderTrackingSinkAgent.createSinkNode(SINK_NODE_NAME,
				sensorWorld, transmissionRange, sinkNodeLocation,
				environment.getInterestArea(), ItwmType.LAST_KNOWN);
	}

	/**
	 * Make the environment for the stealth scenario. Basically, we are
	 * interested in the interest area, which is taken from the parameters.
	 * 
	 */
	@Override
	protected Environment createEnvironment() {
		double interestRectangleX = sip
				.getParameterDouble(constSensorNetwork.SensorDeployment_InterestRectangleX);
		double interestRectangleY = sip
				.getParameterDouble(constSensorNetwork.SensorDeployment_InterestRectangleY);
		double interestRectangleWidth = sip
				.getParameterDouble(constSensorNetwork.SensorDeployment_InterestRectangleWidth);
		double interestRectangleHeight = sip
				.getParameterDouble(constSensorNetwork.SensorDeployment_InterestRectangleHeight);
		environment = new Environment();
		Rectangle2D.Double interestRectangle = new Rectangle2D.Double(
				interestRectangleX, interestRectangleY, interestRectangleWidth,
				interestRectangleHeight);
		environment.setInterestArea(interestRectangle);
		environment.setSensorDistributionArea(interestRectangle);
		environment.setFullArea(interestRectangle);
		return environment;
	}



}
