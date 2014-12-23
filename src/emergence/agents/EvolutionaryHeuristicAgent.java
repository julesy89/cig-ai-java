package emergence.agents;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import ontology.Types;
import ontology.Types.ACTIONS;
import ontology.Types.WINNER;
import tools.ElapsedCpuTimer;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import emergence.Environment;
import emergence.Factory;
import emergence.strategy.AStarStrategy;
import emergence.strategy.ExplorerStrategy;
import emergence.strategy.evolution.Evolution;
import emergence.strategy.evolution.EvolutionaryNode;
import emergence.targets.ATarget;
import emergence.util.ActionTimer;
import emergence.util.Helper;
import emergence.util.MapInfo;

public class EvolutionaryHeuristicAgent extends AbstractPlayer{

	// print out information. only DEBUG!
	public static boolean VERBOSE = false;

	// random object
	public static Random r = new Random();

	// current evolution object that should be iterated
	public Evolution evo;

	// so often the next step is simulated. no dead!
	public int pessimistic = 5; //den hier ändern!!!! in 8!!!!!

	// number of actions that are simulated
	public int pathLength = 6;

	// how many entries should the population has
	public int populationSize = 13;

	// number of the fittest to save for the next generation
	public int numFittest = 4;

	// update path length
	public boolean updatePathLength = true;
	
	// if update path length the target generation
	public int minGeneration = 5;
	
	
	private ATarget bestTarget = null;
	public AStarStrategy strategy;

	
	
	public EvolutionaryHeuristicAgent() {
	};

	
	public EvolutionaryHeuristicAgent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		//printParam();
		System.out.println(MapInfo.info(stateObs));
		
		evo = new Evolution(pathLength, populationSize, numFittest, stateObs);
		
		ActionTimer timer = new ActionTimer(elapsedTimer);
		timer.timeRemainingLimit = 50;
		
		ExplorerStrategy explorer = new ExplorerStrategy(stateObs);
		if (VERBOSE)  System.out.println(String.format("[%s] %s",stateObs.getGameTick() ,explorer));
		explorer.expand(stateObs, timer);

	}

	public Types.ACTIONS act(StateObservation stateObs,
			ElapsedCpuTimer elapsedTimer) {
		if(stateObs.getGameTick() == 0){printParam();}
		
		ActionTimer timer = new ActionTimer(elapsedTimer);
		timer.timeRemainingLimit = 25;
		ExplorerStrategy explorer = new ExplorerStrategy(stateObs);
		explorer.expand(stateObs, timer);
		
		
		Environment env = Factory.getEnvironment();
		if (bestTarget == null) {
			if (env.getWinningTarget(stateObs) != null) {
				bestTarget = env.getWinningTarget(stateObs);
			} 
			if (bestTarget != null) strategy = new AStarStrategy(bestTarget);
		}
		
		timer.timeRemainingLimit = 2;
		if (strategy != null) {
			strategy.expand(stateObs, timer);
			return strategy.act();
		}
		
		
		// slide the complete pool one action into the future
		evo.slidingWindow(stateObs);

		// start the evolution
		timer = new ActionTimer(elapsedTimer);
		timer.timeRemainingLimit = 7;
		evo.expand(stateObs, timer);
		

		// get the next action by using pessimistic iterations
		EvolutionaryNode selectedPath = getNextAction(timer, stateObs);
		ACTIONS nextAction = selectedPath.getFirstAction();

		
		// set the path length adaptive
		if (updatePathLength) updatePathLength(stateObs);
		
		
		// print the current status
		if (VERBOSE) {
			evo.print(evo.getPopulationSize());
		}
		
		
		if (stateObs.getGameTick() % 20 == 0) {
			if (VERBOSE) System.out.println(Factory.getEnvironment().toString());
			Factory.getEnvironment().reset();
		}

		// return the best action
		return nextAction;

	}

	
	private void updatePathLength(StateObservation stateObs) {
		int length = evo.getPathLength();
		// System.out.println(evo.getNumGeneration());
		if (evo.getNumGeneration() < minGeneration) {
			if (length > 2)
				evo.setPathLength(stateObs, length - 1);
		} else if (evo.getNumGeneration() > minGeneration) {
			if (length < 40) evo.setPathLength(stateObs, length + 1);
		}
		pathLength = evo.getPathLength();
	}

	
	private EvolutionaryNode getNextAction(ActionTimer timer, StateObservation stateObs) {
		Types.ACTIONS nextAction = ACTIONS.ACTION_NIL;
		boolean isDangerous = true;
		timer.timeRemainingLimit = 3;
		EvolutionaryNode p = null;
		Set<Types.ACTIONS> forbiddenActions = new HashSet<Types.ACTIONS>();
		int i = 0;
		while (i < evo.getPopulationSize()) {
			p = evo.getPopulation().get(i);
			++i;
			nextAction = p.getFirstAction();

			// if we tried that action before continue
			if (forbiddenActions.contains(nextAction))
				continue;
			isDangerous = pessimisticNextAction(stateObs, nextAction, timer);
			if (!isDangerous)
				break;
			else
				forbiddenActions.add(nextAction);
		}
		return p;
	}

	private boolean pessimisticNextAction(StateObservation stateObs,
			ACTIONS nextAction, ActionTimer timer) {
		boolean dead = false;
		if (nextAction != null) {
			for (int i = 0; i < pessimistic; i++) {
				if (!timer.isTimeLeft())
					break;
				StateObservation tmp = stateObs.copy();
				tmp.advance(nextAction);
				if (tmp.getGameWinner() == WINNER.PLAYER_LOSES) {
					dead = true;
					if (VERBOSE) {
						System.out.println("-------------");
						System.out.println("DANGER! NEW EVOLUTION");
						System.out.println("-------------");
					}
					break;
				}
			}
		}
		return dead;
	}
	
	public void printParam(){
		String[] params = new String[7];
		
		params[0] = "EvolutionaryHeuristicAgent";
		params[1] = Integer.toString(pessimistic);
		params[2] = Integer.toString(pathLength);
		params[3] = Integer.toString(populationSize);
		params[4] = Integer.toString(numFittest);
		params[5] = Boolean.toString(updatePathLength);
		params[6] = Integer.toString(minGeneration);
		
		Helper.printParameter(params);
	}

}
