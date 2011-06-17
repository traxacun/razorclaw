package razorclaw.evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

import razorclaw.object.APIMeta;
import razorclaw.object.Webpage;
import razorclaw.object.WebpageMeta;

/**
 * This app identifies languages in top 1000 .tk domains
 * 
 * @author Shuai Yuan
 * 
 */
public class LanguageIdentifier {
	class DomainList {
		public ArrayList<String> domains;
	}

	private static final Logger LOG = Logger.getLogger(LanguageIdentifier.class
			.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int counter = 0;

		try {
			// get all meta data from dot.tk API
			// URLConnection conn = (new URL("http://www.stats.tk/ucl/top100"))
			// .openConnection();
			// conn.setConnectTimeout(0);
			// conn.setReadTimeout(0);
			// conn.setRequestProperty("UserAgent", "razorclaw");
			//
			// String s, content = "";
			// BufferedReader in = new BufferedReader(new InputStreamReader(
			// conn.getInputStream()));
			// while ((s = in.readLine()) != null) {
			// content += s;
			// }
			// // parse the response
			// DomainList domains = JSON.decode(content, DomainList.class);

			BufferedReader in = new BufferedReader(new FileReader(
					"data/selected_vn.txt"));

			BufferedWriter out = new BufferedWriter(new FileWriter(
					"data/2nd_vn_estimation.txt"));
			String s = "";
			ArrayList<String> domains = new ArrayList<String>();
			while ((s = in.readLine()) != null) {
				domains.add(s);
			}

			for (String domain : domains) {
				Webpage web = new Webpage();
				WebpageMeta meta = new WebpageMeta();
				APIMeta api = razorclaw.tk.StatsAPI.crawl(domain);

				web.setAPIMeta(api);
				web.setWebpageMeta(meta);

				try {
					web = razorclaw.crawler.HTMLCrawler.crawl(web);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}

				String lang = detectLanguage(web.getText());

				out.write(domain);
				out.write("\t");
				out.write(lang);
				out.newLine();
				out.flush();

				counter++;

				if (counter % 10 == 0) {
					LOG.info(counter + " domains loaded");
				}
			}

			LOG.info("Loading webpages completed");
		} catch (Exception e) {
			// timeout exception
			e.printStackTrace();

			LOG.severe("Parsing top1000 list failed");
		}

		LOG.info("Parsing top1000 list finished");
	}

	private static String detectLanguage(String text) {
		try {
			DetectorFactory.loadProfile("lang-profiles/");
		} catch (Exception e) {
			// profiles already loaded
		}
		try {
			Detector detector = DetectorFactory.create();
			detector.append(text);

			String lang = detector.detect();

			return lang;
		} catch (LangDetectException e) {
			LOG.warning("Un-recongnized language. Using English as default.");

			return "en";
		}
	}
}
