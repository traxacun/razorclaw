package razorclaw.evaluation.yahoo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;

import net.arnx.jsonic.JSON;

import razorclaw.evaluation.storage.StorageHandler;
import razorclaw.object.APIMeta;
import razorclaw.object.KeyPhrase;
import razorclaw.object.Webpage;
import razorclaw.object.WebpageMeta;

public class YahooAccess {
	private static final Logger LOG = Logger.getLogger("Yahoo");

	private static final String ACCESS_URI = "http://search.yahooapis.com/ContentAnalysisService/V1/termExtraction";

	private static final String APP_ID = "RDtsYqvV34GSmnZprRxRkObbOlZg.oaNjiF7sx4EsjlDONBHu8btkq5tn_EPNhmrp5X3xXS0os5YywW864ByA673F_DGT7k-";

	private static final String BASE_PATH = "data/full-list/kea-ru/";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// load list of domains
		File f = new File(BASE_PATH);

		int count = 0;
		ArrayList<String> results = new ArrayList<String>();

		for (String file : f.list()) {
			String s = "", content = "";
			String wrapper = file.substring(0, file.length() - 4) + ".tk\t";

			try {
				BufferedReader in = new BufferedReader(new FileReader(BASE_PATH
						+ file));
				while ((s = in.readLine()) != null) {
					content += s;
				}

				// assemble request
				Form form = new Form();
				form.add("output", "json");
				form.add("appid", APP_ID);
				form.add("context", content);

				Client client = Client.create();
				WebResource resource = client.resource(ACCESS_URI);

				// access the term extraction service
				// String s, content = "";
				// BufferedReader in = new BufferedReader(new InputStreamReader(
				// conn.getInputStream()));
				// while ((s = in.readLine()) != null) {
				// content += s;
				// }
				// parse the response
				String keyPhrases = "";

				String text = resource.post(String.class, form);

				JSONObject obj = new JSONObject(text);
				JSONArray resultSet = ((JSONObject) obj.get("ResultSet"))
						.getJSONArray("Result");
				for (int i = 0; i < resultSet.length() && i < 3; i++) {
					keyPhrases += resultSet.get(i).toString() + ", ";
				}
				if (keyPhrases != null && !keyPhrases.isEmpty()) {
					wrapper += keyPhrases.substring(0, keyPhrases.length() - 2);
				}
			} catch (JSONException e) {
				e.printStackTrace();

				LOG.severe("Parsing response failed");
				continue;
			} catch (Exception e) {
				e.printStackTrace();

				LOG.severe("Accessing Yahoo service failed");
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
		StorageHandler.saveResult(results, "data/2nd/yahoo_ru.txt");

		// output run time
		// LOG.info("Running time: " + StorageHandler.getRunTime() +
		// " seconds");
		// LOG.info(StorageHandler.getRunTime() / count
		// + " seconds per domain on average");
	}
}
