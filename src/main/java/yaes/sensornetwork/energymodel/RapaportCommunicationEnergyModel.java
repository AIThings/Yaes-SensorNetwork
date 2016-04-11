package yaes.sensornetwork.energymodel;

import java.io.Serializable;

/**
 * This class implements a model of power consumption
 * 
 * T. Rappaport. {\em Wireless Communications: Principles \& Practice}. New
 * Jersey: Prentice-Hall, Inc., 1996.
 */
public class RapaportCommunicationEnergyModel implements Serializable {
	public enum PowerConsumptionScenario {
		EXPERIMENTAL, HIGH_PATH_LOSS, LOW_PATH_LOSS
	}

	public static double ALPHA_1_1 = 45e-9; // Joule
											// /
											// bit;

	public static double ALPHA_1_2 = 135e-9; // Joule
												// /
												// bit
	public static double ALPHA_2_SC1 = 10e-12; // Joule
												// /
												// m^2
												// with
												// p.l.i
												// =
												// 2
	public static double ALPHA_2_SC2 = 0.001e-12; // Joule
													// /
													// m^4
													// with
													// p.l.i
													// =
	// 4
	public static double ALPHA_3 = 50e-9; // Joule
											// /
											// bit
	// constants for typical values
	public static double PATH_LOSS_INDEX_SC1 = 2;
	public static double PATH_LOSS_INDEX_SC2 = 4;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5218755524770186170L;
	private double alpha_1_1 = 0;
	private double alpha_1_2 = 0;
	private double alpha_2 = 0;
	private double path_loss_index = 0;

	/**
	 * initialize for scenario 1
	 * 
	 */
	public RapaportCommunicationEnergyModel(PowerConsumptionScenario pcs) {
		//pcs = PowerConsumptionScenario.LOW_PATH_LOSS;
		switch (pcs) {
		case LOW_PATH_LOSS: {
			path_loss_index = RapaportCommunicationEnergyModel.PATH_LOSS_INDEX_SC1;
			alpha_1_1 = RapaportCommunicationEnergyModel.ALPHA_1_1;
			alpha_1_2 = RapaportCommunicationEnergyModel.ALPHA_1_2;
			alpha_2 = RapaportCommunicationEnergyModel.ALPHA_2_SC1;
			break;
		}
		case HIGH_PATH_LOSS: {
			path_loss_index = RapaportCommunicationEnergyModel.PATH_LOSS_INDEX_SC2;
			alpha_1_1 = RapaportCommunicationEnergyModel.ALPHA_1_1;
			alpha_1_2 = RapaportCommunicationEnergyModel.ALPHA_1_2;
			alpha_2 = RapaportCommunicationEnergyModel.ALPHA_2_SC2;
			break;
		}
		case EXPERIMENTAL: {
			path_loss_index = RapaportCommunicationEnergyModel.PATH_LOSS_INDEX_SC2;
			alpha_1_1 = 0;
			alpha_1_2 = 0;
			alpha_2 = RapaportCommunicationEnergyModel.ALPHA_2_SC1;
		}
			break;
		default:
			break;
		}
	}

	/**
	 * Returns the is packet size when the transmitted data is b. FIXME: works
	 * only for a single packet.
	 * 
	 * http://sd.wareonearth.com/~phil/net/overhead/
	 * 
	 * @param b
	 * @return
	 */
	public int getEthernetPacketSize(int kb) {
		return 1024 * kb + 26;
	}

	/**
	 * Returns the power necessary to receive b bytes
	 * 
	 * @param b
	 * @return
	 */
	public double powerRx(int b) {
		return alpha_1_2 * getEthernetPacketSize(b);
	}

	/**
	 * Returns the power necessary to send b bytes to the distance d (meters).
	 * The input is kilobytes. The result is returned in joules.
	 * 
	 * @param d
	 * @param b
	 * @return
	 */
	public double powerTx(double d, int kb) {
		return (alpha_1_1 + alpha_2 * Math.pow(d, path_loss_index))
				* getEthernetPacketSize(kb);
	}
}
