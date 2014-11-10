package emergence_HR.strategy;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import emergence_HR.heuristics.AHeuristic;
import emergence_HR.tree.Node;
import emergence_HR.tree.Tree;

public class LevelOrderStrategy extends AStrategy {

	// the queue that is the storage for the nodes
	public Queue<Node> queue = null;
	
	public HashSet<String> closed = new HashSet<String>();
	

	/**
	 * Constructor for creating a strategy.
	 * 
	 * @param tree
	 *            to search for good nodes.
	 * @param heuristic
	 *            for expanding issues
	 */
	public LevelOrderStrategy(Tree tree, AHeuristic heuristic) {
		super(tree, heuristic);
		tree.root.score = this.heuristic.evaluateState(tree.root.stateObs);
		queue = new LinkedList<Node>();
		queue.add(tree.root);
		closed.add(tree.root.hash());
	}

	@Override
	public boolean expand() {
		if (queue.isEmpty()) return false;
		// just look for the head of the queue
		Node n = queue.poll();
		heuristic.addScore(n);
		this.checkBest(n, heuristic);
		
		for(Node child : n.getChildren()) {
			child.score = this.heuristic.evaluateState(child.stateObs);
			if (!closed.contains(child.hash())) {
				queue.add(child);
				closed.add(child.hash());
			}
		}
		
		
		return true;
	}

}