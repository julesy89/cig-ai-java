package emergence_RL.heuristic;

import emergence_RL.tree.Node;
import emergence_RL.uct.UCTSearch;
import emergence_RL.uct.UCTSettings;
import emergence_RL.uct.treePolicy.ATreePolicy;

public class HeuristicTreePolicy extends ATreePolicy {

	
	
	@Override
	public Node bestChild(UCTSettings s, Node n, double c) {
		Node bestChild = null;
		double bestValue = Double.NEGATIVE_INFINITY;

		for (Node child : n.getChildren()) {

			child.exploitation = child.Q
					/ (child.visited + UCTSettings.epsilon);

			child.exploration = Math.sqrt(Math.log(n.visited + 1)
					/ (child.visited));

			// heuristic by using the target
			child.heuristicValue = s.heuristic
					.evaluateState(child.stateObs);
			
			child.targetHeuristicIndex = s.heuristic.lastUsed;

			// history of field
			String h = child.hash();
			Integer visitsOfField = UCTSearch.fieldVisits.get(h);
			child.historyValue = 1;
			if (visitsOfField != null && UCTSearch.maxVisitedField > 0) {
				child.historyValue = Math.sqrt((1 - visitsOfField / (double) UCTSearch.maxVisitedField));
			}
			
			child.uct = child.exploitation + c * child.exploration + child.heuristicValue
					+ child.historyValue;
			

			// check if it has the best value
			if (child.uct >= bestValue) {
				bestChild = child;
				bestValue = child.uct;
			}
		}

		return bestChild;
	}

}
