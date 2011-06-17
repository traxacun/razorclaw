package razorclaw.evaluation.razorclaw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * this class combines reward output and raw score output for later regression
 * computation
 * 
 * @author Shuai Yuan
 * 
 */
public class PrepareResult {
	private static final String REWARD_SCORE = "data/1st/en_reward.txt";

	private static final String RAW_SCORE = "data/1st/en_raw.txt";

	private static final String OUTPUT_FILE = "data/1st/en_reward_raw.txt";

	private static final Logger LOG = Logger.getLogger(PrepareResult.class
			.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BufferedReader inReward = new BufferedReader(new FileReader(
					REWARD_SCORE));
			BufferedReader inRaw = new BufferedReader(new FileReader(RAW_SCORE));
			BufferedWriter out = new BufferedWriter(new FileWriter(OUTPUT_FILE));

			String line = "";
			String rewardDomain = "", rawDomain = "";

			while ((line = inReward.readLine()) != null) {
				String[] array = line.split("\t");
				rewardDomain = array[0];
				String reward = array[1];

				// first time
				if (rawDomain.isEmpty()) {
					rawDomain = inRaw.readLine();
				}

				while ((line = inRaw.readLine()) != null) {
					if (line.split("\t").length == 1) {
						rawDomain = line.trim();

						break;
					} else {
						out.write(rewardDomain + "\t" + reward + "\t" + line);
						out.newLine();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();

			LOG.severe("Opening files failed");
		}
	}

}
