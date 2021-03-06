
import java.io.File;
import java.util.concurrent.Callable;

import emergence.util.GameResult;

/**
 *	This class is used to allow multithreading for one game result.
 */
public class ExecCallable implements Callable<GameResult> {

	
	public final static boolean VERBOSE = false;
	
	public String controller;
	public String game;
	public int level;
	public String parameter;
	public String log = "";
	
	
	public ExecCallable(String controller, String game, int level, String parameter) {
		super();
		this.controller = controller;
		this.game = game;
		this.level = level;
		this.parameter = parameter;
	}


	@Override
	public GameResult call() throws Exception {
		
		Runtime rt = Runtime.getRuntime();
		Process pRun;
		
		String strExec = String.format("java Exec %s %s %d %s", controller, game,
				level, parameter);
		
		//System.out.println("STARTED " + strExec);
		//System.out.println("STARTING " + strExec);
		
		pRun = rt.exec(strExec, null, new File("./classes"));
		pRun.waitFor();
		this.log = Exec.stringFromProc(pRun);
		
		if (VERBOSE) {
			System.out.println(parameter);
			System.out.println(log);
			System.out.println("--------------------");
		}
		
		//System.out.println("FINISHED " + strExec);
		
		int win = parseWinner(log);
		double score = parseScore(log);
		String timesteps = parseTimesteps(log);
		String parameterAgent = parseParameter(log);
		
		return new GameResult(log, game, level, win, score, timesteps, parameterAgent);
	}
	
	
	private int parseWinner(String s) {
		if (!s.contains("Result")) return -1;
		s = s.substring(s.indexOf("Result"));
		String[] l = s.split(",");
		String strWinner = l[0].substring(l[0].length() - 1);
		return (strWinner.equals("1")) ? 1 : 0;
	}
	
	
	private double parseScore(String s) {
		if (!s.contains("Result")) return -1;
		s = s.substring(s.indexOf("Result"));
		String[] l = s.split(",");
		String strScore = l[1].trim().split(":")[1];
		return Double.parseDouble(strScore);
	}
	
	private String parseTimesteps(String s) {
		if(!s.contains("timesteps")) return "-1";
		s = s.substring(s.lastIndexOf("timesteps:")+10, s.length()-1);
		return s;
	}
	
	private String parseParameter(String s) {
		if(!s.contains("PARAMETER_START")) return "-1";
		s  = s.substring(s.indexOf("PARAMETER_START")+16, s.indexOf("PARAMETER_ENDE")-1);
		return s;
	}
	
}