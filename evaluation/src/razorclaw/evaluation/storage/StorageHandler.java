package razorclaw.evaluation.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import razorclaw.object.KeyPhrase;

public class StorageHandler {
	private static final Logger LOG = Logger.getLogger(StorageHandler.class
			.getName());

	private static final String DOMAIN_LIST = "data/1st/selected_ru.txt";

	private static long _start, _end;

	public static ArrayList<String> loadDomains() {
		return loadDomains(DOMAIN_LIST);
	}

	public static ArrayList<String> loadDomains(String file) {
		_start = (new Date()).getTime();

		ArrayList<String> ret = new ArrayList<String>();

		// load the domain list
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			for (String s = in.readLine(); s != null; s = in.readLine()) {
				ret.add(s);
			}

			LOG.info("Loaded domains: " + ret.size());
		} catch (FileNotFoundException e) {
			LOG.severe("File not found: " + DOMAIN_LIST);

			e.printStackTrace();
		} catch (IOException e) {
			LOG.severe("Read file failed");

			e.printStackTrace();
		}

		return ret;
	}

	public static void saveResult(ArrayList<String> result, String file) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));

			for (String s : result) {
				out.write(s + "\n");
			}

			out.close();
		} catch (IOException e) {
			LOG.severe("Write file failed");

			e.printStackTrace();
		} finally {
			_end = (new Date()).getTime();
		}
	}

	public static void saveResult(ArrayList<String> result) {
		// output file name
		final String OUTPUT_FILE = "data/1st/selected_ru_google_"
				+ (new Date()).getTime() + ".txt";

		saveResult(result, OUTPUT_FILE);
	}

	public static long getRunTime() {
		return _end - _start;
	}
}
