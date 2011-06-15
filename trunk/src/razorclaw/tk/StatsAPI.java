package razorclaw.tk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import net.arnx.jsonic.JSON;
import razorclaw.exception.APICrawlException;
import razorclaw.object.APIMeta;

public class StatsAPI {
	private static final String API_BASE = "http://www.stats.tk/ucl/domain?domain=";

	private static final Logger LOG = Logger
			.getLogger(StatsAPI.class.getName());

	public static APIMeta crawl(String domain) {
		LOG.info("Crawling metadata from stats.tk API");

		String forwardURL = "";
		APIMeta apiMeta = new APIMeta();

		// to survive the time-out exception
		for (int i = 0; forwardURL != null && (i < 3 && forwardURL.isEmpty()); i++) {
			try {
				// get all meta data from dot.tk API
				URLConnection conn = (new URL(API_BASE + domain))
						.openConnection();
				conn.setConnectTimeout(0);
				conn.setReadTimeout(0);
				conn.setRequestProperty("UserAgent", "razorclaw");

				String s, content = "";
				BufferedReader in = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				while ((s = in.readLine()) != null) {
					content += s;
				}
				// parse the response
				apiMeta = JSON.decode(content, APIMeta.class);
				// would be none if user using own DNS
				forwardURL = apiMeta.getForwardURL();
			} catch (IOException e) {
				// timeout exception
				LOG.warning("Crawling metadata from stats.tk API failed");
			} finally {
				apiMeta.setDomainname(domain);
			}
		}

		// wrong format API response. user is using own DNS.
		if (forwardURL == null || forwardURL.isEmpty()) {
			LOG.warning("Using domain as the forwardURL");
			apiMeta.setForwardurl("http://" + domain);
		}

		LOG.info("ForwardURL parsed: " + domain + ", " + forwardURL);
		return apiMeta;
	}

}
