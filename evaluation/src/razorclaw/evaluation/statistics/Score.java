package razorclaw.evaluation.statistics;

import java.util.ArrayList;

public class Score {
	private ArrayList<Integer> _scores = new ArrayList<Integer>();

	public void setScores(ArrayList<Integer> _scores) {
		this._scores = _scores;
	}

	public ArrayList<Integer> getScores() {
		return _scores;
	}

}