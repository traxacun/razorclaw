package razorclaw.evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;

import net.arnx.jsonic.JSON;

import razorclaw.object.APIMeta;
import razorclaw.object.KeyPhrase;
import razorclaw.object.Webpage;
import razorclaw.object.WebpageMeta;
import razorclaw.tk.StatsAPI;

public class DomainFeeder {

	private static final Logger LOG = Logger.getLogger(DomainFeeder.class
			.getName());

	private static final String DOMAIN_LIST = "data/0512.txt";

	private static final String OUTPUT = "data/0512.razorclaw";

	private static final String OUTPUT_ADMIN = "data/0512.admin";

	private static final String OUTPUT_YAHOO = "data/0512.yahoo";

	private static final String RAZORCLAW_URL = "http://tkrazorclaw.appspot.com/razorclaw?domain=";

	private static final String ACCESS_URI = "http://search.yahooapis.com/ContentAnalysisService/V1/termExtraction";

	private static final String APP_ID = "RDtsYqvV34GSmnZprRxRkObbOlZg.oaNjiF7sx4EsjlDONBHu8btkq5tn_EPNhmrp5X3xXS0os5YywW864ByA673F_DGT7k-";

	private static String crawlYahoo(String domain) {
		// get the page content
		Webpage web = new Webpage();
		APIMeta apiMeta = StatsAPI.crawl(domain);
		web.setAPIMeta(apiMeta);

		WebpageMeta webMeta = new WebpageMeta();
		web.setWebpageMeta(webMeta);

		try {
			web = razorclaw.crawler.HTMLCrawler.crawl(web);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		// assemble request
		Form form = new Form();
		form.add("output", "json");
		form.add("appid", APP_ID);
		form.add("context", web.getText());

		Client client = Client.create();
		WebResource resource = client.resource(ACCESS_URI);

		try {
			String text = resource.post(String.class, form);

			JSONObject obj = new JSONObject(text);
			JSONArray resultSet = ((JSONObject) obj.get("ResultSet"))
					.getJSONArray("Result");

			String ret = "";
			for (int i = 0; i < resultSet.length(); i++) {
				ret += resultSet.getString(i);
			}

			return ret;
		} catch (JSONException e) {
			e.printStackTrace();
			LOG.severe("Parsing response failed");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			LOG.severe("Accessing Yahoo service failed");
			return null;
		}
	}

	private static String crawlAPI(String domain) {
		APIMeta apiMeta = StatsAPI.crawl(domain);

		if (apiMeta != null) {
			return apiMeta.getAdminKeywords().toString();
		} else {
			return null;
		}
	}

	private static String crawlRazorclaw(String domain) {
		URLConnection conn;
		String s, content = "";
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
				return res.getKeyphrase();
			} else {
				return null;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// start timer
		Date timer = new Date();

		int counter = 0;

		// load the domain list
		try {
			BufferedReader in = new BufferedReader(new FileReader(DOMAIN_LIST));
			BufferedWriter outRazorclaw = new BufferedWriter(new FileWriter(
					OUTPUT));
			// BufferedWriter outAPI = new BufferedWriter(new FileWriter(
			// OUTPUT_ADMIN));
			// BufferedWriter outYahoo = new BufferedWriter(new FileWriter(
			// OUTPUT_YAHOO));

			for (String domain = in.readLine(); domain != null; domain = in
					.readLine(), counter++) {

				outRazorclaw.write(domain);
				outRazorclaw.write("\t");

				String res = "";
				if ((res = crawlRazorclaw(domain)) != null) {
					outRazorclaw.write(res);
				}

				outRazorclaw.newLine();

				outRazorclaw.flush();

				if (counter % 10 == 0) {
					LOG.info(counter
							+ " records processed. "
							+ (((new Date()).getTime() - timer.getTime()) / 1000)
							+ " seconds passed");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// output timer
		long timeCost = ((new Date()).getTime() - timer.getTime()) / 1000;

		LOG.info("Run time: " + timeCost + " seconds");
	}
}
