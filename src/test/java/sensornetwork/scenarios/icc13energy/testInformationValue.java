package sensornetwork.scenarios.icc13energy;

import java.awt.geom.Rectangle2D;

import org.junit.Test;

import yaes.sensornetwork.scenarios.icc13energy.InformationValue;
import yaes.ui.format.Formatter;
import yaes.ui.text.TextUi;
import yaes.world.physical.location.Location;

public class testInformationValue {

    /**
     * Tests whether the info value formula gives good values
     */
    @Test
    public void testInfoValueFormula() {
        Formatter fmt = new Formatter();
        fmt.add("Info value:");
        fmt.indent();
        Location realLocation = new Location(500, 500);
        Rectangle2D.Double interestArea =
                new Rectangle2D.Double(300, 300, 800, 300);
        for (double x = 300; x < 800; x = x + 10) {
            Location estimatedLocation = new Location(x, 500);
            double value =
                    InformationValue.calculateInfoValue(realLocation,
                            estimatedLocation, interestArea);
            fmt.is(estimatedLocation.toString(), value);
        }
        TextUi.println(fmt);

    }

}
