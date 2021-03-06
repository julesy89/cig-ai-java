package emergence.targets;

import core.game.StateObservation;
import tools.Vector2d;

/**
 * This class represents always a target at the level. First of all you have to
 * specify the type and after that the index. index means typesId of the object.
 * 
 */
abstract public class ATarget {

	/** the different types of a target. */
	public enum TYPE {
		Portal, Resource, NPC, Immovable, Movable
	};

	/** type of this target (portal, npc, ....) */
	protected TYPE type;

	/** type id of this target */
	protected Integer itype;

	/**
	 * Create a target.
	 * 
	 * @param type
	 *            type of this target
	 * @param itype
	 *            type id of this target
	 */
	public ATarget(TYPE type, Integer itype) {
		this.type = type;
		this.itype = itype;
		if (type == null) {
			System.out.println();
		}
	}

	/**
	 * @return distance from avatar to target
	 */
	abstract public Vector2d getPosition(StateObservation stateObs);

	/**
	 * Print all needed information to specify a target.
	 */
	public String toString() {
		return String.format("(type:%s, itype:%s)", type.toString(), itype);
	}

	/**
	 * Check whether the target could move or not.
	 * 
	 * @param type
	 * @return true if it's a movable type else false.
	 */
	public static boolean isImmovable(TYPE type) {
		if (type.equals(TYPE.Portal) || type.equals(TYPE.Immovable)
				|| type.equals(TYPE.Resource)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the target exists in the given state observation.
	 * 
	 * @param stateObs
	 * @return
	 */
	public boolean exists(StateObservation stateObs) {
		return TargetFactory.getObservationFromType(type, itype, stateObs) != null;
	}

	/**
	 * Returns true if the itype of the targets is the same.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ATarget))
			return false;
		if (obj == this)
			return true;
		return this.itype == ((ATarget) obj).itype;
	}

	/**
	 * Returns the itype of the target, is used a hashvalue.
	 */
	@Override
	public int hashCode() {
		return itype;
	}

	/**
	 * Returns the type.
	 * 
	 * @return
	 */
	public TYPE getType() {
		return type;
	}

	/**
	 * Returns the itype.
	 * 
	 * @return
	 */
	public Integer getItype() {
		return itype;
	}

}
