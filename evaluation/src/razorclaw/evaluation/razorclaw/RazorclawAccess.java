package razorclaw.evaluation.razorclaw;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.arnx.jsonic.JSON;
import razorclaw.evaluation.storage.StorageHandler;
import razorclaw.object.KeyPhrase;

public class RazorclawAccess {
	private static final Logger LOG = Logger.getLogger(RazorclawAccess.class
			.getName());

	private static final String RAZORCLAW_URL = "http://localhost:8888/razorclaw?domain=";

	// private static final String BASE_PATH = "data/full-list/kea-en";

	private static final String OUTPUT_FILE = "data/2nd/razorclaw_ru.txt";

	// @formatter:off
	private static final String[] DOMAINS = { 
		"phperweb.tk",
	};
	
	private static final double[][] X = {
		{71.65610263,
		0,
		0,
		0,
		0,
		0,
		68.51351351,
		0,
		0,
		0,
		0,
		3.142589118,
		0,
		0,
		71.65610263}
	};
	// @formatter:on

	// private static final String RAZORCLAW_URL =
	// "http://workers.tkrazorclaw.appspot.com/razorclaw?domain=";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// File f = new File(BASE_PATH);
		ArrayList<String> results = new ArrayList<String>();

		int count = 0;
		for (String domain : DOMAINS) {
			// domain = domain.substring(0, domain.length() - 4) + ".tk";

			URLConnection conn;
			String s, content = "";

			String wrapper = domain + "\t";
			try {
				conn = (new URL(RAZORCLAW_URL + domain)).openConnection();
				conn.setConnectTimeout(0);
				conn.setReadTimeout(0);
				conn.setRequestProperty("UserAgent", "razorclaw");

				BufferedReader urlReader = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));
				while ((s = urlReader.readLine()) != null) {
					content += s;
				}
				// parse the response
				KeyPhrase res = JSON.decode(content, KeyPhrase.class);

				if (res != null) {
					wrapper += res.getKeyphrase();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				LOG.warning("Wrong domain format");
			} catch (Exception e) {
				e.printStackTrace();
				LOG.warning("Accessing razorclaw failed");
			} finally {
				results.add(wrapper);

				LOG.info(wrapper);

				if (count % 10 == 0) {
					LOG.info(count + " domains processed");
				}
				count++;
			}
		}

		StorageHandler.saveResult(results, OUTPUT_FILE);
	}
}
