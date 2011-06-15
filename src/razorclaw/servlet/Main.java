package razorclaw.servlet;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;
import net.sf.jsr107cache.CacheException;
import razorclaw.crawler.HTMLCrawler;
import razorclaw.datastore.DomainStoreHandler;
import razorclaw.datastore.PhraseStoreHandler;
import razorclaw.object.APIMeta;
import razorclaw.object.Dictionaries.Status;
import razorclaw.object.KeyPhraseScore;
import razorclaw.object.PhraseProperty;
import razorclaw.object.Webpage;
import razorclaw.object.WebpageMeta;
import razorclaw.object.KeyPhrase;
import razorclaw.ranker.BM25F;
import razorclaw.ranker.UniversalComparator;
import razorclaw.tk.StatsAPI;
import razorclaw.util.TextUtils;

public class Main extends HttpServlet {
	private static final long serialVersionUID = 3455350955823075513L;

	private static final Logger LOG = Logger.getLogger(Main.class.getName());

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doExecute(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doExecute(req, resp);
	}

	public void doExecute(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String domain = "";
		if ((domain = req.getParameter("domain")) == null) {
			LOG.severe("Wrong parameter \"domain\"");
			return;
		}

		// CrawlTaskHandler.createCrawlTask(req.getParameter("domain"));
		domain = domain.toLowerCase();

		try {
			// ------------crawl part-----------------
			Webpage web = new Webpage();
			APIMeta apiMeta = StatsAPI.crawl(domain);
			WebpageMeta webpageMeta = new WebpageMeta();
			web.setWebpageMeta(webpageMeta);
			web.setAPIMeta(apiMeta);

			web = HTMLCrawler.crawl(web);
			// -------------parse part----------------

			// detect language
			// String lang = TextUtils.detectLanguage(web.getText());
			// web.getWebpageMeta().setLanguage(lang);

			// TODO force parser
			web.getWebpageMeta().setLanguage("en");

			web.parse();
			// -------------rank-----------------
			ArrayList<KeyPhraseScore> rankResult = BM25F.rank(
					web.getWebpageMeta(), web.getAPIMeta(), web.getPhraseMap());
			// -------------sort and output------------
			ArrayList<KeyPhraseScore> sortResult = sort(rankResult);

			KeyPhrase result = new KeyPhrase();
			for (KeyPhraseScore kps : sortResult) {
				result.appendKeyPhrase(kps.getPhrase());
			}

			// console output for review
			System.out.println(result.getKeyphrase());

			resp.setContentType("text/html; charset=UTF-8");
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().println(JSON.encode(result));
		} catch (IOException e) {
			LOG.severe("Crawling stats.tk or webpage failed");
			LOG.severe("Generating keyphrase failed");
		} catch (URISyntaxException e) {
			LOG.severe("Wrong URL format");
			LOG.severe("Generating keyphrase failed");
		}
	}

	/**
	 * sort and generate keyphrases
	 * 
	 * @param phraseMap
	 */
	private ArrayList<KeyPhraseScore> sort(List<KeyPhraseScore> rankResult) {
		Collections.sort(rankResult, new UniversalComparator());

		ArrayList<KeyPhraseScore> ret = new ArrayList<KeyPhraseScore>();

		// get the top 5
		for (int i = 0; i < 5 && i < rankResult.size(); i++) {
			ret.add(rankResult.get(i));
		}

		return ret;
	}
}
