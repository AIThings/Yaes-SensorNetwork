package sensornetwork.applications.intrudertracking;

import org.junit.Test;

import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingWorldModel;
import yaes.sensornetwork.applications.intrudertracking.IntruderTrackingWorldModel.ItwmType;
import yaes.ui.format.Formatter;
import yaes.ui.text.TextUi;
import yaes.world.World;
import yaes.world.physical.location.Location;

public class testIntruderTrackingWorldModel {

    /**
     * Tests the ITWM in the last position estimate world model
     */
    @Test
    public void testLastPosition() {
        World world = new World(null);
        IntruderTrackingWorldModel itwm =
                new IntruderTrackingWorldModel(ItwmType.LAST_KNOWN, world);
        itwm.addIntruderAtLocation(10.0, "intr", new Location(30, 30));
        itwm.addIntruderAtLocation(20.0, "intr", new Location(40, 40));
        world.setTime(30.0);
        Location loc = itwm.estimateIntruderLocation("intr");
        TextUi.println("Location at time=" + Formatter.fmt(world.getTime())
                + " is " + loc);
    }

    
    /**
     * Tests the ITWM in the inertial estimate
     */
    @Test
    public void testInertial() {
        World world = new World(null);
        IntruderTrackingWorldModel itwm =
                new IntruderTrackingWorldModel(ItwmType.INERTIAL, world);
        itwm.addIntruderAtLocation(10.0, "intr", new Location(30, 30));
        itwm.addIntruderAtLocation(20.0, "intr", new Location(40, 40));
        world.setTime(30.0);
        Location loc = itwm.estimateIntruderLocation("intr");
        TextUi.println("Location at time=" + Formatter.fmt(world.getTime())
                + " is " + loc);
    }

    
    /**
     * Tests whether the change model works for 
     */
    @Test
    public void testChangeModel() {
        Formatter fmt = new Formatter();
        World world = new World(null);
        IntruderTrackingWorldModel itwm =
                new IntruderTrackingWorldModel(ItwmType.INERTIAL, world);
        boolean changed;        
        changed = itwm.addIntruderAtLocation(10.0, "intr", new Location(30, 30));
        fmt.is("changed", changed);
        changed = itwm.addIntruderAtLocation(20.0, "intr", new Location(40, 40));
        fmt.is("changed", changed);
        changed = itwm.addIntruderAtLocation(30.0, "intr", new Location(30, 50));
        fmt.is("changed", changed);
        changed = itwm.addIntruderAtLocation(10.0, "intr", new Location(30, 50));
        fmt.is("changed", changed);        
        world.setTime(50.0);       
        Location loc = itwm.estimateIntruderLocation("intr");
        fmt.add("Location at time=" + Formatter.fmt(world.getTime())
                + " is " + loc);
        TextUi.println(fmt);
    }

    
}
