package emergence_NI.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ontology.Types;
import ontology.Types.WINNER;
import tools.Vector2d;
import core.game.StateObservation;
import emergence_NI.helper.ActionMap;

/**
 * This class represents a TreeNode. Important is that there is the possibility
 * to simulate next steps of a node. This has the consequence that there are
 * always father and children states.
 */
public class Node {

	// father node, if null it's the root
	public Node father;

	// state observation. if it's ones advanced we need not to do it again!
	public StateObservation stateObs;

	// always the last action
	public Types.ACTIONS lastAction;

	// always the last action
	public Types.ACTIONS rootAction;
	
	// it's static to get it fast
	public int level;

	// the Q value that is needed for the MCTS
	public double Q;

	// storage for the utc value!
	public double uct;

	// counts how often any expandation function was executed!
	public int visited;

	// array of children if there were expanded
	public Node[] children;
	

	public double score;
	public double exploitation;
	public double exploration;
	public double heuristicValue;
	public double historyValue;
	
	

	/**
	 * A tree node is defined by using ONLY the state observation
	 * 
	 * @param stateObs
	 *            observation of this node!
	 */
	public Node(StateObservation stateObs) {
		this.father = null;
		this.stateObs = stateObs;
		this.children = new Node[stateObs.getAvailableActions().size()];
		this.Q = 0;
		this.level = 0;
		this.rootAction = Types.ACTIONS.ACTION_NIL;
	}

	/**
	 * A tree node is defined by using ONLY the state observation
	 * 
	 * @param stateObs
	 *            observation of this node!
	 */
	public Node(StateObservation stateObs, Node father, Types.ACTIONS lastAction) {
		this(stateObs);
		this.father = father;
		this.level = father.level + 1;
		this.lastAction = lastAction;
		this.rootAction = (father.father == null) ? lastAction : father.rootAction;
	}

	/**
	 * Return a random child in this tree.
	 * 
	 * @param r
	 * @return null if there are no available actions else the random child!
	 */
	public Node getRandomChild(Random r, boolean mustBeNew) {

		Types.ACTIONS a = null;

		int size = stateObs.getAvailableActions().size();
		if (size == 0)
			return null;

		// get a random child
		if (!mustBeNew) {
			a = getRandomAction(r);
			// get a random child that is not expanded yet!
		} else {
			ArrayList<Types.ACTIONS> posActions = new ArrayList<Types.ACTIONS>();
			for (int i = 0; i < children.length; i++) {
				if (children[i] == null)
					posActions.add(ActionMap.getAction(stateObs, i));
			}
			int index = r.nextInt(posActions.size());
			a = posActions.get(index);
		}
		Node child = getChild(a, true);
		return child;
	}
	

	public Types.ACTIONS getRandomAction(Random r) {
		int size = stateObs.getAvailableActions().size();
		int index = r.nextInt(size);
		Types.ACTIONS a = stateObs.getAvailableActions().get(index);
		return a;
	}

	/**
	 * Create one child if the action a is used!
	 * 
	 * @param a
	 * @return
	 */
	public Node getChild(Types.ACTIONS a) {

		// copy the state
		StateObservation tmpStateObs = stateObs.copy();
		tmpStateObs.advance(a);

		// create the node and set the correct values
		Node child = new Node(tmpStateObs, this, a);

		// set the child that it is not expanded again!
		int index = ActionMap.getInt(stateObs, a);
		children[index] = child;

		return child;
	}

	/**
	 * Create one child if the action a is used but first looks if it was
	 * created before!
	 * 
	 * @param a
	 * @return
	 */
	public Node getChild(Types.ACTIONS a, boolean useCache) {
		int index = ActionMap.getInt(stateObs, a);
		if (children[index] != null)
			return children[index];
		else
			return getChild(a);
	}
	
	

	/**
	 * Create a list of all possible children that could be created from this
	 * state.
	 * 
	 * @param node
	 *            node that should be expanded
	 * @return list of all possible children states
	 */
	public List<Node> getChildren() {

		// if children are cached use them
		if (isFullyExpanded())
			return Arrays.asList(children);

		ArrayList<Types.ACTIONS> actionList = stateObs.getAvailableActions();

		// for each possible action
		for (int i = 0; i < actionList.size(); i++) {

			// action that has to be performed
			Types.ACTIONS a = actionList.get(i);
			getChild(a, true);

		}

		return Arrays.asList(children);
	}

	/**
	 * Return whether this node was simulated for all the actions!
	 * 
	 * @return fully expanded or not!
	 */
	public boolean isFullyExpanded() {
		for (int i = 0; i < children.length; i++) {
			if (children[i] == null)
				return false;
		}
		return true;
	}
	
	public boolean isNotExpanded() {
		for (int i = 0; i < children.length; i++) {
			if (children[i] != null)
				return false;
		}
		return true;
	}

	public String hash() {
		return hash(stateObs, lastAction);
	}
	
	
	public static String hash(StateObservation stateObs, Types.ACTIONS action) {
		Vector2d pos = stateObs.getAvatarPosition();
		String used = (action == null || action != Types.ACTIONS.ACTION_USE) ? "n"
				: "y"; 
		return String.format("[%s,%s,%s]", pos.x, pos.y, used);
	}


	@Override
	public String toString() {
		Vector2d pos = stateObs.getAvatarPosition();
		String s = String
				.format("me:[%s,%s] | last:%s | level:%s | Q:%s | visited:%s | utc:%s | fE:%s | score:%s | children:[",
						pos.x, pos.y, lastAction, level, Q, visited, uct,
						isFullyExpanded(), score);
		for (int i = 0; i < children.length; i++) {
			if (children[i] == null)
				s += "_,";
			else
				s += "x,";
		}
		s += "]";
		s += String
				.format("| exploitation:%s | exploration:%s | heuristic:%s | history:%s",
						exploitation, exploration, heuristicValue, historyValue);
		return s;
	}

	public static double getScore(StateObservation stateObs) {
		if (stateObs.getGameWinner() == WINNER.PLAYER_WINS)
			return 100;
		else if (stateObs.getGameWinner() == WINNER.PLAYER_LOSES)
			return -100;
		else
			return stateObs.getGameScore();
	}

	
	public String print() {
		String s = "";
		for (int i = 0; i < children.length; i++) {
			if (children[i] == null) continue;
			for (int j = 0; j < children[i].level; j++) {
				s += '\t';
			}
			s += children[i].toString();
			s += '\n';
			s += children[i].print();
		}
		return s;
	}

}