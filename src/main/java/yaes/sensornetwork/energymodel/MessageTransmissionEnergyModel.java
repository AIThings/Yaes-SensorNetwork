/*
   This file is part of the Yet Another Extensible Simulator
   Created on: Oct 2, 2009
 
   yaes.world.sensornetwork.energymodel.MessageTransmissionEnergyModel
 
   Copyright (c) 2008-2009 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.energymodel;

import java.io.Serializable;

import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.agents.AbstractSensorAgent;
import yaes.sensornetwork.model.SensorNode;

/**
 * 
 * <code>yaes.world.sensornetwork.energymodel.MessageTransmissionEnergyModel</code>
 * 
 * Contains support for the measurement of the transmission energy
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class MessageTransmissionEnergyModel implements Serializable {
	/**
     * 
     */
	private static final long serialVersionUID = -1421084402930142819L;

	private AbstractSensorAgent agent;

	private RapaportCommunicationEnergyModel communicationEnergyModel = null;
	// if it is true, the transmissions are done at the exact distance
	private boolean transmissionEnergyExactDistance = false;

	/**
	 * if it is -1, calculate it from the size of the node
	 */
	private int transmissionEnergyFixedMessageSize = -1;

	/**
	 * Fixed overhead for the transmission energy calculation. If this value is
	 * 10 and the message length is 10, ---> we have 10.
	 */
	private int transmissionEnergyFixedOverhead = 0;

	private double transmissionRange = 0;

	public MessageTransmissionEnergyModel(AbstractSensorAgent agent) {
		this.agent = agent;
	}

	/**
	 * @return the communicationEnergyModel
	 */
	public RapaportCommunicationEnergyModel getCommunicationEnergyModel() {
		return communicationEnergyModel;
	}

	/**
	 * @return the transmissionEnergyFixedMessageSize
	 */
	public int getTransmissionEnergyFixedMessageSize() {
		return transmissionEnergyFixedMessageSize;
	}

	/**
	 * @return the transmissionEnergyFixedOverhead
	 */
	public int getTransmissionEnergyFixedOverhead() {
		return transmissionEnergyFixedOverhead;
	}

	/**
	 * @return the transmissionRange
	 */
	public double getTransmissionRange() {
		return transmissionRange;
	}

	/**
	 * @return the transmissionEnergyExactDistance
	 */
	public boolean isTransmissionEnergyExactDistance() {
		return transmissionEnergyExactDistance;
	}

	/**
	 * @param communicationEnergyModel
	 *            the communicationEnergyModel to set
	 */
	public void setCommunicationEnergyModel(
			RapaportCommunicationEnergyModel communicationEnergyModel) {
		this.communicationEnergyModel = communicationEnergyModel;
	}

	/**
	 * @param transmissionEnergyExactDistance
	 *            the transmissionEnergyExactDistance to set
	 */
	public void setTransmissionEnergyExactDistance(
			boolean transmissionEnergyExactDistance) {
		this.transmissionEnergyExactDistance = transmissionEnergyExactDistance;
	}

	/**
	 * @param transmissionEnergyFixedMessageSize
	 *            the transmissionEnergyFixedMessageSize to set
	 */
	public void setTransmissionEnergyFixedMessageSize(
			int transmissionEnergyFixedMessageSize) {
		this.transmissionEnergyFixedMessageSize = transmissionEnergyFixedMessageSize;
	}

	/**
	 * @param transmissionEnergyFixedOverhead
	 *            the transmissionEnergyFixedOverhead to set
	 */
	public void setTransmissionEnergyFixedOverhead(
			int transmissionEnergyFixedOverhead) {
		this.transmissionEnergyFixedOverhead = transmissionEnergyFixedOverhead;
	}

	/**
	 * @param transmissionRange
	 *            the transmissionRange to set
	 */
	public void setTransmissionRange(double transmissionRange) {
		this.transmissionRange = transmissionRange;
	}

	/**
	 * @param message
	 */
	public double transmissionEnergyCost(ACLMessage message) {
		// incur the energy cost
		if (communicationEnergyModel == null) {
			return 0;
		}
		double distance;
		int messageLength;
		if (transmissionEnergyExactDistance) {
			// throw new Error("Transmitting at exact distance not supported");
			String destination = message.getDestination();
			// broadcast is at full transmission range
			if (destination.equals("*")) {
				distance = transmissionRange;
			} else {
				SensorNode dest = agent.getSensorWorld()
						.lookupSensorNodeByName(message.getDestination());
				if (dest == null) {
					distance = transmissionRange;
				} else {
					distance = dest.getLocation().distanceTo(
							agent.getNode().getLocation());
				}
			}
		} else {
			distance = transmissionRange;
		}
		if (transmissionEnergyFixedMessageSize == -1) {
			messageLength = transmissionEnergyFixedOverhead
					+ message.toString().length();
		} else {
			messageLength = transmissionEnergyFixedMessageSize;
		}
		double energyCost = communicationEnergyModel.powerTx(distance,
				messageLength);
		return energyCost;
	}

}
