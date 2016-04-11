/**
 * 
 */
package yaes.sensornetwork.scenarios.tryandbounce;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lboloni
 * 
 */
public class TABPathRecord implements Serializable {

	private static final long serialVersionUID = -7526667174206460572L;

	/**
	 * Nodes which answered, but bounced back
	 */
	private List<String> bouncedNodes = new ArrayList<String>();

	/**
	 * Nodes which do not answered
	 */
	private List<String> notAnsweringNodes = new ArrayList<String>();

	/**
	 * The nodes traversed by the system
	 */
	private List<String> traversedNodes = new ArrayList<String>();

	/**
	 * Constructor
	 * 
	 * @param node
	 */
	public TABPathRecord(String node) {
		traversedNodes.add(node);
	}

	public TABPathRecord(TABPathRecord other) {
		traversedNodes.addAll(other.traversedNodes);
		notAnsweringNodes.addAll(other.notAnsweringNodes);
		bouncedNodes.addAll(other.bouncedNodes);
	}

	/**
	 * @param myself
	 */
	public void addBouncedNodes(String myself) {
		traversedNodes.remove(myself);
		bouncedNodes.add(myself);
	}

	/**
	 * @param failedNode
	 */
	public void addFailedNode(String failedNode) {
		traversedNodes.remove(failedNode);
		notAnsweringNodes.add(failedNode);
	}

	/**
	 * @param nextNode
	 */
	public void addProgressNode(String nextNode) {
		traversedNodes.add(nextNode);
	}

	/**
	 * Returns true if the node is suitable as the next hop.
	 * 
	 * @param hop
	 * @return
	 */
	public boolean canBeNextHop(String hop) {
		if (traversedNodes.contains(hop)) {
			return false;
		}
		if (bouncedNodes.contains(hop)) {
			return false;
		}
		if (notAnsweringNodes.contains(hop)) {
			return false;
		}
		return true;
	}

	/**
	 * Send back to the last traversed node
	 * 
	 * @return
	 */
	public String getCannotSendDestination() {
		if (traversedNodes.size() == 0) {
			return null;
		}
		return traversedNodes.get(traversedNodes.size() - 1);
	}

	/**
	 * @return
	 */
	public String getOriginalObserver() {
		if (traversedNodes.isEmpty()) {
			return null;
		}
		return traversedNodes.get(0);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<");
		buffer.append(traversedNodes + ",");
		buffer.append(notAnsweringNodes + ",");
		buffer.append(bouncedNodes);
		buffer.append(">");
		return buffer.toString();
	}

}
