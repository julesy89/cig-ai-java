package emergence.agents;

import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import emergence.Environment;
import emergence.Factory;
import emergence.heuristics.DistanceHeuristic;
import emergence.strategy.ExplorerStrategy;
import emergence.strategy.mcts.MCTSNode;
import emergence.strategy.mcts.MCTStrategy;
import emergence.targets.ATarget;
import emergence.util.ActionTimer;
import emergence.util.Helper;
import emergence.util.MapInfo;

public class MCTSHeuristicAgent extends AbstractPlayer {

	/** print out information. only DEBUG! */
	public static final boolean VERBOSE = false;

	/** use the rolling horizon */
	public boolean rollingHorizon = true;

	/** the MCTS strategy used */
	private MCTStrategy strategy = null;

	/** the actual best action */
	private ACTIONS lastAction = ACTIONS.ACTION_NIL;

	/** the actual best action */
	private ATarget bestTarget = null;

	/**
	 * Constructs a new MCTSHeuristic agent. Uses MCST starteies and rolling
	 * horizon.
	 * 
	 * @param stateObs
	 * @param elapsedTimer
	 */
	public MCTSHeuristicAgent(StateObservation stateObs,
			ElapsedCpuTimer elapsedTimer) {
		// System.out.println(MapInfo.info(stateObs));
		strategy = new MCTStrategy(new MCTSNode(null));
		strategy.root = new MCTSNode(null);

		ActionTimer timer = new ActionTimer(elapsedTimer);
		timer.timeRemainingLimit = 100;
		ExplorerStrategy explorer = new ExplorerStrategy(stateObs);
		if (VERBOSE)
			System.out.println(String.format("[%s] %s", stateObs.getGameTick(),
					explorer));
		explorer.expand(stateObs, timer);

		if (VERBOSE) {
			System.out.println(MapInfo.info(stateObs));
			System.out.println(Factory.getGameDetection().detect(stateObs));
		}

	}

	/**
	 * Returns the action which will be executed. Uses MCTS startegies and
	 * heuristic.
	 */
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

		// print parameters for csv output
		if (stateObs.getGameTick() == 0) {
			printParam();
		}

		// track the last action
		Factory.getFieldTracker().track(stateObs, lastAction);
		Environment env = Factory.getEnvironment();

		// create a timer
		ActionTimer timer = new ActionTimer(elapsedTimer);
		timer.timeRemainingLimit = 25;
		ExplorerStrategy explorer = new ExplorerStrategy(stateObs);
		if (VERBOSE)
			System.out.println(String.format("[%s] %s", stateObs.getGameTick(),
					explorer));
		explorer.expand(stateObs, timer);

		if (env.getWinningTarget(stateObs) != null) {
			bestTarget = env.getWinningTarget(stateObs);
		} else if (env.getScoringTarget(stateObs) != null) {
			bestTarget = env.getScoringTarget(stateObs);
		}
		if (bestTarget != null)
			strategy.heuristic = new DistanceHeuristic(bestTarget);

		timer.timeRemainingLimit = 3;
		if (stateObs.getGameTick() != 0) {
			if (rollingHorizon) {
				strategy.rollingHorizon(lastAction);
			} else {
				strategy = new MCTStrategy(new MCTSNode(null));
			}
		}

		strategy.expand(stateObs, timer);
		ACTIONS a = strategy.act();
		lastAction = a;

		if (VERBOSE) {
			System.out.println(strategy);
			System.out.println(env);
		}

		return a;
	}

	/**
	 * print parameters
	 */
	public void printParam() {
		String[] params = new String[3];

		params[0] = "MCTSHeuristicAgent";
		params[1] = Boolean.toString(this.rollingHorizon);
		// values from MCTSStrategy
		params[2] = this.strategy.toCSVString();

		Helper.printParameter(params);
	}

}
