package razorclaw.linguistic.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/**
 * check if a word is a stopword. now works for English only.
 * 
 * @author Shuai YUAN
 * 
 */
public class StopwordsHandler {
	private static HashSet<String> _stopwords;

	private final static String EN_STOPWORDS = "model/en-stopwords.txt";

	private StopwordsHandler() {

	}

	/**
	 * load stopwords list from local txt file.
	 */
	public static void init() {
		if (_stopwords == null) {
			_stopwords = new HashSet<String>();
			try {
				BufferedReader in = new BufferedReader(new FileReader(
						EN_STOPWORDS));
				String line;
				while ((line = in.readLine()) != null) {
					_stopwords.add(line);
				}

				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static boolean isStopwords(String s) {
		if (_stopwords == null) {
			init();
		}

		return _stopwords.contains(s);
	}
}
