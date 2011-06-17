package razorclaw.evaluation.statistics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class KappaC {

	// @formatter:off
	private static final String[] INPUT_FILES = { 
		"data/2nd/kappa/2nd_cn_1.txt", 
		"data/2nd/kappa/2nd_cn_2.txt",
		"data/2nd/kappa/2nd_cn_3.txt",
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

		// ---every two of all input files
		for (int i = 0; i < INPUT_FILES.length; i++) {
			Assessor assessor1 = assessors.get(i);

			for (int j = i; j < INPUT_FILES.length; j++) {
				Assessor assessor2 = assessors.get(j);

				// ---Pa, agreement by observation---
				int nLine = assessor1.getLines().size();
				int nAlgorithms = assessor1.getLines().get(0).getScores()
						.size();
				int nRecords = nLine * nAlgorithms;

				int[][] sumTable = new int[nRecords][CATEGORY.length];

				// used for Pe
				int[][] sumChance = new int[2][CATEGORY.length];

				for (int posLine = 0; posLine < assessor1.getLines().size(); posLine++) {
					// i = 0 ~ 5
					for (int posAlgorithm = 0; posAlgorithm < assessor1
							.getLines().get(posLine).getScores().size(); posAlgorithm++) {
						int score = assessor1.getLines().get(posLine)
								.getScores().get(posAlgorithm);

						sumTable[posLine * nAlgorithms + posAlgorithm][score]++;

						sumChance[0][score]++;
					}
				}
				for (int posLine = 0; posLine < assessor2.getLines().size(); posLine++) {
					// i = 0 ~ 5
					for (int posAlgorithm = 0; posAlgorithm < assessor2
							.getLines().get(posLine).getScores().size(); posAlgorithm++) {
						int score = assessor2.getLines().get(posLine)
								.getScores().get(posAlgorithm);

						sumTable[posLine * nAlgorithms + posAlgorithm][score]++;

						sumChance[1][score]++;
					}
				}

				int sumAgreement = 0;
				for (int[] line : sumTable) {
					for (int category : line) {
						if (category == 2) {
							sumAgreement++;
						}
					}
				}

				double scorePa = (double) sumAgreement / nRecords;

				// ---Pe, agreement by chance---
				double scorePe = 0.0;
				for (int posColumn = 0; posColumn < CATEGORY.length; posColumn++) {
					scorePe += (double) (sumChance[0][posColumn] * sumChance[1][posColumn])
							/ (nRecords * nRecords);
				}

				// ---kappa_c---
				double scoreKappaC = (scorePa - scorePe) / (1 - scorePe);

				System.out.println(scoreKappaC);
			}
		}
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