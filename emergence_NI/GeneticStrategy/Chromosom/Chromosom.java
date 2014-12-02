package emergence_NI.GeneticStrategy.Chromosom;


public class Chromosom implements Comparable<Chromosom>{
	public Double score;
	public int[] actions;
	public boolean rated = false;
	
	public Chromosom(double score, int[] actions){
		this.score = score;
		this.actions = actions.clone();
	}
	
	/**
	 * sorted by the score
	 */
	@Override
	public int compareTo(Chromosom second) {
		int test =  second.score.compareTo(this.score);
		
		if (this.score < second.score) {
			return 1;
		} else if (this.score > second.score) {
			return -1;
		} else {
			return 0;
		}
	}
	
	public Chromosom clone(){
		return new Chromosom(this.score, this.actions);
	}

}