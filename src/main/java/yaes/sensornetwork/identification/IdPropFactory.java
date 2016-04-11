package yaes.sensornetwork.identification;

import yaes.sensornetwork.identification.IdentificationProperties.IdPropType;

public class IdPropFactory {

	/**
	 * Creates the features for a non-friendly human intruder
	 * @return
	 */
	public static IdentificationProperties createNonFriendlyHumanIntruder() {
		IdentificationProperties ip = new IdentificationProperties();
		ip.identificationAsHuman = 1.0;
		ip.identificationAsFriendly = 0.0;
		ip.averageSpeed = 2;
		ip.metalicContent = 0.1; // gun
		ip.weight = 100;
		ip.type = IdPropType.PROPERTY;
		return ip;
	}

	/**
	 * Creates the properties for a friendly human intruder
	 * @return
	 */
	public static IdentificationProperties createFriendlyHumanIntruder() {
		IdentificationProperties ip = new IdentificationProperties();
		ip.identificationAsHuman = 1.0;
		ip.identificationAsFriendly = 0.0;
		ip.averageSpeed = 2;
		ip.metalicContent = 0.03; // some metalic objects
		ip.weight = 100;
		ip.type = IdPropType.PROPERTY;
		return ip;
	}


	/**
	 * Creates the properties for a small animal
	 * @return
	 */
	public static IdentificationProperties createSmallAnimal() {
		IdentificationProperties ip = new IdentificationProperties();
		ip.identificationAsHuman = 0.0;
		ip.identificationAsFriendly = 0.0;
		ip.averageSpeed = 2;
		ip.metalicContent = 0.0; // no metalic content in animals
		ip.weight = 10;
		ip.type = IdPropType.PROPERTY;
		return ip;
	}

	
	/**
	 * Creates the properties for a UGV
	 * @return
	 */
	public static IdentificationProperties createUGV() {
		IdentificationProperties ip = new IdentificationProperties();
		ip.identificationAsHuman = 1.0;
		ip.identificationAsFriendly = 0.0;
		ip.averageSpeed = 2;
		ip.metalicContent = 0.8; // gun
		ip.weight = 20;
		ip.type = IdPropType.PROPERTY;
		return ip;
	}

	
	/**
	 * Creates the properties for a UGV
	 * @return
	 */
	public static IdentificationProperties createVehicle() {
		IdentificationProperties ip = new IdentificationProperties();
		ip.identificationAsHuman = 1.0;
		ip.identificationAsFriendly = 0.0;
		ip.averageSpeed = 2;
		ip.metalicContent = 0.8; // gun
		ip.weight = 20;
		ip.type = IdPropType.PROPERTY;
		return ip;
	}

}
