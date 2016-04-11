package yaes.sensornetwork.scenarios.icc12infovalue;

import yaes.sensornetwork.identification.IdPropObservationFactory.SensorType;
import yaes.world.physical.location.IMoving;
import yaes.world.physical.location.Location;

/**
 * The definition of the sensor for the purpose of 
 * this one
 * 
 * @author Lotzi Boloni
 *
 */
public class IccSensor implements IMoving {
	public SensorType sensorType;
	public Location location;
	public double sensorRange;
	public String name;
	
	@Override
	public Location getLocation() {
		return location;
	}
	@Override
	public void setLocation(Location location) {
		this.location = location;
	}
}
