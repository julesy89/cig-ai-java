package emergence_HR.tree;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import emergence_HR.ActionTimer;
import emergence_HR.heuristics.AHeuristic;

public class HeuristicTreeGreedy extends AHeuristicTree {

	public Queue<Node> queue = null;
	AHeuristic heuristic = null;

	Set<String> closedList = new HashSet<String>();

	double bestHeuristic = Double.NEGATIVE_INFINITY;

	public HeuristicTreeGreedy(Node root) {
		super(root);
	}

	public void expand(ActionTimer timer, AHeuristic heuristic) {

		if (this.heuristic == null || this.heuristic != heuristic) {
			this.heuristic = heuristic;
			queue = new PriorityQueue<Node>(11, new NodeComparator(heuristic));
			queue.add(root);
			closedList.add(root.hash());
		}
		
		// check whether there is time and we've further tree nodes
		while (timer.isTimeLeft() && !queue.isEmpty()) {

			// just look for the head of the queue
			Node n = queue.poll();
			heuristic.addScore(n);

			// if it is the best state until now save root as the best action
			double score = heuristic.evaluateState(n.stateObs);
			if (score > bestHeuristic) {
				bestNode = n;
				bestHeuristic = score;
				bestAction = n.rootAction;
			}

			// add all children to the queue
			LinkedList<Node> children = n.getChildren();
			// add children to the queue
			for (Node child : children) {
				if (!closedList.contains(child.hash())) {
					queue.add(child);
					closedList.add(child.hash());
				}
			}

			timer.addIteration();
		}

	}

	@Override
	public String toString() {
		String s = "\n";
		s += heuristic.toString();
		s += "\n";
		final int MAX = 4;
		int i = 0;
		for (Node n : queue) {
			s += n.toString();
			s += String
					.format(" -> %s \n", heuristic.evaluateState(n.stateObs));
			if (i >= MAX)
				break;
			++i;
		}
		s += "size: " + queue.size();
		s += "\n-----------------------------";
		return s;
	}

}
