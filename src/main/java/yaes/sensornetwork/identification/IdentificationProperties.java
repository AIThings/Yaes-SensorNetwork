package yaes.sensornetwork.identification;

import java.io.Serializable;

import yaes.ui.format.Formatter;

/**
 * The properties based on which the typical node will be identified
 * 
 * This is used both in the IntruderNode - to see its identification and in the
 * the observation
 * 
 * @author Lotzi Boloni
 * 
 */
public class IdentificationProperties implements Serializable {

	private static final long serialVersionUID = 7527862901189731992L;

	public enum IdPropType {
		PROPERTY, OBSERVATION
	};

	public IdPropType type = null;

	/**
	 * The weight in kgs - negative means no observation
	 */
	public double weight = -1;
	/**
	 * The metalic content in percentage - negative means no observation
	 */
	public double metalicContent = -1;
	/**
	 * The average speed - negative means no observation
	 */
	public double averageSpeed = -1;
	/**
	 * Confidence for identification as human [0..1]
	 */
	public double identificationAsHuman = -1;
	/**
	 * Confidence for identification as friendly human
	 */
	public double identificationAsFriendly = -1;

	@Override
	public String toString() {
		Formatter fmt = new Formatter();
		switch (type) {
		case PROPERTY: {
			fmt.add("Identification properties");
			fmt.indent();
			fmt.is("weight", weight);
			fmt.is("metalic content", metalicContent);
			fmt.is("averageSpeed", averageSpeed);
			fmt.is("human", identificationAsHuman);
			fmt.is("friendly", identificationAsFriendly);
			break;
		} 
		case OBSERVATION: {
			fmt.add("Observation properties:");
			fmt.indent();
			if (weight >= 0) {
				fmt.is("weight", weight);				
			}
			if (metalicContent >= 0) {
				fmt.is("metalic content", metalicContent);
			}
			if (averageSpeed >= 0) {
				fmt.is("speed (current)", averageSpeed);				
			}
			if (identificationAsHuman >= 0) {
				fmt.is("human", identificationAsHuman);
			}			
		}
			break;
		default:
			break;
		}
		
		return fmt.toString();
	}
}
