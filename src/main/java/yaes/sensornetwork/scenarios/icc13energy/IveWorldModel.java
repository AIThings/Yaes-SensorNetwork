package yaes.sensornetwork.scenarios.icc13energy;

import java.util.ArrayList;
import java.util.List;

import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingWorldModel;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.ui.format.Formatter;

/**
 * The model of the world maintained by every IVE agent. 
 * 
 * Current version: keeps track of the intruders, and keeps track of the reports
 * 
 * @author Lotzi Boloni
 *
 */
public class IveWorldModel extends IntruderTrackingWorldModel {
	private static final long serialVersionUID = -3554094843347011999L;
	private String myself;
	private List<IveReportModel> reportModels = new ArrayList<>();

	/**
	 * @return the reportModels
	 */
	public List<IveReportModel> getReportModels() {
		return reportModels;
	}

	/**
	 * Creates an IVE world model (INERTIAL or LAST_LOCATION)
	 * @param type
	 * @param myself
	 * @param snw
	 */
	public IveWorldModel(ItwmType type, String myself, SensorNetworkWorld snw) {
	    super(type, snw);
		this.myself = myself;
	}

	/**
	 * @return the myself
	 */
	public String getMyself() {
		return myself;
	}
	
	
    @Override
    public String toString() {
        Formatter fmt = new Formatter();
        fmt.add("IveWorldModel: ");
        fmt.addIndented(toStringIntruderModels());
        fmt.addIndented(toStringReportModels());
        return fmt.toString();
    }
    
    /**
     * Lists the report models maintained by the node 
     * @return
     */
    protected String toStringReportModels() {
        Formatter fmt = new Formatter();
        fmt.add("Reports");
        fmt.indent();
        for(IveReportModel irm: reportModels) {
            fmt.add(irm.toString());
        }        
        return fmt.toString();
    }
	
}
