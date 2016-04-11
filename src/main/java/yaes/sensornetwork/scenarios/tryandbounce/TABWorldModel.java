/**
 * 
 */
package yaes.sensornetwork.scenarios.tryandbounce;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingWorldModel;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.ui.format.Formatter;

/**
 * 
 * Extends the sensor network model with model
 * 
 * @author lboloni
 * 
 */
public class TABWorldModel extends IntruderTrackingWorldModel implements Serializable{

	private static final long serialVersionUID = 3182536150539110466L;
	private String myself;
	private List<NodeModel> nodeModels = new ArrayList<>();
	private List<TABReportModel> reportModels = new ArrayList<>();

	public TABWorldModel(String myself, SensorNetworkWorld snw) {
	    super(ItwmType.LAST_KNOWN, snw);
		this.myself = myself;
	}

	/**
	 * @return the myself
	 */
	public String getMyself() {
		return myself;
	}

	/**
	 * Returns the model for a specific node
	 * 
	 * @param name
	 * @return
	 */
	public NodeModel getNodeModel(String name) {
		for (NodeModel nodeModel : nodeModels) {
			if (nodeModel.getName().equals(name)) {
				return nodeModel;
			}
		}
		return null;
	}

	/**
	 * @return the nodeModels
	 */
	public List<NodeModel> getNodeModels() {
		return nodeModels;
	}

	/**
	 * @return the reportModels
	 */
	public List<TABReportModel> getReportModels() {
		return reportModels;
	}

	/**
	 * Returns all the reports for a given threat
	 * 
	 * @param threatName
	 * @return
	 */
	public List<TABReportModel> getReportsForInterest(String interest) {
		List<TABReportModel> retval = new ArrayList<>();
		for (TABReportModel reportModel : reportModels) {
			if (reportModel.getInterestName().equals(interest)) {
				retval.add(reportModel);
			}
		}
		return retval;
	}

	/**
	 * Returns all the reports for a given threat
	 * 
	 * @param threatName
	 * @return
	 */
	public List<TABReportModel> getReportsForThreat(String threatName) {
		List<TABReportModel> retval = new ArrayList<>();
		for (TABReportModel reportModel : reportModels) {
			if (reportModel.getIntruderNode().equals(threatName)) {
				retval.add(reportModel);
			}
		}
		return retval;
	}

	/**
	 * Nicely formatted version of the local world model
	 */
	
	@Override
	public String toString() {
		Formatter fmt = new Formatter();
		fmt.add("Local world model of node:" + myself);
		fmt.add("Nodes:");
		fmt.indent();
		for (NodeModel nodeModel : nodeModels) {
			fmt.add(nodeModel.toString());
		}
		fmt.deindent();
		fmt.add("Intruders:");
		fmt.indent();
		for (String intruder : getIntruders()) {
			fmt.add(intruder + " -- " + estimateIntruderLocation(intruder));
		}
		fmt.deindent();
		fmt.add("Reports:");
		fmt.indent();
		for (TABReportModel reportModel : reportModels) {
			fmt.add(reportModel.toString());
		}
		fmt.deindent();
		return fmt.toString();
	}

}
