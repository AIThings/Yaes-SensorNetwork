package yaes.sensornetwork.identification;

import java.util.Random;

import yaes.sensornetwork.identification.IdentificationProperties.IdPropType;

/**
 * This class contains functions which, based on an id prop factory create new
 * versions, as seen by the sensors with limited capacity
 * 
 * 
 * @author Lotzi Boloni
 * 
 */
public class IdPropObservationFactory {

	public enum SensorType {
		VISUAL, WEIGHT, METAL_DETECTOR, LOCATION
	};

	/**
	 * Generates an empty sensor reading
	 * 
	 * @param ip
	 * @param r
	 * @param sensorType
	 * @param sensorObservation
	 * @param distance
	 * @param sensorRange
	 * @return
	 */
	public static IdentificationProperties getEmptySensorReading() {
		IdentificationProperties retval = new IdentificationProperties();
		retval.type = IdPropType.OBSERVATION;
		return retval;
	}

	/**
	 * Generates the errorless sensor reading, but only if the distance is
	 * closer than the sensor range
	 * 
	 * @param ip
	 * @param r
	 * @param sensorType
	 * @param sensorObservation
	 * @param distance
	 * @param sensorRange
	 * @return
	 */
	public static IdentificationProperties getSensorReading(
			IdentificationProperties ip, SensorType sensorType,
			double distance, double sensorRange) {
		if (distance > sensorRange) {
			return getEmptySensorReading();
		}
		IdentificationProperties retval = new IdentificationProperties();
		retval.type = IdPropType.OBSERVATION;
		switch (sensorType) {
		case VISUAL: {
			retval.identificationAsHuman = ip.identificationAsHuman;
			retval.identificationAsFriendly = ip.identificationAsFriendly;
			break;
		}
		case METAL_DETECTOR: {
			retval.metalicContent = ip.metalicContent;
			break;
		}
		case WEIGHT:
			retval.weight = ip.weight;
			break;
		case LOCATION:
		default:
			break;
		}
		return retval;
	}

	/**
	 * Generates a distorted sensor reading, specific to the sensor
	 * 
	 * @param ip
	 * @param r
	 * @param sensorType
	 * @param sensorObservation
	 * @param distance
	 * @param sensorRange
	 * @return
	 */
	public static IdentificationProperties getSensorReadingWithError(
			IdentificationProperties ip, Random r, SensorType sensorType,
			double distance, double sensorRange) {
		IdentificationProperties retval = new IdentificationProperties();
		retval.type = IdPropType.OBSERVATION;
		switch (sensorType) {
		case VISUAL: {
			// identifying as a human, within the sensor range is quite clear
			if (ip.identificationAsHuman == 1.0) {
				if (distance < sensorRange) {
					retval.identificationAsHuman = 1.0;
				} else {
					retval.identificationAsHuman = Math.max(0.5, 1.0 - distance
							/ sensorRange * 0.1);
				}
			} else { // can only be 0
				if (distance < sensorRange) {
					retval.identificationAsHuman = 0.0;
				} else {
					retval.identificationAsHuman = Math.min(0.5, distance
							/ sensorRange * 0.1);
				}
			}
			if (ip.identificationAsFriendly == 1.0) {
				if (distance < sensorRange) {
					retval.identificationAsFriendly = 0.8;
				} else {
					retval.identificationAsFriendly = Math.max(0.5, 1.0
							- distance / sensorRange * 0.2);
				}
			} else {
				if (distance < sensorRange) {
					retval.identificationAsHuman = 0.0;
				} else {
					retval.identificationAsHuman = Math.min(0.5, distance
							/ sensorRange * 0.1);
				}
			}
			break;
		}
		case METAL_DETECTOR: {
			retval.metalicContent = (1 + r.nextGaussian()) * ip.metalicContent;
			retval.metalicContent = Math.max(retval.metalicContent, 0);
			retval.metalicContent = Math.min(retval.metalicContent, 1.0);
			break;
		}
		case WEIGHT:
			retval.weight = (1 + r.nextGaussian()) * ip.weight;
			retval.weight = Math.max(retval.weight, 0);
			retval.weight = Math.min(retval.weight, 1.0);
			break;
		case LOCATION:
		default:
			break;
		}
		return retval;
	}

}
