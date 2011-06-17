package razorclaw.evaluation.statistics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class KappaF {

	// @formatter:off
	private static final String[] INPUT_FILES = { 
		"data/2nd/kappa/2nd_cn_1.txt", 
		"data/2nd/kappa/2nd_cn_2.txt",
		"data/2nd/kappa/2nd_cn_3.txt",
//		"data/1st/kappa/1st_vn_2.txt",
//		"data/1st/kappa/1st_vn_3.txt",
//		"data/1st/kappa/1st_vn_4.txt",
//		"data/1st/kappa/1st_vn_5.txt",
//		"data/1st/kappa/1st_ru_4.txt",
//		"data/1st/kappa/1st_ru_5.txt"
	};
	
	private static final int[] CATEGORY = {0, 1, 2, 3, 4, 5};
	// @formatter:on

	private static final Logger LOG = Logger.getLogger(KappaF.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<Assessor> assessors = new ArrayList<Assessor>();
		for (String s : INPUT_FILES) {
			try {
				assessors.add(loadFile(s));
			} catch (IOException e) {
				e.printStackTrace();
				LOG.severe("Loading file: " + s + " failed");

				continue;
			}
		}

		// ---build the score table---
		int nAssessors = assessors.size();
		int nLine = assessors.get(0).getLines().size();
		int nAlgorithms = assessors.get(0).getLines().get(0).getScores().size();

		int nRecords = nLine * nAlgorithms;

		int[][] sumTable = new int[nRecords][CATEGORY.length];

		for (Assessor a : assessors) {
			for (int posLine = 0; posLine < a.getLines().size(); posLine++) {

				// i = 0 ~ 5
				for (int posAlgorithm = 0; posAlgorithm < a.getLines()
						.get(posLine).getScores().size(); posAlgorithm++) {
					int score = a.getLines().get(posLine).getScores()
							.get(posAlgorithm);

					sumTable[posLine * nAlgorithms + posAlgorithm][score]++;
				}
			}
		}

		// ---Pi---
		double[] scoreRow = new double[nRecords];
		int posLine = 0;
		for (int[] line : sumTable) {
			double sumLine = 0.0;
			for (int count : line) {
				sumLine += count * count;
			}
			scoreRow[posLine++] = (sumLine - nAssessors)
					/ (nAssessors * (nAssessors - 1));
		}

		// ---pi---
		double[] scoreColumn = new double[CATEGORY.length];
		for (int posColumn = 0; posColumn < CATEGORY.length; posColumn++) {
			double sumColumn = 0.0;
			for (int[] line : sumTable) {
				sumColumn += line[posColumn];
			}
			scoreColumn[posColumn] = sumColumn / (nAssessors * nRecords);
		}

		// ---\bar{P}---
		double scorePBar = 0.0;
		for (double score : scoreRow) {
			scorePBar += score;
		}
		scorePBar /= nRecords;

		// ---P_e---
		double scorePE = 0.0;
		for (double score : scoreColumn) {
			scorePE += score * score;
		}

		// ---k_f---
		double scoreKappaF = (scorePBar - scorePE) / (1 - scorePE);

		System.out.println(scoreKappaF);
	}

	private static Assessor loadFile(String file) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line = "";
		Assessor ret = new Assessor();
		ret.setName(file);

		while ((line = in.readLine()) != null) {
			Score score = new Score();
			String[] scores = line.split("\t");

			for (String s : scores) {
				if (s.isEmpty()) {
					score.getScores().add(0);
				} else {
					score.getScores().add(Integer.parseInt(s.trim()));
				}
			}
			ret.getLines().add(score);
		}

		return ret;
	}

}
