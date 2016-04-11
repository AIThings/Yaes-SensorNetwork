package yaes.sensornetwork.applications.intrudertracking;

import java.awt.geom.Rectangle2D;
import java.util.Random;

import yaes.sensornetwork.Environment;
import yaes.sensornetwork.SensorNetworkContext;
import yaes.sensornetwork.constSensorNetwork;
import yaes.world.physical.location.Location;

public class IntruderGenerator {

	/**
	 * An illustration on how we can model a variety of different intruders
	 * moving around in a richly modeled area.
	 * 
	 * This intruder generation assumes that the environment had been generated
	 * using the EnvironmentGenerator.generatePartnershipArea() function.
	 * 
	 * @param context
	 */
	public static void genintrVarietyInPartnershipArea(
			SensorNetworkContext context) {
		Environment env = context.environment;
		for (int i = 1; i <= 5; i++) {
			IntruderHelper.addForagingAnimal(context, context.getRandom(), i);
		}
		IntruderHelper.addFriendlyHuman(context, 1, env.getLandmark("A"),
				env.getLandmark("B"), env.getLandmark("C"),
				env.getLandmark("E"));
		IntruderHelper.addFriendlyHuman(context, 2, env.getLandmark("E"),
				env.getLandmark("C"), env.getLandmark("D"));
		IntruderHelper.addIntruderHuman(context, 2, env.getLandmark("E"),
				env.getLandmark("A"));
		IntruderHelper.addSmallUGV(context, 2, env.getLandmark("E"),
				new Location(650, 720), new Location(450, 700),
				env.getLandmark("E"));
	}

	/**
	 * Generates intruders, as foraging animals specified by
	 * constSensorNetwork.Scenario_IntruderNodeCount
	 * 
	 * @param context
	 * @param
	 */
	public static void genintrForagingAnimals(SensorNetworkContext context) {
		int intruderNodes = context.getSimulationInput().getParameterInt(
				constSensorNetwork.Intruders_Number);
		for (int i = 1; i <= intruderNodes; i++) {
			IntruderHelper.addForagingAnimal(context, context.getRandom(), i);
		}
	}

	/**
	 * Generates a scenario when a number of human intruders perform randomly distributed
	 * crossings of the interest area
	 * 
	 * @param context
	 * @param crossings
	 *            how many crossings
	 * @param timeRange
	 *            the time range over which the start of the crossing is
	 *            distributed
	 * @param speed
	 *            the speed of the nodes
	 */
	public static void genintrRandomlyDistributedCrossings(
			SensorNetworkContext context, int crossings, int timeRange,
			double speed, Random random) {
		for (int i = 0; i != crossings; i++) {
			int startTime = (int) (random.nextDouble() * timeRange);
			IntruderHelper.createRandomCrossingPPM(context.getWorld(), i,
					context.environment.getInterestArea(), startTime, speed,
					random);
		}
	}


	/**
	 * Generates a scenario where the intruders are combing the full area 
	 * 
	 * FIXME: must have an adjustable speed
	 * 
	 * @param context
	 */
	public static void genintrLine(
			SensorNetworkContext context, int intruders, 
			double speed) {
		Rectangle2D.Double area = context.environment.getFullArea();
		double xstart = area.getMinX();
		double xend = area.getMaxX();
		double ystep = area.getHeight() / intruders;
		for(int i = 0 ; i != intruders; i++) {
			double y = area.getMinY() + ystep / 2 + i * ystep;
			Location locs[] = new Location[10];
			// going back and forth 5 times
			int j = 0;
			while(j < 10) {
				locs[j++] = new Location(xstart, y);
				locs[j++] = new Location(xend, y);
			}			
			IntruderHelper.addIntruderHuman(context, i, locs);
		}
	}


	/**
	 * Generates a scenario where the intruders are orbiting a location 
	 * 
	 * FIXME: must have an adjustable speed
	 * FIXME: are the intruders proceeding with the same angular speed???
	 * 
	 * @param context
	 */
	public static void genintrOrbit(
			SensorNetworkContext context, int intruders, Location center, 
			double distancerange, double speed) {
		double step = distancerange / intruders;
		// the number of steps into which the full circle is divided
		double div = 10; 
		for(int i = 0 ; i != intruders; i++) {
			// the distance where the orbiting happens
			double d = step / 2 + i * step;
			Location locs[] = new Location[10];
			for(int j = 0; j != 10; j++) {
				double angle = 2 * j * Math.PI / div;
				locs[j] = new Location(center.getX() + Math.sin(angle) * d, center.getY() + Math.cos(angle) * d);
			}
			IntruderHelper.addIntruderHuman(context, i, locs);
		}
	}

	
}
