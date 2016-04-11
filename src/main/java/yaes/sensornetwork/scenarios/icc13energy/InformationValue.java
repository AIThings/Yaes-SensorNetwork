package yaes.sensornetwork.scenarios.icc13energy;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import yaes.sensornetwork.applications.intrudertracking.IntruderNode;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.SinkNode;
import yaes.ui.format.Formatter;
import yaes.world.physical.location.Location;

/**
 * Calculates the information value according to the one described in
 * ICC13-EnergyValue / InformationValue.tex
 * 
 * @author Lotzi Boloni
 * 
 */
public class InformationValue {

    /**
     * The item
     * 
     * @author Lotzi Boloni
     * 
     */
    class InformationValueItem {
        public double infoValue = 0.0;
        public int intruders = 0;
        public int intrudersInArea = 0;
        public double maxInfoValue = 0.0;
        double time;
    }

    public static final double E_ACC = 10.0;

    public static final double MAX_VALUE_PER_TIME = 10.0;

    /**
     * The information value information collection
     */
    private List<InformationValueItem> infoValue = new ArrayList<>();
    /**
     * The interest rectangle considered
     */
    public Rectangle2D.Double interestRectangle;
    /**
     * The sum of the information value
     */
    private double sumInfoValue = 0;
    /**
     * The sum of the realizable information value
     */
    private double sumMaxInfoValue = 0;

    /**
     * @return the sumInfoValue
     */
    public double getSumInfoValue() {
        return sumInfoValue;
    }

    /**
     * @return the sumMaxInfoValue
     */
    public double getSumMaxInfoValue() {
        return sumMaxInfoValue;
    }

    @Override
    public String toString() {
        Formatter fmt = new Formatter();
        fmt.add("Information Value");
        fmt.indent();
        fmt.is("sumInfoValue", sumInfoValue);
        fmt.is("sumMaxInfoValue", sumMaxInfoValue);
        if (!infoValue.isEmpty()) {
            fmt.add("last item");
            InformationValueItem item = infoValue.get(infoValue.size() - 1);
            fmt.indent();
            fmt.is("intruders", item.intruders);
            fmt.is("intrudersInArea", item.intrudersInArea);
            fmt.is("maxInfoValue", item.maxInfoValue);
            fmt.is("infoValue", item.infoValue);
        }
        return fmt.toString();
    }

    /**
     * Updates the information value at the current time
     * 
     * @param time
     */
    public void update(IveContext context) {
        Rectangle2D.Double interestRectangle =
                context.environment.getInterestArea();
        SensorNetworkWorld snw = context.getWorld();
        SinkNode sinkNode = snw.getSinkNode();
        IveSinkAgent sinkAgent = (IveSinkAgent) sinkNode.getAgent();
        IveWorldModel sinkModel = sinkAgent.getWorldModel();
        InformationValueItem ivi = new InformationValueItem();
        infoValue.add(ivi);
        ivi.time = snw.getTime();
        List<String> trackedNodes = sinkModel.getIntruders();
        // for all the intruders
        for (IntruderNode node : snw.getIntruderNodes()) {
            ivi.intruders++;
            Location realLocation = node.getLocation();
            if (interestRectangle.contains(realLocation.asPoint())) {
                ivi.maxInfoValue = InformationValue.MAX_VALUE_PER_TIME;
                ivi.intrudersInArea++;
            }
            Location estimatedLocation = null;
            if (trackedNodes.contains(node.getName())) {
                estimatedLocation =
                        sinkModel.estimateIntruderLocation(node.getName());
            }
            ivi.infoValue =
                    calculateInfoValue(realLocation, estimatedLocation,
                            interestRectangle);
            sumMaxInfoValue += ivi.maxInfoValue;
            sumInfoValue += ivi.infoValue;
        }
    }

    /**
     * Calculation of the intruder
     * 
     * @return
     */
    public static double calculateInfoValue(Location realLocation,
            Location estimatedLocation, Rectangle2D.Double interestRectangle) {
        if (!interestRectangle.contains(realLocation.asPoint())) {
            return 0.0;
        }
        if (estimatedLocation == null) {
            return 0.0;
        }
        double distance = estimatedLocation.distanceTo(realLocation);
        // TextUi.println("Node: " + node.getName() + " distance error: "
        // + Formatter.fmt(distance));
        double ratio =
                1
                        - (distance * distance - InformationValue.E_ACC
                                * InformationValue.E_ACC)
                        / (interestRectangle.getHeight() * interestRectangle
                                .getWidth());
        ratio = Math.min(ratio, 1.0);
        ratio = Math.max(0, ratio);
        double scaledratio = Math.pow(ratio,30);
        double retval = scaledratio * InformationValue.MAX_VALUE_PER_TIME;
        return retval;
    }

}
