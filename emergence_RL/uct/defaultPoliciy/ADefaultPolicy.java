package emergence_RL.uct.defaultPoliciy;

import ontology.Types;
import tools.Vector2d;
import core.game.StateObservation;
import emergence_RL.tree.Node;
import emergence_RL.uct.UCTSettings;

public abstract class ADefaultPolicy {

	abstract public double expand(UCTSettings s, Node n);


	
	
	// Normalizes a value between its MIN and MAX.
	protected double normalise(double a_value, double a_min, double a_max) {
		return (a_value - a_min) / (a_max - a_min);
	}

	protected String hash(StateObservation stateObs, Types.ACTIONS lastAction) {
		Vector2d pos = stateObs.getAvatarPosition();
		String used = (lastAction == Types.ACTIONS.ACTION_USE) ? "y" : "n";
		return String.format("[%s,%s,%s]", pos.x, pos.y, used);
	}
	
	
	


}