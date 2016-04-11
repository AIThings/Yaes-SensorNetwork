/*
 * Created on Aug 6, 2007
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package yaes.sensornetwork.model.stealth;

import java.io.Serializable;

/**
 * This class represents a simple model of the stealth level of the agent
 */
public class SimpleStealthModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2801141472369500900L;
	private double stealthLevel;
	private double p_deploy; // deployment disclosure probability
	private double p_ad; // accidental disclosure probability
	private double lastTime;

	public SimpleStealthModel(double p_deploy, double p_ad) {
		this.p_deploy = p_deploy;
		this.p_ad = p_ad;
		stealthLevel = 1.0 - p_deploy;
		lastTime = 0;
	}

	/**
	 * Copy constructor
	 * 
	 * @param model
	 */
	public SimpleStealthModel(SimpleStealthModel model) {
		this.p_deploy = model.p_deploy;
		this.p_ad = model.p_ad;
		this.stealthLevel = model.stealthLevel;
		this.lastTime = model.lastTime;
	}

	/**
	 * @return the stealthLevel
	 */
	public double getStealthLevel() {
		return stealthLevel;
	}

	/**
	 * Updates the stealth level, based on the accidental disclosure probability
	 * 
	 * @param time
	 */
	public void update(double time) {
		double mult = Math.pow(1 - p_ad, time - lastTime);
		stealthLevel = stealthLevel * mult;
		lastTime = time;
	}

	public void updateEvent(IDisclosureEvent event) {
		stealthLevel = stealthLevel * (1 - event.getDisclosureProbability());
	}

}
