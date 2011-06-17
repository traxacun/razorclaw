package razorclaw.evaluation.statistics;

import java.util.ArrayList;

public class Assessor {
	private String _name;

	// each line contains 4 scores
	private ArrayList<Score> _lines = new ArrayList<Score>();

	public void setName(String _name) {
		this._name = _name;
	}

	public String getName() {
		return _name;
	}

	public void setLines(ArrayList<Score> _scores) {
		this._lines = _scores;
	}

	public ArrayList<Score> getLines() {
		return _lines;
	}

}
