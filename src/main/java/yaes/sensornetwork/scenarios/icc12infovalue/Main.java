package yaes.sensornetwork.scenarios.icc12infovalue;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import yaes.sensornetwork.identification.IdPropFactory;
import yaes.sensornetwork.identification.IdPropObservationFactory;
import yaes.sensornetwork.identification.IdPropObservationFactory.SensorType;
import yaes.sensornetwork.identification.IdentificationProperties;
import yaes.sensornetwork.identification.IntruderNodeType;
import yaes.sensornetwork.knowledge.AccuracyMetric;
import yaes.sensornetwork.knowledge.ILocationEstimator;
import yaes.sensornetwork.knowledge.IntruderSighting;
import yaes.sensornetwork.knowledge.IntruderSightingHistory;
import yaes.sensornetwork.knowledge.SimpleEstimator;
import yaes.sensornetwork.knowledge.UncertainMovementSegment;
import yaes.ui.format.Formatter;
import yaes.ui.plot.AbstractGraphDescription;
import yaes.ui.plot.MatlabUtil;
import yaes.ui.plot.OctaveUtil;
import yaes.ui.text.TextUi;
import yaes.ui.text.TextUiHelper;
import yaes.util.FileWritingUtil;
import yaes.world.physical.location.IMoving;
import yaes.world.physical.location.Location;
import yaes.world.physical.map.ArrangementHelper;
import yaes.world.physical.path.PathGenerator;
import yaes.world.physical.path.PlannedPath;
import yaes.world.physical.path.ProgrammedPathMovement;

public class Main {

	public static List<IccSensor> sensors = new ArrayList<>();
	public static List<IccIntruder> intruders = new ArrayList<>();
	public static ILocationEstimator estimator = new SimpleEstimator();
	public static Map<String, Double> lastReport = new HashMap<>();
	// maps the name of the intruder to its beliefs
	public static Map<String, Map<IntruderNodeType, Double>> belief = new HashMap<>();
	// maps the name of the intruder to its plausibilities
	public static Map<String, Map<IntruderNodeType, Double>> plausibility = new HashMap<>();
	public static List<String> intercepting = new ArrayList<String>();

	public enum SimulationCase {
		Unlimited, Uniform, Pragmatic, Cautious
	};

	public static List<Double> integrateBy(List<Double> source, int num) {
		List<Double> retval = new ArrayList<Double>();
		for (int i = 0; i < source.size(); i = i + num) {
			double collect = Double.MAX_VALUE;
			for (int j = 0; j != num; j++) {
				// collect = collect + source.get(i + j);
				collect = Math.min(collect, source.get(i + j));
			}
			// collect = collect / num;
			retval.add(collect);
		}
		return retval;
	}

	/**
	 * The main funtion
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static final void main(String args[]) throws IOException {
		TextUi.println("run with plenty of resources");
		List<Double> fullResource = runSimulation(1000, false, false,
				SimulationCase.Unlimited);
		double messageSec = 0.3;
		TextUi.println("run with 5 / sec");
		List<Double> withLimit = runSimulation(messageSec, false, false,
				SimulationCase.Uniform);
		TextUi.println("run with 5 / sec + pragmatic");
		List<Double> withPragmatic = runSimulation(messageSec, true, false,
				SimulationCase.Pragmatic);
		TextUi.println("run with 5 / sec + pragmatic + error");
		List<Double> withPragmaticErrorCorrect = runSimulation(messageSec,
				true, true, SimulationCase.Cautious);
		// print it out
		for (int i = 0; i != fullResource.size(); i++) {
			String toPrint = TextUiHelper.padTo(i, 10)
					+ TextUiHelper
							.padTo(Formatter.fmt(fullResource.get(i)), 20)
					+ TextUiHelper.padTo(Formatter.fmt(withLimit.get(i)), 20)
					+ TextUiHelper.padTo(Formatter.fmt(withPragmatic.get(i)),
							20)
					+ TextUiHelper
							.padTo(Formatter.fmt(withPragmaticErrorCorrect
									.get(i)), 20);
			TextUi.println(toPrint);
		}
		int integration = 10;
		List<Double> unlimitedIntegrated = integrateBy(fullResource,
				integration);
		List<Double> uniformIntegrated = integrateBy(withLimit, integration);
		List<Double> pragmaticIntegrated = integrateBy(withPragmatic,
				integration);
		List<Double> cautiousIntegrated = integrateBy(
				withPragmaticErrorCorrect, integration);

		StringBuffer buffer = new StringBuffer();
		buffer.append(MatlabUtil.getMatlabVector("UNLIMITED",
				unlimitedIntegrated) + ";\n");
		buffer.append(MatlabUtil.getMatlabVector("UNIFORM", uniformIntegrated)
				+ ";\n");
		buffer.append(MatlabUtil.getMatlabVector("PRAGMATIC",
				pragmaticIntegrated) + ";\n");
		buffer.append(MatlabUtil
				.getMatlabVector("CAUTIOUS", cautiousIntegrated) + ";\n");
		buffer.append(OctaveUtil.generateBasicGraphPrefix(100, 100));
		// buffer.append("plot(UNLIMITED, '1', 'DisplayName', 'UNLIMITED')\n");
		// buffer.append("plot(UNIFORM, '2', 'DisplayName', 'UNIFORM')\n");
		// buffer.append("plot(PRAGMATIC, '3', 'DisplayName', 'PRAGMATIC')\n");
		// buffer.append("plot(CAUTIOUS, '4', 'DisplayName', 'CAUTIOUS')\n");
		buffer.append("plot(UNLIMITED"
				+ AbstractGraphDescription.getLineStyle(0)
				+ ", 'DisplayName', 'UNLIMITED')\n");
		buffer.append("plot(UNIFORM"
				+ AbstractGraphDescription.getLineStyle(1)
				+ ", 'DisplayName', 'UNIFORM')\n");
		buffer.append("plot(PRAGMATIC"
				+ AbstractGraphDescription.getLineStyle(2)
				+ ", 'DisplayName', 'PRAGMATIC')\n");
		buffer.append("plot(CAUTIOUS"
				+ AbstractGraphDescription.getLineStyle(3)
				+ ", 'DisplayName', 'CAUTIOUS')\n");
		buffer.append("legend('UNLIMITED', 'UNIFORM', 'PRAGMATIC', 'CAUTIOUS');\n");
		String temp = buffer.toString();
		FileWritingUtil.writeToTextFile(new File("ICCExperiment.m"), temp);

	}

	/**
	 * The main function of the simulation scenario
	 * 
	 * @return
	 */
	public static List<Double> runSimulation(double resourcePerSec,
			boolean pragmatic, boolean expectErrors,
			SimulationCase simulationCase) {
		Random random = new Random();
		List<Double> retval = new ArrayList<Double>();
		double timeStart = 0;
		double timeEnd = 600;
		initializeSensors();
		initializeIntruders();
		initializeBeliefs();
		IntruderSightingHistory ish = new IntruderSightingHistory();
		// initialize the identification with the worst
		Map<IccIntruder, IntruderNodeType> identify = new HashMap<IccIntruder, IntruderNodeType>();
		for (IccIntruder intr : intruders) {
			identify.put(intr, IntruderNodeType.INTRUDER_HUMAN);
		}
		double resource = 0;
		for (double time = timeStart; time < timeEnd; time = time + 1.0) {
			TextUi.println("time " + time);
			List<IntruderSighting> sightings = new ArrayList<IntruderSighting>();
			// simulate the sensing and reporting
			for (IccIntruder intr : intruders) {
				Location l = intr.ppmt.getLocation(time);
				for (IccSensor sensor : sensors) {
					double distance = l.distanceTo(sensor.location);
					if (distance <= sensor.sensorRange) {
						// simulate the reporting error due to distance
						double xreported = l.getX() + 0.1 * distance
								* random.nextGaussian();
						double yreported = l.getY() + 0.1 * distance
								* random.nextGaussian();
						Location lreported = new Location(xreported, yreported);
						IntruderSighting sight = new IntruderSighting(
								sensor.name, time, intr.name, lreported);
						IdentificationProperties ip = IdPropObservationFactory
								.getSensorReading(intr.ip, sensor.sensorType,
										l.distanceTo(sensor.location),
										sensor.sensorRange);
						sight.setIdentificationProperties(ip);
						sightings.add(sight);
					}
				}
			}
			// filter the sightings with the limited resources
			resource = resource + resourcePerSec;
			int resourceLimit = (int) Math.floor(resource);
			resource = resource - resourceLimit;
			List<IntruderSighting> filteredSightings;
			if (pragmatic) {
				filteredSightings = filterPragmatic(time, sightings,
						resourceLimit, true, expectErrors);
			} else {
				// filteredSightings = filterByResource(time, sightings,
				// resourceLimit);
				filteredSightings = filterPragmatic(time, sightings,
						resourceLimit, false, expectErrors);
			}
			for (IntruderSighting is : filteredSightings) {
				ish.addSighting(is, time);
			}

			updateTheIdentification(time, belief, plausibility, ish,
					simulationCase);
			// calculate the value of information
			double value = valueOfInformation(time, ish, intercepting);
			retval.add(value);
		}
		return retval;
	}

	/**
	 * Initialize the beliefs to the hard ones
	 */
	private static void initializeBeliefs() {
		intercepting.clear();
		for (IccIntruder intr : intruders) {
			Map<IntruderNodeType, Double> beliefvalue = new HashMap<IntruderNodeType, Double>();
			for (IntruderNodeType type : IntruderNodeType.values()) {
				if (intr.type == type) {
					beliefvalue.put(type, 1.0);
				} else {
					beliefvalue.put(type, 0.0);
				}
			}
			belief.put(intr.name, beliefvalue);
			Map<IntruderNodeType, Double> plausibilityvalue = new HashMap<IntruderNodeType, Double>();
			for (IntruderNodeType type : IntruderNodeType.values()) {
				if (intr.type == type) {
					plausibilityvalue.put(type, 1.0);
				} else {
					plausibilityvalue.put(type, 0.01);
				}
			}
			plausibility.put(intr.name, plausibilityvalue);
		}
	}

	/**
	 * This function computes how much is the current value of the information
	 * in the intruder sighting history.
	 * 
	 * It is based on the belief and possibility functions - this is a bit of a
	 * mess because the way this is supposed to be
	 * 
	 * 
	 * @param time
	 * @param identify
	 * @param ish
	 * @return
	 */
	private static double valueOfInformation(double time,
			IntruderSightingHistory ish, List<String> intercepting) {
		double retval = 0;
		for (IccIntruder intr : intruders) {
			switch (intr.type) {
			case ANIMAL:
				retval += 0;
				break;
			case VEHICLE:
				// retval += 0.01 * valueTracking(ish, intr, time);
				retval += 0.1 * valueKeepAround(ish, intr, time);
				retval += 0.2 * valueHistoricalPathReconstruction(ish, intr,
						time);
				break;
			case FRIENDLY_HUMAN:
				retval += 0;
				break;
			case INTRUDER_HUMAN:
				if (intercepting.contains(intr.name)) {
					retval += 3 * valueTracking(ish, intr, time);
					retval += 0.2 * valueHistoricalPathReconstruction(ish,
							intr, time);
				} else {
					retval += 1 * valueKeepAround(ish, intr, time);
					retval += 0.2 * valueHistoricalPathReconstruction(ish,
							intr, time);
					TextUi.println("Intruder human" + retval);
				}
				break;
			case SMALL_UGV:
				if (intercepting.contains(intr.name)) {
					retval += 3 * valueTracking(ish, intr, time);
					retval += 0.2 * valueHistoricalPathReconstruction(ish,
							intr, time);
				} else {
					retval += 1 * valueKeepAround(ish, intr, time);
					retval += 0.2 * valueHistoricalPathReconstruction(ish,
							intr, time);
				}
				break;
			default:
				break;
			}
		}
		TextUi.println("Value of information " + Formatter.fmt(retval));
		return retval;
	}

	/**
	 * Returns the value for the function
	 * 
	 * @param x
	 * @param limit
	 * @return
	 */
	public static double boundValueFunction(double x, double limit, double decay) {
		if (x <= limit) {
			return 1.0;
		}
		return Math.pow(decay, (x - limit) / limit);
	}

	/**
	 * The value for keeping around (low level tracking)
	 * 
	 * FIXME: adjustment, in-area etc, limit etc
	 * 
	 * @param ish
	 * @param intr
	 * @param time
	 * @return
	 */
	public static double valueKeepAround(IntruderSightingHistory ish,
			IccIntruder intr, double time) {
		Location groundTruth = intr.ppmt.getLocation(time);
		Location estimated = estimator
				.getIntruderLocation(ish, intr.name, time);
		if (estimated == null) {
			return 0;
		}
		double dist = estimated.distanceTo(groundTruth);
		return boundValueFunction(dist, 5, 0.8);
	}

	/**
	 * The value for real time tracking
	 * 
	 * FIXME: adjustment, in-area etc, limit etc
	 * 
	 * @param ish
	 * @param intr
	 * @param time
	 * @return
	 */
	public static double valueTracking(IntruderSightingHistory ish,
			IccIntruder intr, double time) {
		Location groundTruth = intr.ppmt.getLocation(time);
		Location estimated = estimator
				.getIntruderLocation(ish, intr.name, time);
		if (estimated == null) {
			return 0;
		}
		double dist = estimated.distanceTo(groundTruth);
		return boundValueFunction(dist, 5, 0.5);
	}

	/**
	 * 
	 * The value for historical path reconstruction:
	 * 
	 * FIXME: implement me
	 * 
	 * @param ish
	 * @param intr
	 * @param time
	 * @return
	 */
	public static double valueHistoricalPathReconstruction(
			IntruderSightingHistory ish, IccIntruder intr, double time) {
		double maxSpeed = 30;
		SimpleEntry<List<UncertainMovementSegment>, Double> uncertaintyAreas = AccuracyMetric
				.uncertaintyArea(0, time, intr.name, maxSpeed, estimator, ish);
		double area = uncertaintyAreas.getValue();
		return boundValueFunction(area, 25, 0.5);
	}

	/**
	 * Updates the identification ... we can keep this hard wired for the time
	 * being
	 * 
	 * @param time
	 * @param identify
	 * @param ish
	 */
	private static void updateTheIdentification(double time,
			Map<String, Map<IntruderNodeType, Double>> belief,
			Map<String, Map<IntruderNodeType, Double>> plausibility,
			IntruderSightingHistory ish, SimulationCase simulationCase) {
		if (time < 200.0) {
			switch (simulationCase) {
			case Cautious:
			case Pragmatic:
			case Uniform:
			case Unlimited: {
				Map<IntruderNodeType, Double> b = belief.get("Intruder-1");
				Map<IntruderNodeType, Double> p = plausibility
						.get("Intruder-1");
				// set the belief to be friendly but the plausibility to be 0.5
				for (IntruderNodeType type : IntruderNodeType.values()) {
					switch (type) {
					case ANIMAL:
					case SMALL_UGV:
					case VEHICLE:
						b.put(type, 0.0);
						p.put(type, 0.0);
						break;
					case FRIENDLY_HUMAN:
						b.put(type, 1.0);
						p.put(type, 1.0);
						break;
					case INTRUDER_HUMAN:
						b.put(type, 0.0);
						p.put(type, 0.5);
						break;
					default:
						break;
					}
				}
			}
				break;
			default:
				break;
			}
		}
		// at time 90, the all of them except the pragmatic wake up
		if (time >= 200.0 && time < 360) {
			switch (simulationCase) {
			case Pragmatic:
				break;
			case Cautious:
			case Uniform:
			case Unlimited: {
				Map<IntruderNodeType, Double> b = belief.get("Intruder-1");
				Map<IntruderNodeType, Double> p = plausibility
						.get("Intruder-1");
				// set the belief to be friendly but the plausibility to be 0.5
				for (IntruderNodeType type : IntruderNodeType.values()) {
					switch (type) {
					case ANIMAL:
					case SMALL_UGV:
					case VEHICLE:
						b.put(type, 0.0);
						p.put(type, 0.0);
						break;
					case FRIENDLY_HUMAN:
						b.put(type, 0.0);
						p.put(type, 0.0);
						break;
					case INTRUDER_HUMAN:
						b.put(type, 1.0);
						p.put(type, 1.0);
						break;
					default:
						break;
					}
				}
				break;
			}
			default:
				break;
			}
		}
		// at time 120, the pragmatic wakes up as well that Intruder-1 is
		// intruder
		if (time >= 360.0) {
			switch (simulationCase) {
			case Cautious:
			case Uniform:
			case Unlimited:
				break;
			case Pragmatic: {
				Map<IntruderNodeType, Double> b = belief.get("Intruder-1");
				Map<IntruderNodeType, Double> p = plausibility
						.get("Intruder-1");
				// set the belief to be friendly but the plausibility to be 0.5
				for (IntruderNodeType type : IntruderNodeType.values()) {
					switch (type) {
					case ANIMAL:
					case SMALL_UGV:
					case VEHICLE:
						b.put(type, 0.0);
						p.put(type, 0.0);
						break;
					case FRIENDLY_HUMAN:
						b.put(type, 0.0);
						p.put(type, 0.0);
						break;
					case INTRUDER_HUMAN:
						b.put(type, 1.0);
						p.put(type, 1.0);
						break;
					default:
						break;
					}
				}
				break;
			}
			default:
				break;
			}
		}
		if (time >= 480.0) {
			intercepting.add("Intruder-1");
		}
	}

	private static void initializeIntruders() {
		Rectangle2D.Double area = new Rectangle2D.Double(-100, -100, 1200, 1200);
		Random random = new Random(100);
		intruders.clear();
		lastReport.clear();
		// the human intruder
		Location OUT1 = new Location(-50, 300);
		Location OUT2 = new Location(-50, 300);
		Location A = new Location(0, 0);
		Location B = new Location(400, 500);
		Location C = new Location(200, 800);
		Location D = new Location(1000, 1000);
		Location E = new Location(1000, 300);
		// the first human intruder
		ProgrammedPathMovement ppm = new ProgrammedPathMovement();
		ppm.addSetLocation(OUT1);
		ppm.addWaitFor(30);
		ppm.addSetLocation(A);
		PlannedPath pp = PathGenerator.createPathFromNodes(OUT1, A, B, C, D, A);
		ppm.addFollowPath(pp, 1.6);
		IdentificationProperties ip = IdPropFactory
				.createNonFriendlyHumanIntruder();
		IccIntruder intr = new IccIntruder("Intruder-1",
				IntruderNodeType.INTRUDER_HUMAN, ip, ppm);
		intruders.add(intr);
		// the second human intruder
		ppm = new ProgrammedPathMovement();
		ppm.addSetLocation(OUT2);
		ppm.addWaitFor(60);
		pp = PathGenerator.createPathFromNodes(OUT2, E, A, B, C, D, A);
		ppm.addFollowPath(pp, 1.6);
		ip = IdPropFactory.createNonFriendlyHumanIntruder();
		intr = new IccIntruder("Intruder-2", IntruderNodeType.VEHICLE,
				ip, ppm);
		// intruders.add(intr);
		// 20 animals
		for (int i = 0; i != 20; i++) {
			ppm = new ProgrammedPathMovement();
			pp = PathGenerator.createRandomWaypointPathByLength(random, area,
					20 * 300);
			ppm.addSetLocation(pp.getSource());
			ppm.addFollowPath(pp, 1);
			ip = IdPropFactory.createSmallAnimal();
			intr = new IccIntruder("Animal" + i, IntruderNodeType.ANIMAL, ip,
					ppm);
			intruders.add(intr);
		}
	}

	/**
	 * Initialize the sensors
	 */
	private static void initializeSensors() {
		sensors.clear();
		Random r = new Random(1);
		// 400 location sensors
		List<IMoving> temp = new ArrayList<IMoving>();
		for (int i = 0; i != 400; i++) {
			IccSensor is = new IccSensor();
			is.name = "SensorLocation-" + i;
			is.sensorRange = 50;
			is.sensorType = SensorType.LOCATION;
			temp.add(is);
			sensors.add(is);
		}
		ArrangementHelper.arrangeInAGridWithNoise(0, 0, 1000, 1000, temp, r,
				0.2);
		// 25 weight sensors
		temp = new ArrayList<IMoving>();
		for (int i = 0; i != 400; i++) {
			IccSensor is = new IccSensor();
			is.name = "SensorWeight-" + i;
			is.sensorRange = 50;
			is.sensorType = SensorType.WEIGHT;
			temp.add(is);
			sensors.add(is);
		}
		ArrangementHelper.arrangeInAGridWithNoise(0, 0, 1000, 1000, temp, r,
				0.2);
		// 25 metal detector sensors
		temp = new ArrayList<IMoving>();
		for (int i = 0; i != 400; i++) {
			IccSensor is = new IccSensor();
			is.name = "SensorMetal-" + i;
			is.sensorRange = 50;
			is.sensorType = SensorType.METAL_DETECTOR;
			temp.add(is);
			sensors.add(is);
		}
		ArrangementHelper.arrangeInAGridWithNoise(0, 0, 1000, 1000, temp, r,
				0.2);
		// 25 visual sensors
		temp = new ArrayList<IMoving>();
		for (int i = 0; i != 400; i++) {
			IccSensor is = new IccSensor();
			is.name = "SensorVideo-" + i;
			is.sensorRange = 50;
			is.sensorType = SensorType.VISUAL;
			temp.add(is);
			sensors.add(is);
		}
		ArrangementHelper.arrangeInAGridWithNoise(0, 0, 1000, 1000, temp, r,
				0.2);
	}

	/**
	 * Gets when was the last report (or -1 if never). Useful for the filtering.
	 * 
	 * @param sighting
	 * @return
	 */
	public static double getLastReport(IntruderSighting sighting) {
		Double d = lastReport.get(sighting.getNameIntruder());
		if (d == null) {
			return -1;
		}
		return d.doubleValue();
	}

	/**
	 * Filters by resource - to at most count observations
	 * 
	 * @param original
	 * @param count
	 * @return
	 */
	public static List<IntruderSighting> filterByResource(double time,
			List<IntruderSighting> original, int count) {
		List<IntruderSighting> list = new ArrayList<IntruderSighting>();
		list.addAll(original);
		List<IntruderSighting> retval = new ArrayList<IntruderSighting>();
		while (!list.isEmpty() && retval.size() < count) {
			// choose the next one
			IntruderSighting candidate = null;
			double delay = -1;
			for (IntruderSighting sighting : list) {
				double del = time - getLastReport(sighting);
				if (del > delay) {
					candidate = sighting;
					delay = del;
				}
			}
			// add the candidate
			if (candidate != null) {
				list.remove(candidate);
				retval.add(candidate);
				lastReport.put(candidate.getNameIntruder(),
						candidate.getTimeSighting());
			} else {
				break;
			}
		}
		return retval;
	}

	/**
	 * Filters pragmatically - this is where the identification trick will come
	 * into play - weight the values of the nodes with their lateness,
	 * 
	 * @param original
	 * @param count
	 * @param errorCorrection
	 *            - if is is like that, also add the plausibilities
	 * @return
	 */
	public static List<IntruderSighting> filterPragmatic(double time,
			List<IntruderSighting> original, int count, boolean scaling,
			boolean errorCorrection) {
		List<SimpleEntry<IntruderSighting, Double>> scores = new ArrayList<>();
		for (IntruderSighting is : original) {
			double del = time - getLastReport(is);
			String intruderName = is.getNameIntruder();
			Map<IntruderNodeType, Double> bel = belief
					.get(intruderName);
			Map<IntruderNodeType, Double> pla = plausibility.get(intruderName);
			double scale = 0.1;
			scale = scale + 1.0 * bel.get(IntruderNodeType.INTRUDER_HUMAN)
					+ 1.0 * bel.get(IntruderNodeType.SMALL_UGV) + 1.0
					* bel.get(IntruderNodeType.VEHICLE) + 0.2
					* bel.get(IntruderNodeType.FRIENDLY_HUMAN);
			if (errorCorrection) {
				scale = scale + pla.get(IntruderNodeType.INTRUDER_HUMAN)
						+ pla.get(IntruderNodeType.SMALL_UGV)
						+ pla.get(IntruderNodeType.VEHICLE) + 0.2
						* pla.get(IntruderNodeType.FRIENDLY_HUMAN);
			}
			if (intercepting.contains(intruderName)) {
				scale = 2 * scale;
			}
			if (scaling) {
				del = del * scale;
			}
			scores.add(new SimpleEntry<IntruderSighting, Double>(is, del));
		}
		Collections.sort(scores,
				new Comparator<SimpleEntry<IntruderSighting, Double>>() {
					@Override
					public int compare(
							SimpleEntry<IntruderSighting, Double> o1,
							SimpleEntry<IntruderSighting, Double> o2) {
						return Double.compare(o1.getValue(), o2.getValue());
					}
				});
		Collections.reverse(scores);
		List<IntruderSighting> retval = new ArrayList<IntruderSighting>();
		for (int i = 0; i < count && i < scores.size(); i++) {
			IntruderSighting is = scores.get(i).getKey();
			lastReport.put(is.getNameIntruder(), is.getTimeSighting());
			retval.add(is);
		}
		return retval;
	}

}
