/**
 * 
 */
package yaes.sensornetwork.applications.intrudertracking;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yaes.ui.format.Formatter;
import yaes.ui.text.TextUiHelper;
import yaes.world.World;
import yaes.world.physical.location.Location;

/**
 * 
 * The world model of an intruder tracking sensor network
 * 
 * @author lboloni
 * 
 */
public class IntruderTrackingWorldModel implements Serializable {
    private static final long serialVersionUID = 8593762784265861081L;

    /**
     * LATEST: the latest location is the current INERTIAL: assumes that the
     * intruder performs an inertial movement
     * 
     */
    public enum ItwmType {
        LAST_KNOWN, INERTIAL
    };

    private ItwmType type;
    /**
     * The current world - essentially, providing time
     */
    private World world;
    private Map<String, IntruderModel> intruderModels = new HashMap<>();

    
    /**
     * Returns the name of all the intruder nodes currently tracked by this 
     * knowledgebase
     * 
     * @return
     */
    public List<String> getIntruders() {
        List<String> retval = new ArrayList<>();
        retval.addAll(intruderModels.keySet());
        return retval;
    }
    
    
    /**
     * Creates a certain intruder tracking world model
     * 
     * @param type
     */
    public IntruderTrackingWorldModel(ItwmType type, World world) {
        this.type = type;
        this.world = world;
    }

    /**
     * Considers a report about the location of an intruder. Passes it down to
     * the intruder model.  
     * 
     * Returns true if there was a change
     * 
     * @param time
     * @param name
     * @param location
     */
    public boolean addIntruderAtLocation(double time, String name, Location location) {
        IntruderModel model = intruderModels.get(name);
        if (model == null) {
            model = new IntruderModel(name, type);
            intruderModels.put(name, model);
        }
        return model.addIntruderAtLocation(time, location);
    }

    /**
     * The toString function, lists the components of the model
     */
    @Override
    public String toString() {
        Formatter fmt = new Formatter();
        fmt.add("IntruderTrackingWorldModel: ");
        fmt.addIndented(toStringIntruderModels());
        return fmt.toString();
    }

    /**
     * Extracted here the intruder models - this function might be called from
     * the other functions under it
     * 
     * @return
     */
    protected String toStringIntruderModels() {
        Formatter fmt = new Formatter();
        fmt.add("Intruders currently tracked: " + intruderModels.keySet().size());
        fmt.indent();
        for (String name : intruderModels.keySet()) {
            IntruderModel target = intruderModels.get(name);
            fmt.add(TextUiHelper.padTo(name, 10) + " at "
                    + target.toString());
        }
        return fmt.toString();
    }

    /**
     * Estimate the location of the specified intruder at the current moment.
     * Returns null if it doesn't know anything about the given intruder
     * 
     * @param name
     * @return
     */
    public Location estimateIntruderLocation(String name) {
        IntruderModel im = intruderModels.get(name);
        if (im == null) {
            return null;
        }
        return im.getLocationEstimate(world.getTime());
    }
}
