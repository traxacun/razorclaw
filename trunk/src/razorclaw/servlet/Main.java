package razorclaw.servlet;

import java.io.IOException;
import java.net.URISyntaxException;
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

	// input
	private String _domain;

	private Status _status;

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
		if ((_domain = req.getParameter("domain")) == null) {
			LOG.severe("Wrong parameter \"domain\"");
			return;
		}

		// CrawlTaskHandler.createCrawlTask(req.getParameter("domain"));
		_domain = _domain.toLowerCase();

		try {
			setStatus(Status.CRAWLING);
			// ------------crawl part-----------------
			Webpage web = new Webpage();
			APIMeta apiMeta = StatsAPI.crawl(_domain);
			WebpageMeta webpageMeta = new WebpageMeta();
			web.setWebpageMeta(webpageMeta);
			web.setAPIMeta(apiMeta);

			web = HTMLCrawler.crawl(web);

			setStatus(Status.CRAWLED);
			// -------------parse part----------------
			setStatus(Status.PARSING);

			// detect language
			String lang = TextUtils.detectLanguage(web.getText());
			web.getWebpageMeta().setLanguage(lang);

			web.parse();

			// --------always save inverse document index ------------
			// LOG.info("Saving inverse document index");
			// saveIndex(web);

			// if (req.getParameter("buildIndex") != null
			// && req.getParameter("buildIndex").equals("1")) {
			//
			// } else {
			// -------------rank part-----------------
			setStatus(Status.RANKING);

			LOG.info("Ranking phrases");
			BM25F.rank(web.getWebpageMeta(), web.getAPIMeta(),
					web.getPhraseMap());

			setStatus(Status.RANKED);
			// -------------sort and output------------
			KeyPhrase keyPhrase = sort(web.getPhraseMap());

			System.out.println(keyPhrase.getKeyphrase());

			String result = JSON.encode(keyPhrase);
			resp.setContentType("text/html; charset=UTF-8");
			resp.setCharacterEncoding("UTF-8");

			resp.getWriter().println(result);

			setStatus(Status.FINISHED);
			// }
			// } catch (CacheException e) {
			// LOG.severe("Memcached failed");
			// LOG.severe("Generating keyphrase failed");
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
	private KeyPhrase sort(HashMap<String, PhraseProperty> phraseMap) {
		List<Entry<String, PhraseProperty>> phrases = new LinkedList<Entry<String, PhraseProperty>>(
				phraseMap.entrySet());
		Collections.sort(phrases, new UniversalComparator());

		// System.out.println(phrases);

		// get the top 3
		KeyPhrase keyPhrase = new KeyPhrase();
		keyPhrase.setDomain(_domain);

		for (int i = 0; i < 3 && i < phrases.size(); i++) {
			keyPhrase.appendKeyPhrase(phrases.get(i).getKey());
		}

		return keyPhrase;
	}

	// -----------getters and setters------------------
	public void setStatus(Status _status) {
		this._status = _status;
	}

	public Status getStatus() {
		return _status;
	}

}
