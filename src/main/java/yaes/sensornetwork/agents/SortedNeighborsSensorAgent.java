/*
   This file is part of the Yet Another Extensible Simulator
   Created on: Oct 12, 2009
 
   stealthrouting.agents.ISortedNeighbors
 
   Copyright (c) 2008-2009 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.agents;

import java.util.ArrayList;
import java.util.List;

import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.ui.format.Formatter;

/**
 * 
 * <code>stealthrouting.agents.ISortedNeighbors</code> An agent which has a
 * routing method where there are neighbors in a certain preference order
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public abstract class SortedNeighborsSensorAgent extends AbstractSensorAgent {

    private static final long serialVersionUID = 7501928154118935463L;
	private List<String> neighbors = new ArrayList<>();
	private List<String> hopsToSink = new ArrayList<>();

	public SortedNeighborsSensorAgent(String name,
			SensorNetworkWorld sensingManager) {
		super(name, sensingManager);
	}

	/**
	 * @return the hopsToSink
	 */
	public List<String> getHopsToSink() {
		return hopsToSink;
	}

	/**
	 * @return the neighbors
	 */
	public List<String> getNeighbors() {
		return neighbors;
	}


    /**
     * Prints the paths to the sink
     * @return
     */
    protected String toStringPaths() {
        Formatter fmt = new Formatter();
        fmt.add("Paths to sink:");
        fmt.indent();
        for (String hop : getHopsToSink()) {
            fmt.add(hop);
        }
        return fmt.toString();
    }
}
