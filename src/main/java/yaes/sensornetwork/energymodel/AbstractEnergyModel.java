package yaes.sensornetwork.energymodel;

import yaes.world.physical.location.INamedMoving;

public abstract class AbstractEnergyModel implements IEnergyModel {

	private static final double MAX_ENERGY = 100.0;
	private static final long serialVersionUID = -6414570986973050133L;
	protected double currentEnergy;
	protected double lastTime;
	protected INamedMoving theNode;

	public AbstractEnergyModel(INamedMoving theNode) {
		this.theNode = theNode;
		lastTime = 0.0;
		currentEnergy = AbstractEnergyModel.MAX_ENERGY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see yaes.world.network.node.energymodel.IEnergyModel#getEnergy()
	 */
	@Override
	public double getEnergy() {
		return currentEnergy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see yaes.world.network.node.energymodel.IEnergyModel#getMaximumEnergy ()
	 */
	@Override
	public double getMaximumEnergy() {
		return AbstractEnergyModel.MAX_ENERGY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see yaes.world.network.node.energymodel.IEnergyModel#getNode()
	 */
	@Override
	public INamedMoving getNode() {
		return theNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see networkmodel.nodes.energymodel.IBatteryOperated#setEnergy(double,
	 * double)
	 */
	@Override
	public void setEnergy(double energy, double time) {
		this.currentEnergy = energy;
		this.lastTime = time;
	}
}
