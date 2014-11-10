package emergence_HR.heuristics;


import core.game.StateObservation;
import ontology.Types;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 11/02/14
 * Time: 15:44
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class WinScoreHeuristic extends AHeuristic {

    private static final double HUGE_NEGATIVE = -10000000.0;
    private static final double HUGE_POSITIVE =  10000000.0;

    double initialNpcCounter = 0;

    public WinScoreHeuristic() {

    }
    @Override
    public double evaluateState(StateObservation stateObs) {
        boolean gameOver = stateObs.isGameOver();
        Types.WINNER win = stateObs.getGameWinner();
        double rawScore = stateObs.getGameScore();

        if(gameOver && win == Types.WINNER.PLAYER_LOSES)
            return HUGE_NEGATIVE;

        if(gameOver && win == Types.WINNER.PLAYER_WINS)
            return HUGE_POSITIVE;

        return rawScore;
    }

	@Override
	protected double getRank(StateObservation stateObs) {
		// TODO Auto-generated method stub
		return 0;
	}


}

