/*
   This file is part of the Yet Another Extensible Simulator
   Created on: Oct 1, 2009
 
   yaes.world.sensornetwork.SensorRoutingHelper
 
   Copyright (c) 2008-2009 Ladislau Boloni

   This package is licensed under the LGPL version 2.
 */
package yaes.sensornetwork.agents;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import weka.core.matrix.Matrix;
import yaes.framework.agent.ACLMessage;
import yaes.sensornetwork.constSensorNetwork;
import yaes.sensornetwork.model.SensorNetworkWorld;
import yaes.sensornetwork.model.SensorNode;
import yaes.ui.text.TextUi;
import yaes.world.physical.location.Location;

/**
 * 
 * <code>yaes.world.sensornetwork.SensorRoutingHelper</code>
 * 
 * Functions for shortest paths in a sensor network.
 * 
 * @author Ladislau Boloni (lboloni@eecs.ucf.edu)
 */
public class SensorRoutingHelper implements constSensorNetwork {

	static class DistanceEdge {
		public double distance = 1.0;
	}

	/**
	 * Extracts from the sensor world the list of sensor agents which are: (a)
	 * enabled and (b) of a certain class
	 * 
	 * @param world
	 * @param agentClass
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<AbstractSensorAgent> getSensorAgents(
			SensorNetworkWorld world, Class agentClass) {
		// List<ForwarderSensorAgent> sourceAgents = new
		// ArrayList<ForwarderSensorAgent>();
		List<AbstractSensorAgent> abstractAgents = new ArrayList<>();
		for (SensorNode sensorNode : world.getSensorNodes()) {
			if (!sensorNode.isEnabled()) {
				continue;
			}
			AbstractSensorAgent asa = sensorNode.getAgent();
			if (agentClass.isAssignableFrom(asa.getClass())) {
				// sourceAgents.add((ForwarderSensorAgent) asa);
				abstractAgents.add(asa);
			}
		}
		return abstractAgents;
	}

	/**
	 * 
	 * Creates the next hop forwarding destination in a world composed of only
	 * ForwarderSensorAgents and a single sink.
	 * 
	 * @param sinkAgent
	 * @param sensorAgents
	 */
	public static void createPathsForForwarderSensorAgents(
			AbstractSensorAgent sinkAgent, SensorNetworkWorld world) {
		List<AbstractSensorAgent> allAgents = SensorRoutingHelper
				.getSensorAgents(world, ForwarderSensorAgent.class);
		List<AbstractSensorAgent> sourceAgents = new ArrayList<>(allAgents);
		allAgents.add(sinkAgent);

		for (AbstractSensorAgent source : sourceAgents) {
			List<String> path = SensorRoutingHelper.getShortestPath(allAgents,
					source, sinkAgent);
			if (path.size() < 2) {
				TextUi.println("Path finding failed for " + source.getName()
						+ " to " + sinkAgent.getName());
				TextUi.println("Returned path was: " + path);
				TextUi.println("This most likely means that the graph is not connected.");
				System.exit(1);
			}
			((ForwarderSensorAgent) source).setForwardingDestination(path
					.get(1));
		}
	}

	/**
	 * This method creates the forwarding paths from the current node to the
	 * destination node based on geographical greedy based algorithm for
	 * shortest path
	 * 
	 * @param sinkAgent
	 * @param world
	 */
	public static void createGreedyPathsForForwarderSensorAgents(
			AbstractSensorAgent sinkAgent, SensorNetworkWorld world) {
		List<AbstractSensorAgent> allAgents = SensorRoutingHelper
				.getSensorAgents(world, ForwarderSensorAgent.class);
		List<AbstractSensorAgent> sourceAgents = new ArrayList<>(allAgents);
		allAgents.add(sinkAgent);

		for (AbstractSensorAgent source : sourceAgents) {
			List<String> path = SensorRoutingHelper.getGreedyNeighborPaths(
					allAgents, source, sinkAgent);
			if (path.size() < 2) {
				TextUi.println("Path finding failed for " + source.getName()
						+ " to " + sinkAgent.getName());
				TextUi.println("Returned path was: " + path);
				TextUi.println("This most likely means that the graph is not connected.");
				System.exit(1);
			}
			((ForwarderSensorAgent) source).setForwardingDestination(path
					.get(1));
		}
	}

	/**
	 * 
	 * Takes a sink agent and a list of SortedNeighborsAgents, and creates the
	 * paths for them
	 * 
	 * @param sinkAgent
	 * @param sensorAgents
	 */
	public static void createPathsForSortedNeighbors(
			AbstractSensorAgent sinkAgent, SensorNetworkWorld world) {
		List<AbstractSensorAgent> allAgents = SensorRoutingHelper
				.getSensorAgents(world, SortedNeighborsSensorAgent.class);
		List<AbstractSensorAgent> sourceAgents = new ArrayList<>(allAgents);
		allAgents.add(sinkAgent);
		for (AbstractSensorAgent source : sourceAgents) {
			List<String> path = SensorRoutingHelper.getShortestPath(allAgents,
					source, sinkAgent);
			((SortedNeighborsSensorAgent) source).getHopsToSink().add(
					path.get(1));
			for (AbstractSensorAgent dest : sourceAgents) {
				if (source.equals(dest)) {
					continue;
				}
				if (isConnected(source, dest)) {
					((SortedNeighborsSensorAgent) source).getNeighbors().add(
							dest.getName());
				}
			}
		}
	}

	/**
	 * Utility function: determines the connectivity between two nodes
	 * 
	 * @param source
	 * @param destination
	 * @return
	 */
	public static boolean isConnected(AbstractSensorAgent source,
			AbstractSensorAgent destination) {
		boolean retval = source.getTransmissionRangeShape().contains(
				destination.getNode().getLocation().asPoint());
		return retval;
	}

	/**
	 * Returns a list of shortest paths from a source to a list of destinations
	 * in the network built from the agents - connectivity is based on the
	 * transmission range. It does the Dijkstra call once, and then it returns
	 * them all
	 */
	public static List<List<String>> getShortestPaths(
			List<AbstractSensorAgent> agents, AbstractSensorAgent source,
			List<AbstractSensorAgent> destinations) {
		Graph<AbstractSensorAgent, DistanceEdge> graph = new DirectedSparseGraph<>();
		for (AbstractSensorAgent sensorAgent : agents) {
			graph.addVertex(sensorAgent);
		}
		for (AbstractSensorAgent agent1 : agents) {
			for (AbstractSensorAgent agent2 : agents) {
				if (agent1.equals(agent2)) {
					continue;
				}
				if (isConnected(agent1, agent2)) {
					graph.addEdge(new DistanceEdge(), agent1, agent2);
				}
			}
		}
		DijkstraShortestPath<AbstractSensorAgent, DistanceEdge> shortestPath = new DijkstraShortestPath<>(
				graph);

		List<List<String>> retval = new ArrayList<>();
		for (AbstractSensorAgent destination : destinations) {
			List<DistanceEdge> path = null;
			try {
				path = shortestPath.getPath(source, destination);
			} catch (IllegalArgumentException e) {
				TextUi.println("Illegal argument exception: probably disconnected.");
			}
			if (path == null) {
				TextUi.println("Could not find the shortest path.");
				throw new Error("Could not find the shortest path!!");
			}
			List<String> pathStrings = new ArrayList<>();
			pathStrings.add(source.getName());
			for (DistanceEdge edge : path) {
				pathStrings.add(graph.getEndpoints(edge).getSecond().getName());
			}
			TextUi.println(pathStrings);
			retval.add(pathStrings);
		}
		return retval;
	}

	/**
	 * Returns the shortest path from source to destination in the network built
	 * from the agents - connectivity is based on the transmission range
	 * 
	 * @param agents
	 * @return
	 */
	public static List<String> getShortestPath(
			List<AbstractSensorAgent> agents, AbstractSensorAgent source,
			AbstractSensorAgent destination) {
		Graph<AbstractSensorAgent, DistanceEdge> graph = new DirectedSparseGraph<>();
		for (AbstractSensorAgent sensorAgent : agents) {
			graph.addVertex(sensorAgent);
		}
		// forming neighborhood graph
		for (AbstractSensorAgent agent1 : agents) {
			//ArrayList<AbstractSensorAgent> neighbors = new ArrayList();
			for (AbstractSensorAgent agent2 : agents) {
				if (agent1.equals(agent2)) {
					continue;
				}
				if (isConnected(agent1, agent2)) {
					graph.addEdge(new DistanceEdge(), agent1, agent2);
				}
			}
		}
		DijkstraShortestPath<AbstractSensorAgent, DistanceEdge> shortestPath = new DijkstraShortestPath<>(
				graph);

		List<DistanceEdge> path = null;
		try {
			path = shortestPath.getPath(source, destination);
		} catch (IllegalArgumentException e) {
			TextUi.println("Illegal argument exception: probably disconnected.");
		}
		if (path == null) {
			// throw new Error("Could not find the shortest path!!");
			return null;
		}
		List<String> pathStrings = new ArrayList<>();
		pathStrings.add(source.getName());
		for (DistanceEdge edge : path) {
			pathStrings.add(graph.getEndpoints(edge).getSecond().getName());
		}
		return pathStrings;
	}

	/**
	 * This method returns the path from the current node to the destination
	 * based on the geographical greedy based algorithm for shortest path
	 * 
	 * @param agents
	 * @param source
	 * @param destination
	 * @return
	 */
	public static List<String> getGreedyNeighborPaths(
			List<AbstractSensorAgent> agents, AbstractSensorAgent source,
			AbstractSensorAgent destination) {

		List<String> pathStrings = new ArrayList<String>();
		Location sinkLoc = destination.getNode().getLocation();
		// SensorNode nearestNode = source.getNode();
		AbstractSensorAgent nextAgent = source;
		pathStrings.add(nextAgent.getName());
		// TextUi.println("Path for node :" +nextAgent.getNode().getName());

		while (!nextAgent.getNode().equals(destination.getNode())) {
			List<AbstractSensorAgent> neighbors = new ArrayList<>();
			double best_distance = nextAgent.getNode().getLocation()
					.distanceTo(sinkLoc);
			SensorNode nearestNode = nextAgent.getNode();
			for (AbstractSensorAgent sensorAgent : agents) {
				if (sensorAgent.equals(nextAgent))
					continue;
				if (isConnected(nextAgent, sensorAgent))
					neighbors.add(sensorAgent);
			}
			// greedy neighbor selection
			for (AbstractSensorAgent neighbor : neighbors) {
				double neighbor_distance = neighbor.getNode().getLocation()
						.distanceTo(sinkLoc);
				if (neighbor_distance < best_distance
						|| neighbor.equals(destination)) {
					best_distance = neighbor_distance;
					nearestNode = neighbor.getNode();
					if (nearestNode.equals(destination))
						break;
				}
			}

			pathStrings.add(nearestNode.getName());

			if (nearestNode.equals(nextAgent.getNode()))
				break;

			nextAgent = nearestNode.getAgent();
		}

		// TextUi.println("Returned path: " + pathStrings.toString());

		return pathStrings;
	}

	/**
	 * This method returns message with sensor node (as the nexthop) that is
	 * calculated using the GPSR Forwarding algorithm. The algorithm initially
	 * tried to greedy forward the packet to the destination. If it fails, then
	 * it opts for the perimeter routing. If perimeter routing also fails then
	 * it means that the graph is disconnected
	 * 
	 * @param self
	 * @param source
	 * @param message
	 * @param world
	 * @return
	 */
	public static boolean gpsrForwarding(AbstractSensorAgent self,
			ACLMessage message, SensorNetworkWorld world) {
		AbstractSensorAgent sender = (AbstractSensorAgent) world.getDirectory()
				.getAgent(message.getSender());
		AbstractSensorAgent sinkAgent = world.getSinkNode().getAgent();
		Location sinkLocation = sinkAgent.getNode().getLocation();
		// if the message destination and the source destination are same it
		// means that the message has reached its destination

		// if(message.getDestination().equals(self.getName())){
		// }
		if (message.getValue(MODE).equals(GREEDY)) {
			TextUi.print("Greedy---->");
			if (!greedyForward(self, message, world)) {
				TextUi.print("Perimeter--->");
				message.setValue(MODE, PERIMETER);
				message.setValue(LP, self.getNode().getLocation()); // Routing
																	// failed
																	// location
				message.setValue(LF, self.getNode().getLocation()); // Face-Intersection
																	// point
				AbstractSensorAgent t = rightHandForward(self, sender, world); // set
																				// the
																				// destination
				message.setValue(FIRST_EDGE, new Pair<>(
						self, t)); // add the pair-link to the message
				((ForwarderSensorAgent) self).setForwardingDestination(t
						.getName());
				((ForwarderSensorAgent) self).forwardMessage(message);
			}
		} else if (message.getValue(MODE).equals(PERIMETER)) {
			TextUi.print("Perimeter--->");
			if (self.getNode().getLocation().distanceTo(sinkLocation) < ((Location) message
					.getValue(LP)).distanceTo(sinkLocation)) {
				TextUi.print("Greedy---->");
				message.setValue(MODE, GREEDY);
				greedyForward(self, message, world);
			} else {
				AbstractSensorAgent t = rightHandForward(self, sender, world);
				Pair<AbstractSensorAgent> newPair = new Pair<>(
						self, t);
				@SuppressWarnings("unchecked")
				Pair<AbstractSensorAgent> oldPair = (Pair<AbstractSensorAgent>) message
						.getValue(FIRST_EDGE);
				if ((newPair.getFirst().equals(oldPair.getFirst()) && newPair
						.getSecond().equals(oldPair.getSecond()))
						|| (newPair.getFirst().equals(oldPair.getSecond()) && newPair
								.getSecond().equals(oldPair.getFirst())))
					((ForwarderSensorAgent) self).setForwardingDestination(t
							.getName());

				else {
					t = faceChange(self, t, message, world);
					((ForwarderSensorAgent) self).setForwardingDestination(t
							.getName());
					((ForwarderSensorAgent) self).forwardMessage(message);
				}
			}
		}
		if ((int) message.getValue(TTL) > 100)
			return false;
		return true;
	}

	/**
	 * This method applies the face change to a given face if it intersects with
	 * the line between source and the destination. Remember that we have to
	 * traverse the packet along those edges which are not intersected from the
	 * source-to-destination line. And those edges should be on the face whose
	 * atlease one edge is being interesected from the source-to-destination
	 * line
	 * 
	 * @param self
	 * @param t
	 * @param message
	 * @param world
	 * @return
	 */
	public static AbstractSensorAgent faceChange(AbstractSensorAgent self,
			AbstractSensorAgent t, ACLMessage message, SensorNetworkWorld world) {
		AbstractSensorAgent sinkAgent = world.getSinkNode().getAgent();
		AbstractSensorAgent t2;
		Location sinkLocation = sinkAgent.getNode().getLocation();
		Location lp = (Location) message.getValue(LP);
		Location lf = (Location) message.getValue(LF);
		Location i = intersection(t.getNode().getLocation(), self.getNode()
				.getLocation(), lp.getLocation(), sinkLocation);
		if (!i.equals(null)) {
			if (i.distanceTo(sinkLocation) < lf.distanceTo(sinkLocation)) {
				TextUi.print("FaceChange--->");
				t2 = rightHandForward(self, t, world);
				t2 = faceChange(self, t2, message, world);
				message.setValue(FIRST_EDGE, new Pair<>(
						self, t2)); // add the pair-link to the message
			}
		}
		return t;
	}

	/**
	 * This methods returns the point of intersection between two lines. For
	 * calculating the point of intersection we have used the determinant of
	 * matrices as given here
	 * http://en.wikipedia.org/wiki/Line-line_intersection
	 * 
	 * @param t
	 * @param Lp
	 * @param self
	 * @param dest
	 * @return
	 */
	public static Location intersection(Location t, Location self, Location Lp,
			Location dest) {
		// |x1 y1|
		// |x2 y2|
		Matrix A = new Matrix(new double[][] { { t.getX(), t.getY() },
				{ self.getX(), self.getY() } });
		// |x3 y3|
		// |x4 y4|
		Matrix B = new Matrix(new double[][] { { Lp.getX(), Lp.getY() },
				{ dest.getX(), dest.getY() } });
		// |x1 1|
		// |x2 1|
		Matrix C = new Matrix(new double[][] { { t.getX(), 1 },
				{ self.getX(), 1 } });
		// |x3 1|
		// |x4 1|
		Matrix D = new Matrix(new double[][] { { Lp.getX(), 1 },
				{ dest.getX(), 1 } });
		// |y1 1|
		// |y2 1|
		Matrix E = new Matrix(new double[][] { { t.getY(), 1 },
				{ self.getY(), 1 } });
		// |y3 1|
		// |y4 1|
		Matrix F = new Matrix(new double[][] { { Lp.getY(), 1 },
				{ dest.getY(), 1 } });

		Matrix numerator = new Matrix(new double[][] { { A.det(), C.det() },
				{ B.det(), D.det() } });
		Matrix denominator = new Matrix(new double[][] { { C.det(), E.det() },
				{ D.det(), F.det() } });

		double X = numerator.det() / denominator.det();

		numerator = new Matrix(new double[][] { { A.det(), E.det() },
				{ B.det(), F.det() } });
		double Y = numerator.det() / denominator.det();
		if (X == 0 && Y == 0)
			return null;
		return new Location(X, Y);
	}

	/**
	 * This method returns a sensor node which is selected using the greedy
	 * forwarding algorithm. If there is no node that is to be selected, i.e.,
	 * the source node is the nearest to the destination then this method would
	 * return null value.
	 * 
	 * @param self
	 * @param m
	 * @param world
	 * @return
	 */
	public static boolean greedyForward(AbstractSensorAgent self,
			ACLMessage message, SensorNetworkWorld world) {
		AbstractSensorAgent sinkAgent = world.getSinkNode().getAgent();
		Location sinkLoc = sinkAgent.getNode().getLocation();
		SensorNode nearestNode = self.getNode();
		double best_distance = self.getNode().getLocation().distanceTo(sinkLoc);
		ArrayList<AbstractSensorAgent> neighbors = getNeighbors(self, world);

		for (AbstractSensorAgent neighbor : neighbors) {
			double neighbor_distance = neighbor.getNode().getLocation()
					.distanceTo(sinkLoc);
			if (neighbor_distance < best_distance || neighbor.equals(sinkAgent)) {
				best_distance = neighbor_distance;
				nearestNode = neighbor.getNode();
				if (nearestNode.equals(sinkAgent))
					break;
			}
		}
		((ForwarderSensorAgent) self).setForwardingDestination(nearestNode
				.getName());

		if (nearestNode.getName().equals(self.getName())) { // greedy routing
															// fails if the
															// source is the
															// best node itself
			return false;
		}
		((ForwarderSensorAgent) self).forwardMessage(message);
		return true; // set the next-hop as the nearest node found in the greedy
						// algorithm
	}

	/**
	 * Perimeter Routing -------- This method uses the right-hand rule to return
	 * the node that is first edge encountered in counter clockwise movement.
	 * The right hand rule is applied in the perimeter routing. In this routing,
	 * the packet is forwarded by the vertices which are connected by such
	 * vertices that intersect the geometric line connecting source and
	 * destination. The source is the point at which greedy forwarding had
	 * failed.
	 * 
	 * @param self
	 * @param sinkAgent
	 * @param world
	 * @return
	 */
	public static AbstractSensorAgent rightHandForward(
			AbstractSensorAgent self, AbstractSensorAgent source,
			SensorNetworkWorld world) {
		SensorNode a_min = source.getNode();
		// get the locations of the souce and the destination
		Location selfLoc = self.getNode().getLocation();
		Location sourceLoc = source.getNode().getLocation();
		ArrayList<AbstractSensorAgent> neighbors = getNeighbors(self, world);

		double b_in = (2 * Math.PI)
				+ Math.atan2(selfLoc.getY() - sourceLoc.getY(), selfLoc.getX()
						- sourceLoc.getX());
		double delta_min = 3 * Math.PI;
		double b_a, delta_b, d_min, distance;
		d_min = Double.POSITIVE_INFINITY;
		for (AbstractSensorAgent neighbor : neighbors) {
			Location neighborLoc = neighbor.getNode().getLocation();
			if (neighbor.getNode().equals(source)
					|| neighbor.getNode().equals(self))
				continue;
			b_a = (2 * Math.PI)
					+ Math.atan2(selfLoc.getY() - neighborLoc.getY(),
							selfLoc.getX() - neighborLoc.getX());
			delta_b = (2 * Math.PI) + (b_a - b_in);
			if (delta_b < delta_min) {
				delta_min = delta_b;
				a_min = neighbor.getNode();
			}
			if (delta_b == delta_min) {
				distance = selfLoc.distanceTo(neighborLoc);
				if (distance < d_min) {
					d_min = distance;
					a_min = neighbor.getNode();
				}
			}

		}
		return a_min.getAgent();
	}

	/**
	 * This method returns back the Relative Neighborhood Graph. The Relative
	 * Neighborhood Graph is subgraph of Gabriel graph. The presence of point w
	 * within the circle (region of transmission, i.e., neighbors) prevents
	 * points u and v from being neighbors. if distance(u, v) > max[distance(u,
	 * w), distance(v, w)] then eliminate the edge (u, v)
	 * 
	 * @param node
	 * @param neighbors
	 * @return
	 */
	public static List<AbstractSensorAgent> relativeNeighborGraph(
			AbstractSensorAgent node, ArrayList<AbstractSensorAgent> neighbors) {
		List<AbstractSensorAgent> retVal = new ArrayList<>(
				neighbors);
		Location u = node.getNode().getLocation();
		for (AbstractSensorAgent neighbor : neighbors) {
			Location v = neighbor.getNode().getLocation();
			for (AbstractSensorAgent otherNeighbor : neighbors) {
				Location w = otherNeighbor.getNode().getLocation();
				if (neighbor.equals(otherNeighbor))
					continue;
				else if (u.distanceTo(v) > Math.max(u.distanceTo(w),
						v.distanceTo(w))) {
					retVal.remove(neighbor);
					break;
				}
			}
		}
		return retVal;
	}

	/**
	 * This method returns back the list of neighbor after applying the relative
	 * neighborhood graph which converts the non-planarized graph into a
	 * planarized graph
	 * 
	 * @param self
	 * @param world
	 * @return
	 */
	public static ArrayList<AbstractSensorAgent> getNeighbors(
			AbstractSensorAgent self, SensorNetworkWorld world) {
		ArrayList<AbstractSensorAgent> neighbors = new ArrayList<>();
		List<AbstractSensorAgent> allAgents = SensorRoutingHelper
				.getSensorAgents(world, ForwarderSensorAgent.class);
		List<AbstractSensorAgent> sourceAgents = new ArrayList<>(allAgents);
		allAgents.add(world.getSinkNode().getAgent());
		for (AbstractSensorAgent sensorAgent : allAgents) {
			if (sensorAgent.equals(self)) {
				continue;
			}
			if (isConnected(self, sensorAgent)) {
				neighbors.add(sensorAgent);
			}
		}
		return neighbors;
		// return relativeNeighborGraph(self, neighbors);
		// return voidRegions(relativeNeighborGraph(self, neighbors));
		// return relativeNeighborGraph(self, voidRegions(neighbors));

	}

	private static ArrayList<AbstractSensorAgent> voidRegions(
			ArrayList<AbstractSensorAgent> neighbors) {
		ArrayList<AbstractSensorAgent> retVal = new ArrayList<>(
				neighbors);
		Area area = new Area(new Rectangle2D.Double(20, 20, 0, 0));
		for (AbstractSensorAgent agent : neighbors)
			if (area.contains(agent.getNode().getLocation().getX(), agent
					.getNode().getLocation().getY())) {
				retVal.remove(agent);
			}
		return retVal;

	}

	/**
	 * Returns the shortest path from source to destination in the network built
	 * from the agents - connectivity is based on the transmission range and
	 * Gabriel Graph
	 * 
	 * @param agents
	 * @return
	 */
	public static List<String> getRNGShortestPath(
			List<AbstractSensorAgent> agents, AbstractSensorAgent source,
			AbstractSensorAgent destination) {

		Graph<AbstractSensorAgent, DistanceEdge> graph = new DirectedSparseGraph<>();
		for (AbstractSensorAgent sensorAgent : agents)
			graph.addVertex(sensorAgent);

		for (AbstractSensorAgent agent1 : agents) {
			ArrayList<AbstractSensorAgent> neighbors = new ArrayList<AbstractSensorAgent>();
			for (AbstractSensorAgent agent2 : agents) {
				if (agent1.equals(agent2)) {
					continue;
				}
				if (isConnected(agent1, agent2)) {
					neighbors.add(agent2);
				}
			}

			List<AbstractSensorAgent> retVal = new ArrayList<AbstractSensorAgent>();
			retVal = relativeNeighborGraph(agent1, neighbors); // provides list
																// of relative
																// neighbors
			for (AbstractSensorAgent agent : retVal) {
				graph.addEdge(new DistanceEdge(), agent1, agent);
			}
		}

		DijkstraShortestPath<AbstractSensorAgent, DistanceEdge> shortestPath = new DijkstraShortestPath<>(
				graph);

		List<DistanceEdge> path = null;
		try {
			path = shortestPath.getPath(source, destination);
		} catch (IllegalArgumentException e) {
			TextUi.println("Illegal argument exception: probably disconnected.");
		}
		if (path == null) {
			// throw new Error("Could not find the shortest path!!");
			return null;
		}
		List<String> pathStrings = new ArrayList<String>();
		pathStrings.add(source.getName());
		for (DistanceEdge edge : path) {
			pathStrings.add(graph.getEndpoints(edge).getSecond().getName());
		}
		return pathStrings;
	}

	public static void createRNGPathsForForwarderSensorAgents(
			AbstractSensorAgent sinkAgent, SensorNetworkWorld world) {
		List<AbstractSensorAgent> allAgents = SensorRoutingHelper
				.getSensorAgents(world, ForwarderSensorAgent.class);
		List<AbstractSensorAgent> sourceAgents = new ArrayList<>(allAgents);
		allAgents.add(sinkAgent);

		for (AbstractSensorAgent source : sourceAgents) {
			List<String> path = SensorRoutingHelper.getRNGShortestPath(
					allAgents, source, sinkAgent);
			if (path.size() < 2) {
				TextUi.println("Path finding failed for " + source.getName()
						+ " to " + sinkAgent.getName());
				TextUi.println("Returned path was: " + path);
				TextUi.println("This most likely means that the graph is not connected.");
				System.exit(1);
			}
			((ForwarderSensorAgent) source).setForwardingDestination(path
					.get(1));
		}
	}

	/**
	 * This method returns a single greedy neighbor of the current node
	 * 
	 * @param agents
	 * @param source
	 * @param destination
	 * @param world
	 * @return
	 */
	public static String getGreedyNeighbor(List<AbstractSensorAgent> agents,
			AbstractSensorAgent source, AbstractSensorAgent destination,
			SensorNetworkWorld world) {

		Location sinkLoc = destination.getNode().getLocation();
		SensorNode nearestNode = source.getNode();
		double best_distance = source.getNode().getLocation()
				.distanceTo(sinkLoc);

		ArrayList<AbstractSensorAgent> neighbors = new ArrayList<AbstractSensorAgent>();
		List<AbstractSensorAgent> allAgents = SensorRoutingHelper
				.getSensorAgents(world, ForwarderSensorAgent.class);
		List<AbstractSensorAgent> sourceAgents = new ArrayList<>(allAgents);
		allAgents.add(world.getSinkNode().getAgent());

		for (AbstractSensorAgent sensorAgent : allAgents) {
			if (sensorAgent.equals(source)) {
				continue;
			}
			if (isConnected(source, sensorAgent)) {
				neighbors.add(sensorAgent);

			}
		}

		for (AbstractSensorAgent neighbor : neighbors) {
			double neighbor_distance = neighbor.getNode().getLocation()
					.distanceTo(sinkLoc);
			if (neighbor_distance < best_distance
					|| neighbor.equals(destination)) {
				best_distance = neighbor_distance;
				nearestNode = neighbor.getNode();
				if (nearestNode.equals(destination))
					break;
			}
		}
		return nearestNode.getName();
	}

}
