/*
 * Created on Sep 22, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package yaes.sensornetwork.energymodel;

import java.io.Serializable;

import yaes.world.physical.location.INamedMoving;

/**
 * @author lboloni
 * 
 */
public interface IEnergyModel extends Serializable {
	// gets the current value of the energy
	double getEnergy();

	// the maximum energy at full load
	double getMaximumEnergy();

	INamedMoving getNode();

	// sets the energy value at the time time
	void setEnergy(double energy, double time);

	// updates the energy value with the power assumed since the last update
	double updateEnergy(double time);
}
