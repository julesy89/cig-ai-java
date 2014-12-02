
import java.util.Random;

import core.ArcadeMachine;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 04/10/13
 * Time: 16:29
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Test
{

    public static void main(String[] args)
    {
        //Available controllers:
        String controller = "emergence_NI.Agent";
        String controller2 = "controllers.human.Agent";
        String controller3 = "controllers.sampleMCTS.Agent";

        //Available games:
        String gamesPath = "examples/gridphysics/";

        String gameStr = "aliens";
        int levelIdx = 0; 
        
        
        //"aliens", "boulderdash", "butterflies", "chase", "frogs",
        //"missilecommand", "portals", "sokoban", "survivezombies", "zelda", 
        //"camelRace", "digdug", "firestorms", "infection", "firecaster",
        //"overload", "pacman", "seaquest", "whackamole", "eggomania"


        //Other settings
        boolean visuals = true;
        String recordActionsFile = null; //where to record the actions executed. null if not to save.
        int seed = new Random().nextInt();

        //Game and level to play
        String game = gamesPath + gameStr + ".txt";
        String level1 = gamesPath + gameStr + "_lvl" + levelIdx +".txt";
        ArcadeMachine.runOneGame(game, level1, visuals, controller, recordActionsFile, seed);


    }
}