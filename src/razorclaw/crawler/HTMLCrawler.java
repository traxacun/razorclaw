package razorclaw.crawler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

import razorclaw.datastore.DomainStoreHandler;
import razorclaw.object.Webpage;

public class HTMLCrawler {
	private static final Logger LOG = Logger.getLogger(HTMLCrawler.class
			.getName());

	private static String _charset;

	public static Webpage crawl(Webpage web) throws URISyntaxException,
			IOException {
		String targetURL = web.getAPIMeta().getForwardURL();
		LOG.info("Crawling webpage: " + targetURL);

		// if (checkCache(targetURL)) {
		// return loadCache(targetURL);
		// }

		// reform to deal with special characters
		URL url = new URL(targetURL);
		URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(),
				url.getQuery(), null);

		URLConnection conn = (new URL(uri.toString())).openConnection();
		conn.setConnectTimeout(30000); // 10 sec in GAE
		conn.setReadTimeout(30000);
		conn.setRequestProperty("UserAgent", "Chrome 11");

		byte[] byteChunk = new byte[4096];
		ByteArrayOutputStream webpageContent = new ByteArrayOutputStream();

		InputStream in = conn.getInputStream();
		while (in.read(byteChunk) > 0) {
			webpageContent.write(byteChunk);
		}

		// try default charset first(UTF-8)
		Document doc = Jsoup.parse(webpageContent.toString());

		// detect charset
		Elements meta = doc.getElementsByTag("meta");
		_charset = "";
		for (Element e : meta) {
			if (e.hasAttr("content") && e.attr("content").contains("charset=")) {
				String s = e.attr("content").substring(
						e.attr("content").indexOf("charset=") + 8);

				if (s != null && !s.isEmpty()) {
					s = s.trim();
					if (s.substring(s.length() - 1).equals(";")) {
						s = s.substring(s.length() - 1);
					}
					_charset = s;
					web.getWebpageMeta().setCharset(s);
					break;
				}
			}
		}
		if (_charset.isEmpty()) {
			LOG.warning("Un-identified charset from HTML");

			nsDetector det = new nsDetector(nsDetector.ALL);
			det.Init(web);
			det.DoIt(webpageContent.toByteArray(), webpageContent.size(), false);
			det.DataEnd();

			// could be null!
			_charset = web.getWebpageMeta().getCharset();

			LOG.warning("Trying with charset: " + _charset);
		}
		if (_charset != null && !_charset.equalsIgnoreCase("UTF-8")) {
			String content = new String(webpageContent.toString(_charset));
			// parse using given charset
			doc = Jsoup.parse(content);
		}
		web.setHtml(doc.html());
		web.setText(doc.text());

		// saveCache(targetURL, web);

		// the domain is valid, save to datestore
		// DomainStoreHandler.saveDomain(web.getAPIMeta().getDomainName());

		return web;
	}

	/**
	 * save the crawl result to memcache for parse, key=_forwardURL,
	 * value=_webpage
	 * 
	 * @throws CacheException
	 */
	private static void saveCache(String targetURL, Webpage web)
			throws CacheException {
		LOG.info("Saving webpage to cache");

		Cache crawlCache = CacheManager.getInstance().getCache("crawl_cache");
		if (crawlCache == null) {
			CacheFactory cacheFactory = CacheManager.getInstance()
					.getCacheFactory();
			crawlCache = cacheFactory.createCache(Collections.emptyMap());
		}

		crawlCache.put("__crawl__" + web.getAPIMeta().getForwardURL(), web);
	}

	/**
	 * load a webpage from memcache if it's already crawled
	 * 
	 * @throws CacheException
	 */
	private static Webpage loadCache(String targetURL) throws CacheException {
		LOG.info("Loading webpage from cache");

		// cannot be null
		Cache crawlCache = CacheManager.getInstance().getCache("crawl_cache");

		return (Webpage) crawlCache.get("__crawl__" + targetURL);
	}

	/**
	 * some users setup multiple dot.tk domains referring to the same URL. check
	 * if the destination webpage exists in the cache, using forwardURL as the
	 * key
	 */
	private static boolean checkCache(String targetURL) {
		LOG.info("Check crawling cache");

		Cache crawlCache = CacheManager.getInstance().getCache("crawl_cache");
		if (crawlCache == null) {
			return false;
		} else {
			return crawlCache.containsKey(targetURL);
		}
	}
}
