package emergence_HR.tree;

import java.util.Comparator;

/**
 * The comparator that is needed to find the node with the highest heuristic at
 * the priority queue.
 * 
 */
public class NodeComparator implements Comparator<Node> {
	
	
	
	public int compare(Node firstNode, Node secondNode) {
		return 0;
		/*
		if (firstNode.getHeuristic() < secondNode.getHeuristic()) {
			return 1;
		} else if (firstNode.getHeuristic() > secondNode.getHeuristic()) {
			return -1;
		} else {
			return 0;
		}
		*/
	}
}