package yaes.sensornetwork;

import java.awt.geom.Rectangle2D;

import yaes.world.physical.map.SimpleFreeGround;

public class EnvironmentGenerator {

	/**
	 * Generates an environment where we have a larger full area, in which we
	 * have a smaller interest area. The distribution area of the sensors is the
	 * same as the interest area.
	 * 
	 * @return
	 */
	public static Environment genenvSensorCoveredInterestArea() {
		Environment env = new Environment();
		env.setFullArea(new Rectangle2D.Double(0, 0, 1500, 1000));
		env.setInterestArea(new Rectangle2D.Double(200, 200, 1000, 500));
		env.setSensorDistributionArea(new Rectangle2D.Double(200, 200, 1000,
				500));
		return env;
	}

	/**
	 * An illustration of how the framework can model relatively complex
	 * systems.
	 * 
	 * Generates a description of the Research Park / Partnership II building
	 * area. It creates two specific protected areas, some landmarks and some
	 * paths connecting the landmarks.
	 * 
	 * @return
	 */
	public static Environment genenvPartnershipArea() {
		Environment env = new Environment();
		env.setFullArea(new Rectangle2D.Double(0, 0, 1500, 1000));
		env.setInterestArea(new Rectangle2D.Double(400, 200, 600, 600));
		env.setSensorDistributionArea(new Rectangle2D.Double(400, 200, 600, 600));
		env.addProtectedArea("Protected 1", new Rectangle2D.Double(450, 700,
				80, 50));
		env.addProtectedArea("Protected 2", new Rectangle2D.Double(650, 700,
				80, 50));

		// landmarks
		env.addLandmark("A", 0, 900);
		env.addLandmark("B", 1000, 900);
		env.addLandmark("C", 1200, 500);
		env.addLandmark("D", 1200, 0);
		env.addLandmark("E", 1500, 500);

		// create the paths
		env.addPath("AB", env.createPathFromLandmarks("A", "B"));
		env.addPath("BC", env.createPathFromLandmarks("B", "C"));
		// create the roads
		env.addRoad("CE", env.createPathFromLandmarks("C", "E"));
		env.addRoad("CD", env.createPathFromLandmarks("C", "D"));
		// create the protected areas
		// the map
		env.setTheMap(new SimpleFreeGround(
				"src/yaes/sensornetwork/visualization/maps/Partnership-Area.png"));
		return env;
	}

}
