/**
 * 
 */
package yaes.sensornetwork.applications.intrudertracking;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import yaes.sensornetwork.SensorNetworkContext;
import yaes.sensornetwork.identification.IdPropFactory;
import yaes.sensornetwork.identification.IntruderNodeType;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.stealth.StealthySensorNetworkWorld;
import yaes.world.physical.location.Location;
import yaes.world.physical.path.PPMGenerator;
import yaes.world.physical.path.PathGenerator;
import yaes.world.physical.path.PlannedPath;
import yaes.world.physical.path.ProgrammedPathMovement;

/**
 * This class contains a series of utility functions which help us to generate 
 * frequently encountered intruder types.
 * 
 * @author lboloni
 * 
 */
public class IntruderHelper {

	static class EdgeLocationAndGarage {
		int edge;
		Location garage;
		Location location;
	}

	/**
	 * Adds a foraging animal moving in a random waypoint movement in the full area 
	 * of the environment until the stop time.
	 * 
	 * @param context
	 * @param random - the source of the random generator
	 * @param id - the identifier of the animal 
	 * 
	 */
	public static void addForagingAnimal(SensorNetworkContext context,
			Random random, int id) {
		StealthySensorNetworkWorld world = context.getWorld();
		double speed = 10.0;
		double speedStdDev = 5;
		double waitTime = 5;
		double waitTimeStdDev = 10.0;
		ProgrammedPathMovement ppm = PPMGenerator.randomWaypoint(
				context.environment.getFullArea(), context.getSimulationInput()
						.getStopTime(), speed, speedStdDev, waitTime,
				waitTimeStdDev, random);
		IntruderNode mobileNode = new IntruderNode("Animal-"
				+ String.format("%03d", id), ppm, world);
		mobileNode.setIntruderNodeType(IntruderNodeType.ANIMAL);
		world.addIntruderNode(mobileNode);
		world.addObject(mobileNode);
	}

	/**
	 * Adds a friendly human, moving in a random waypoint movement in the full area 
	 * of the environment until the stop time.
	 * 
	 * @param context
	 * @param random
	 * @param id
	 */
	public static void addFriendlyHuman(SensorNetworkContext context,
			Random random, int id) {
		StealthySensorNetworkWorld world = context.getWorld();
		double speed = 10.0;
		double speedStdDev = 5;
		double waitTime = 5;
		double waitTimeStdDev = 10.0;
		ProgrammedPathMovement ppm = PPMGenerator.randomWaypoint(
				context.environment.getFullArea(), context.getSimulationInput()
						.getStopTime(), speed, speedStdDev, waitTime,
				waitTimeStdDev, random);
		IntruderNode mobileNode = new IntruderNode("Human-"
				+ String.format("%03d", id), ppm, world);
		mobileNode.setIntruderNodeType(IntruderNodeType.FRIENDLY_HUMAN);
		world.addIntruderNode(mobileNode);
		world.addObject(mobileNode);
	}

	/**
	 * Adds an intruder human, moving in a random waypoint movement in the full area 
	 * of the environment until the stop time.
	 * 
	 * @param context
	 * @param random
	 * @param id
	 */
	public static void addIntruderHuman(SensorNetworkContext context,
			Random random, int id) {
		StealthySensorNetworkWorld world = context.getWorld();
		double speed = 10.0;
		double speedStdDev = 5;
		double waitTime = 5;
		double waitTimeStdDev = 10.0;
		ProgrammedPathMovement ppm = PPMGenerator.randomWaypoint(
				context.environment.getFullArea(), context.getSimulationInput()
						.getStopTime(), speed, speedStdDev, waitTime,
				waitTimeStdDev, random);
		IntruderNode mobileNode = new IntruderNode("Animal-"
				+ String.format("%03d", id), ppm, world);
		mobileNode.setIntruderNodeType(IntruderNodeType.INTRUDER_HUMAN);
		world.addIntruderNode(mobileNode);
		world.addObject(mobileNode);
	}

	/**
	 * Adds a friendly human moving with a speed of 10 m/s on a path specified by 
	 * the specific locations
	 * 
	 * @param context
	 * @param id
	 * @param location
	 */
	public static void addFriendlyHuman(SensorNetworkContext context, int id,
			Location... locations) {
		StealthySensorNetworkWorld world = context.getWorld();
		double speed = 10.0;
		double speedStdDev = 0.5;
		ProgrammedPathMovement ppm = PPMGenerator.followPathWithConstantSpeed(
				speed, speedStdDev, context.getRandom(), locations);
		IntruderNode mobileNode = new IntruderNode("Friendly-"
				+ String.format("%03d", id), ppm, world);
		mobileNode.setIntruderNodeType(IntruderNodeType.FRIENDLY_HUMAN);
		world.addIntruderNode(mobileNode);
		world.addObject(mobileNode);
	}

	/**
	 * Adds a friendly human moving with a speed of 10 m/s on a path specified by 
	 * the specific locations
	 * 
	 * @param context
	 * @param id
	 * @param points
	 */
	public static void addIntruderHuman(SensorNetworkContext context, int id,
			Location... locations) {
		StealthySensorNetworkWorld world = context.getWorld();
		double speed = 10.0;
		double speedStdDev = 0.5;
		ProgrammedPathMovement ppm = PPMGenerator.followPathWithConstantSpeed(
				speed, speedStdDev, context.getRandom(), locations);
		IntruderNode mobileNode = new IntruderNode("Intruder-"
				+ String.format("%03d", id), ppm, world);
		mobileNode.setIntruderNodeType(IntruderNodeType.INTRUDER_HUMAN);
		world.addIntruderNode(mobileNode);
		world.addObject(mobileNode);
	}

	/**
	 * Adds a small UGV intruder
	 * 
	 * @param context
	 * @param id
	 * @param points
	 */
	public static void addSmallUGV(SensorNetworkContext context, int id,
			Location... locations) {
		StealthySensorNetworkWorld world = context.getWorld();
		double speed = 10.0;
		double speedStdDev = 0.5;
		ProgrammedPathMovement ppm = PPMGenerator.followPathWithConstantSpeed(
				speed, speedStdDev, context.getRandom(), locations);
		IntruderNode mobileNode = new IntruderNode("Intruder-"
				+ String.format("%03d", id), ppm, world);
		mobileNode.setIntruderNodeType(IntruderNodeType.SMALL_UGV);
		world.addIntruderNode(mobileNode);
		world.addObject(mobileNode);
	}

	/**
	 * Adds an intruder human which performs a random crossing of the 
	 * specific area, starting at a specific time.
	 * 
	 * @param 
	 * @param startTime
	 * @param speed
	 */
	public static void addHumanIntruderRandomCrossing(SensorNetworkContext context, int id,
			Rectangle2D.Double rect, double startTime, double speed,
			Random random) {
		StealthySensorNetworkWorld world = context.getWorld();
		// picks a start location on the rectangle
		EdgeLocationAndGarage start = IntruderHelper
				.pickRandomLocationOnRectangleEdge(rect, -1, startTime / speed,
						random);
		EdgeLocationAndGarage stop = IntruderHelper
				.pickRandomLocationOnRectangleEdge(rect, start.edge, 1000,
						random);

		ArrayList<Location> locations = new ArrayList<>();
		locations.add(start.garage);
		locations.add(start.location);
		locations.add(stop.location);
		locations.add(stop.garage);
		PlannedPath path = PathGenerator.createPathFromNodes(locations);
		IntruderNode mobileNode = new IntruderNode("Intruder-"
				+ String.format("%03d", id), path, speed, world,
				IntruderNodeType.INTRUDER_HUMAN,
				IdPropFactory.createNonFriendlyHumanIntruder());
		world.addIntruderNode(mobileNode);
		world.addObject(mobileNode);
	}

	/**
	 * Creates a node which crosses a certain rectangle.
	 * 
	 * @param rect
	 * @param startTime
	 * @param speed
	 */
	public static void createRandomCrossingPPM(SensorNetworkWorld world,
			int id, Rectangle2D.Double rect, double startTime, double speed,
			Random random) {
		// picks a start location on the rectangle
		double distance = 100;
		EdgeLocationAndGarage start = IntruderHelper
				.pickRandomLocationOnRectangleEdge(rect, -1, distance, random);
		EdgeLocationAndGarage stop = IntruderHelper
				.pickRandomLocationOnRectangleEdge(rect, start.edge, distance,
						random);
		ArrayList<Location> locations = new ArrayList<>();
		locations.add(start.garage);
		locations.add(start.location);
		locations.add(stop.location);
		locations.add(stop.garage);
		PlannedPath path = PathGenerator.createPathFromNodes(locations);

		ProgrammedPathMovement ppm = new ProgrammedPathMovement();
		ppm.addSetLocation(start.garage);
		ppm.addWaitUntil(startTime);
		ppm.addFollowPath(path, speed);

		IntruderNode mobileNode = new IntruderNode("Intruder-"
				+ String.format("%03d", id), ppm, world);
		world.addIntruderNode(mobileNode);
		world.addObject(mobileNode);
	}

	/**
	 * Picks a random location on one of the edges of the rectangle, where the
	 * edge is not the specified edge and also adds a garage
	 * 
	 * @param rect
	 * @param excludeEdge
	 *            an edge to be excluded from consideration
	 * @param dist
	 *            the distance of the garage from the location
	 * @return
	 */
	public static EdgeLocationAndGarage pickRandomLocationOnRectangleEdge(
			Rectangle2D.Double rect, int excludeEdge, double dist, Random random) {
		int edge;
		while (true) {
			edge = random.nextInt(4);
			if (edge != excludeEdge) {
				break;
			}
		}
		double x = 0;
		double y = 0;
		double gx = 0;
		double gy = 0;
		switch (edge) {
		case 0: {
			x = rect.x + random.nextDouble() * rect.width;
			y = rect.y;
			gx = x;
			gy = y - dist;
			break;
		}
		case 1: {
			x = rect.x + random.nextDouble() * rect.width;
			y = rect.y + rect.height;
			gx = x;
			gy = y + dist;
			break;
		}
		case 2: {
			x = rect.x;
			y = rect.y + random.nextDouble() * rect.height;
			gx = x - dist;
			gy = y;
			break;
		}
		case 3: {
			x = rect.x + rect.width;
			y = rect.y + random.nextDouble() * rect.height;
			gx = x + dist;
			gy = y;
			break;
		}
		default:
			break;
		}
		EdgeLocationAndGarage retval = new IntruderHelper.EdgeLocationAndGarage();
		retval.edge = edge;
		retval.location = new Location(x, y);
		retval.garage = new Location(gx, gy);
		return retval;
	}

}
