package razorclaw.evaluation.google;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import razorclaw.evaluation.storage.StorageHandler;

public class GoogleAdWordsAccess {

	private static final Logger LOG = Logger.getLogger("Google");

	private static final String BASE_PATH = "data/full-list/kea-ru";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File f = new File(BASE_PATH);
		ArrayList<String> results = new ArrayList<String>();

		int count = 0;

		for (String file : f.list()) {
			String domain = file.substring(0, file.length() - 4) + ".tk";
			String url = "http://" + domain;
			String wrapper = domain + "\t";
			try {
				String keyPhrases = AdWordsWrapper.analyze(url);

				if (keyPhrases != null && !keyPhrases.isEmpty()) {
					wrapper += keyPhrases;
				}
			} catch (Exception e) {
				LOG.severe("Accessing Google service failed");

				e.printStackTrace();
				continue;
			} finally {
				results.add(wrapper);

				count++;
				if (count % 10 == 0) {
					LOG.info(count + " domains processed");
				}

				if (count == 170) {
					break;
				}
			}
		}

		// save
		StorageHandler.saveResult(results, "data/2nd/google_ru.txt");

		// output run time
		// LOG.info("Run time: " + StorageHandler.getRunTime() + " seconds");
		// LOG.info(StorageHandler.getRunTime() / count
		// + " seconds per domain on average");
	}
}
