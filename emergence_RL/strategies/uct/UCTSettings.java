package emergence_RL.strategies.uct;

import java.util.Arrays;
import java.util.Random;

import emergence_RL.heuristic.TargetHeuristic;
import emergence_RL.strategies.uct.actor.IActor;
import emergence_RL.strategies.uct.backpropagation.ABackPropagation;
import emergence_RL.strategies.uct.defaultPolicy.ADefaultPolicy;
import emergence_RL.strategies.uct.treePolicy.ATreePolicy;

public class UCTSettings {

	// this epsilon value is sometimes needed
	public static double epsilon = 1e-6;

	// actor that is used after building the tree
	public IActor actor = null;

	// tree policy for expand the nodes
	public ATreePolicy treePolicy = null;

	// default policy for the roll out
	public ADefaultPolicy defaultPolicy = null;

	// sending the feedback back with backpropagation
	
	public ABackPropagation backPropagation = null;

	// maximal depth of the tree -> 10 per default!
	public int maxDepth = 10;

	// the value for the exploration term
	public double c = Math.sqrt(2);

	// this is the discounting factor. it's one so disabled default
	public double gamma = 1;

	// generator for random numbers
	public Random r = new Random();
	
	// weights that could be used for a formula!
	public double[] weights = new double[] {1,1,1,1};
	
	// this value defines the pessimistic iterations. if 0 it's disabled.
	public int pessimisticIterations = 3;
	
	// initialize the heuristic that could be used
	public TargetHeuristic heuristic = null;

	
	public UCTSettings() {
	}
	

	public UCTSettings(IActor actor, ATreePolicy treePolicy,
			ADefaultPolicy defaultPolicy, ABackPropagation backPropagation,
			int maxDepth) {
		super();
		this.actor = actor;
		this.defaultPolicy = defaultPolicy;
		this.treePolicy = treePolicy;
		this.backPropagation = backPropagation;
		this.maxDepth = maxDepth;
	}

	
	
	/**
	 * Print the whole settings to a string!
	 */
	@Override
	public String toString() {
		String s = String.format(
				"actor:%s tree:%s default:%s back:%s depth:%s c:%s gamma:%s weight[0]:%s weight[1]:%s weight[2]:%s weight[3]:%s",
				actor.getClass().getName(), treePolicy.getClass().getName(),
				defaultPolicy.getClass().getName(), backPropagation.getClass()
						.getName(), maxDepth, c, gamma, weights[0], weights[1], weights[2], weights[3]);
		return s;
	}

	
	/**
	 * This method is needed for string initialization
	 */
	public static UCTSettings create(String parameter) {
		try {

			UCTSettings settings = new UCTSettings();
			// set the correct actor
			String[] array = parameter.split(" ");
			for (String s : array) {
				String key = s.split(":")[0];
				String value = s.split(":")[1];
				if (key.equals("actor")) {
					settings.actor = (IActor) Class.forName(value).newInstance();
				} else if (key.equals("tree")) {
					settings.treePolicy = (ATreePolicy) Class.forName(value)
							.newInstance();
				} else if (key.equals("default")) {
					settings.defaultPolicy = (ADefaultPolicy) Class.forName(value)
							.newInstance();
				} else if (key.equals("back")) {
					settings.backPropagation = (ABackPropagation) Class.forName(value)
							.newInstance();
				} else if (key.equals("depth")) {
					settings.maxDepth = Integer.valueOf(value);
				} else if (key.equals("c")) {
					settings.c = Double.valueOf(value);
				} else if (key.equals("gamma")) {
					settings.gamma = Double.valueOf(value);
				} else if (key.equals("weight[0]")) {
					settings.weights[0] = Double.valueOf(value);
				} else if (key.equals("weight[1]")) {
					settings.weights[1] = Double.valueOf(value);
				} else if (key.equals("weight[2]")) {
					settings.weights[2] = Double.valueOf(value);
				} else if (key.equals("weight[3]")) {
					settings.weights[3] = Double.valueOf(value);
				} else if (key.equals("weight[4]")) {
					settings.weights[4] = Double.valueOf(value);
				} 
				

			}

			return settings;

		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Object clone(){  
	    try{  
	        return super.clone();  
	    }catch(Exception e){ 
	        return null; 
	    }
	}

}