package razorclaw.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;
import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import razorclaw.datastore.DomainStoreHandler;
import razorclaw.datastore.PhraseStoreHandler;
import razorclaw.object.APIMeta;
import razorclaw.object.APIMeta.RefererAnchorText;
import razorclaw.object.Dictionaries.PartOfSpeech;
import razorclaw.object.Dictionaries.Status;
import razorclaw.object.PhraseProperty;
import razorclaw.object.Webpage;
import razorclaw.object.WebpageMeta;
import razorclaw.parser.CJKVTokenizer;
import razorclaw.parser.OpenNLPPOSTagger;
import razorclaw.parser.OpenNLPTokenizer;
import razorclaw.parser.StopwordsHandler;
import razorclaw.parser.stemmer.SremovalStemmer;
import razorclaw.ranker.BM25F;
import razorclaw.tk.StatsAPI;
import razorclaw.util.TextUtils;

@SuppressWarnings("serial")
public class Main extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(Main.class.getName());

	private String _domain;
	/**
	 * parsed DOM for the html content
	 */
	private Document _doc;

	private Webpage _webpage;

	private WebpageMeta _webpageMeta;

	private APIMeta _apiMeta;
	/**
	 * holds phrases of the _webpage
	 */
	private HashMap<String, PhraseProperty> _phrases;

	private Cache _crawlCache;

	private Status _status;

	private KeyPhrase _keyPhrase;

	/**
	 * response the KeyPhrase in JSON
	 * 
	 * @author Shuai YUAN
	 * 
	 */
	class KeyPhrase {
		private String _domain;
		private String _keyphrase;

		public void setDomain(String _domain) {
			this._domain = _domain;
		}

		public String getDomain() {
			return _domain;
		}

		public void setKeyphrase(String _keyphrase) {
			this._keyphrase = _keyphrase;
		}

		public String getKeyphrase() {
			return _keyphrase;
		}

		public void appendKeyPhrase(String s) {
			if (_keyphrase == null || _keyphrase.isEmpty()) {
				setKeyphrase(s);
			} else {
				_keyphrase += ",";
				_keyphrase += s;
			}
		}
	}

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
			_status = Status.CRAWLING;
			// ------------crawl part-----------------
			_webpage = new Webpage();

			_apiMeta = StatsAPI.crawl(_domain);
			if (_apiMeta == null) {
				_status = Status.FAILED;
				return;
			}

			if (true == checkCache()) {
				loadCache();
			} else {
				crawlWebpage();
				saveCache();
			}
			_status = Status.CRAWLED;
			// -------------parse part----------------
			_status = Status.PARSING;
			_webpageMeta = new WebpageMeta();
			_phrases = new HashMap<String, PhraseProperty>();

			// detect language
			String lang = TextUtils.detectLanguage(_webpage.getText());
			_webpageMeta.setLanguage(lang);

			parseHTML();

			parseProperties();

			if (_status == Status.FAILED) {
				return;
			}

			// --------always save inverse document index ------------
			LOG.info("Saving inverse document index");
			saveIndex();

			if (req.getParameter("buildIndex") != null
					&& req.getParameter("buildIndex").equals("1")) {

			} else {
				// -------------rank part-----------------
				_status = Status.RANKING;

				LOG.info("Ranking phrases");
				BM25F.rank(_webpageMeta, _apiMeta, _phrases);

				_status = Status.RANKED;
				// -------------sort and output------------
				sort();

				String result = JSON.encode(_keyPhrase);
				resp.setContentType("text/html; charset=UTF-8");
				resp.setCharacterEncoding("UTF-8");

				resp.getWriter().println(result);

				_status = Status.FINISHED;
			}

		} catch (Exception e) {
			LOG.severe("Generating keyphrase failed");
			logStackTrace(e);
		}
	}

	/**
	 * some users setup multiple dot.tk domains referring to the same URL. check
	 * if the destination webpage exists in the cache, using forwardURL as the
	 * key
	 */
	private boolean checkCache() {
		LOG.info("Check crawling cache");

		try {
			if ((_crawlCache = CacheManager.getInstance().getCache(
					"crawl_cache")) == null) {
				_crawlCache = CacheManager.getInstance().getCacheFactory()
						.createCache(Collections.emptyMap());
				// cache.put("crawl_cache", new HashMap<String, String>());
				CacheManager.getInstance().registerCache("crawl_cache",
						_crawlCache);
			}
			return _crawlCache.containsKey(_apiMeta.getForwardURL());
		} catch (CacheException e) {
			LOG.warning("Check crawling cache failed");
			logStackTrace(e);
			return false;
		}
	}

	/**
	 * load a webpage from memcache if it's already crawled
	 */
	private void loadCache() {
		LOG.info("Loading webpage cache");

		try {
			if ((_crawlCache = CacheManager.getInstance().getCache(
					"crawl_cache")) == null) {
				_crawlCache = CacheManager.getInstance().getCacheFactory()
						.createCache(Collections.emptyMap());
				// cache.put("crawl_cache", new HashMap<String, String>());
				CacheManager.getInstance().registerCache("crawl_cache",
						_crawlCache);
			}
			_webpage = (Webpage) _crawlCache.get("__crawl__"
					+ _apiMeta.getForwardURL());
		} catch (CacheException e) {
			LOG.severe("Loading webpage cache failed");
			logStackTrace(e);
		}
	}

	/**
	 * save the crawl result to memcache for parse, key=_forwardURL,
	 * value=_webpage
	 */
	private void saveCache() {
		LOG.info("Saving crawl result for next step");

		try {
			CacheFactory cacheFactory = CacheManager.getInstance()
					.getCacheFactory();
			_crawlCache = cacheFactory.createCache(Collections.emptyMap());

			_crawlCache.put("__crawl__" + _apiMeta.getForwardURL(), _webpage);
		} catch (Exception e) {
			LOG.severe("Saving crawl result to cache failed");
			logStackTrace(e);
		}
	}

	/**
	 * crawl HTML using _forwardURL instead of _domain
	 */
	private void crawlWebpage() {
		LOG.info("Crawling webpage");

		try {
			// to deal with special characters
			URL url = new URL(_apiMeta.getForwardURL());
			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(),
					url.getQuery(), null);

			URLConnection conn = (new URL(uri.toString())).openConnection();

			conn.setConnectTimeout(0);
			conn.setReadTimeout(0);
			conn.setRequestProperty("UserAgent", "Chrome 11");

			byte[] byteChunk = new byte[4096];
			ByteArrayOutputStream webpageContent = new ByteArrayOutputStream();

			InputStream in = conn.getInputStream();
			while (in.read(byteChunk) > 0) {
				webpageContent.write(byteChunk);
			}

			_doc = Jsoup.parse(webpageContent.toString()); // parse using
															// default charset

			// detect charset
			Elements meta = _doc.getElementsByTag("meta");
			String charset = "";
			for (Element e : meta) {
				if (e.hasAttr("http-equiv") && e.hasAttr("content")
						&& e.attr("http-equiv").equals("Content-Type")) {
					String s = e.attr("content");
					charset = s.substring(s.indexOf("charset=") + 8);
					break;
				}
			}
			if (!charset.isEmpty() && !charset.equalsIgnoreCase("UTF-8")) {
				String content = new String(webpageContent.toString(charset));

				_doc = Jsoup.parse(content); // parse using given charset
			}

			// store HTML instead of DOM which is not serializable
			_webpage.setHtml(_doc.html());
			_webpage.setText(_doc.text());
		} catch (Exception e) {
			LOG.severe("Crawling webpage failed");
			logStackTrace(e);
		}
	}

	/**
	 * parse HTML content and construct _phrases
	 */
	private void parseHTML() {
		LOG.info("Parsing HTML");

		_status = Status.PARSING;
		try {
			PhraseProperty property;
			_webpageMeta.parseMeta(_doc);

			// check the language to load corresponding model
			String lang = _webpageMeta.getLanguage();

			if (lang == null) {
				LOG.severe("Not supported language");

				_status = Status.FAILED;
			} else if (lang.equals("da") || lang.equals("de")
					|| lang.equals("en") || lang.equals("nl")
					|| lang.equals("pt") || lang.equals("se")) {
				// supported by opennlp

				// tokenize to phrases
				// load body
				Element body = _doc.body();

				// TODO: load proper stemmer according to languages
				SremovalStemmer stemmer = new SremovalStemmer();

				for (String p : OpenNLPTokenizer.tokenize(body.text(), lang)) {
					p = TextUtils.removePunctuation(p);
					p = TextUtils.removeNonAlphabeticChars(p);
					p = p.toLowerCase().trim();

					// check if the phrase is valid after stem
					if (p != null && !p.isEmpty()) {
						p = stemmer.stem(p);
						if (StopwordsHandler.isStopwords(p) || p.length() < 3) {
							continue;
						}
						// combine
						if (_phrases.containsKey(p)) {
							_phrases.get(p).increaseOccurance();
						} else {
							property = new PhraseProperty();
							property.setNew(true).setForwardURL(
									_apiMeta.getForwardURL());
							_phrases.put(p, property);
						}
					}
				}
			} else if (lang.equals("zh-cn") || lang.equals("zh-tw")
					|| lang.equals("ja") || lang.equals("ka")
					|| lang.equals("vi")) {
				CJKVTokenizer.feed(new StringReader(_doc.body().text()));

				for (String p = CJKVTokenizer.next(); p != null; p = CJKVTokenizer
						.next()) {
					// System.out.println(p);

					if (p != null && !p.isEmpty() && p.length() > 2) {
						// combine
						if (_phrases.containsKey(p)) {
							_phrases.get(p).increaseOccurance();
						} else {
							property = new PhraseProperty();
							property.setNew(true).setForwardURL(
									_apiMeta.getForwardURL());

							_phrases.put(p, property);
						}
					}
				}
			} else {
				LOG.severe("Not supported language");
				_status = Status.FAILED;
			}

		} catch (Exception e) {
			_status = Status.FAILED;

			LOG.severe("Parsing HTML failed");
			logStackTrace(e);
		}
	}

	private void sort() {
		try {
			List<Entry<String, PhraseProperty>> phrases = new LinkedList<Entry<String, PhraseProperty>>(
					_phrases.entrySet());
			Collections.sort(phrases, new BM25FComparator());

			// get the top 3
			_keyPhrase = new KeyPhrase();
			_keyPhrase.setDomain(_domain);

			for (int i = 0; i < 3 && i < phrases.size(); i++) {
				_keyPhrase.appendKeyPhrase(phrases.get(i).getKey());
			}
		} catch (Exception e) {
			LOG.severe("Sort failed");
			logStackTrace(e);
		}
	}

	/**
	 * check features for phrases in title, meta, anchor text, user/admin/spider
	 * keywords
	 */
	private void parseProperties() {
		SremovalStemmer stemmer = new SremovalStemmer();
		PhraseProperty property;

		// ---------title------------
		for (String s : _webpageMeta.getTitle()) {
			s = TextUtils.removePunctuation(s);
			s = TextUtils.removeNonAlphabeticChars(s);
			s = s.toLowerCase().trim();
			if (s != null && !s.isEmpty()) {
				s = stemmer.stem(s);
				if (s == null || s.isEmpty() || StopwordsHandler.isStopwords(s)
						|| s.length() < 3) {

				} else {
					if (_phrases.containsKey(s)) {
						_phrases.get(s).setTitle(true).increaseOccurance();
					} else { // not exists
						property = new PhraseProperty();
						property.setTitle(true).setNew(true)
								.setForwardURL(_apiMeta.getForwardURL());

						_phrases.put(s, property);
					}
				}
			}
		}
		// ------------metaKeywords-------------
		for (String s : _webpageMeta.getKeywords()) {
			// s = TextUtils.removePunctuation(s);
			// s = TextUtils.removeNonAlphabeticChars(s);
			s = s.toLowerCase().trim();
			if (s != null && !s.isEmpty()) {
				s = stemmer.stem(s);
				if (s == null || s.isEmpty() || StopwordsHandler.isStopwords(s)
						|| s.length() < 3) {

				} else {
					if (_phrases.containsKey(s)) {
						_phrases.get(s).setMetaKeywords(true)
								.increaseOccurance();
					} else { // not exists
						property = new PhraseProperty();
						property.setMetaKeywords(true).setNew(true)
								.setForwardURL(_apiMeta.getForwardURL());

						_phrases.put(s, property);
					}
				}
			}
		}
		// -------------metaDescription--------------
		for (String s : _webpageMeta.getDescription()) {
			// s = TextUtils.removePunctuation(s);
			// s = TextUtils.removeNonAlphabeticChars(s);
			s = s.toLowerCase().trim();
			if (s != null && !s.isEmpty()) {
				s = stemmer.stem(s);
				if (s == null || s.isEmpty() || StopwordsHandler.isStopwords(s)
						|| s.length() < 3) {

				} else {
					if (_phrases.containsKey(s)) {
						_phrases.get(s).setMetaDescription(true)
								.increaseOccurance();
					} else { // not exists
						property = new PhraseProperty();
						property.setMetaDescription(true).setNew(true)
								.setForwardURL(_apiMeta.getForwardURL());

						_phrases.put(s, property);
					}
				}
			}
		}
		// -------------anchor text--------------------
		for (RefererAnchorText text : _apiMeta.getRefererAnchorTexts()) {
			String s = text.getAnchorText();
			// s = TextUtils.removePunctuation(s);
			// s = TextUtils.removeNonAlphabeticChars(s);
			s = s.toLowerCase().trim();
			if (s != null && !s.isEmpty()) {
				s = stemmer.stem(s);
				if (s == null || s.isEmpty() || StopwordsHandler.isStopwords(s)
						|| s.length() < 3) {

				} else {
					if (_phrases.containsKey(s)) {
						_phrases.get(s).setAnchorText(true).increaseOccurance();
					} else { // not exists
						property = new PhraseProperty();
						property.setAnchorText(true).setNew(true)
								.setForwardURL(_apiMeta.getForwardURL());

						_phrases.put(s, property);
					}
				}
			}
		}
		// ------------------spider keywords-----------------
		for (String s : _apiMeta.getSpiderKeywords()) {
			// s = TextUtils.removePunctuation(s);
			// s = TextUtils.removeNonAlphabeticChars(s);
			s = s.toLowerCase().trim();
			if (s != null && !s.isEmpty()) {
				s = stemmer.stem(s);
				if (s == null || s.isEmpty() || StopwordsHandler.isStopwords(s)
						|| s.length() < 3) {

				} else {
					if (_phrases.containsKey(s)) {
						_phrases.get(s).setSpiderKeywords(true)
								.increaseOccurance();
					} else { // not exists
						property = new PhraseProperty();
						property.setSpiderKeywords(true).setNew(true)
								.setForwardURL(_apiMeta.getForwardURL());

						_phrases.put(s, property);
					}
				}
			}
		}
		// ------------------admin keywords-----------------
		for (String s : _apiMeta.getAdminKeywords()) {
			// s = TextUtils.removePunctuation(s);
			// s = TextUtils.removeNonAlphabeticChars(s);
			s = s.toLowerCase().trim();
			if (s != null && !s.isEmpty()) {
				s = stemmer.stem(s);
				if (s == null || s.isEmpty() || StopwordsHandler.isStopwords(s)
						|| s.length() < 3) {

				} else {
					if (_phrases.containsKey(s)) {
						_phrases.get(s).setAdminKeywords(true)
								.increaseOccurance();
					} else { // not exists
						property = new PhraseProperty();
						property.setAdminKeywords(true).setNew(true)
								.setForwardURL(_apiMeta.getForwardURL());

						_phrases.put(s, property);
					}
				}
			}
		}
		// ------------------user keywords-----------------
		for (String s : _apiMeta.getUserKeywords()) {
			// s = TextUtils.removePunctuation(s);
			// s = TextUtils.removeNonAlphabeticChars(s);
			s = s.toLowerCase().trim();
			if (s != null && !s.isEmpty()) {
				s = stemmer.stem(s);
				if (s == null || s.isEmpty() || StopwordsHandler.isStopwords(s)
						|| s.length() < 3) {

				} else {
					if (_phrases.containsKey(s)) {
						_phrases.get(s).setUserKeywords(true)
								.increaseOccurance();
					} else { // not exists
						property = new PhraseProperty();
						property.setUserKeywords(true).setNew(true)
								.setForwardURL(_apiMeta.getForwardURL());

						_phrases.put(s, property);
					}
				}
			}
		}

		// tag part-of-speech, TF
		// @formatter:off
		for (Entry<String, PhraseProperty> e : _phrases.entrySet()) {
			e.getValue()
					.setPartOfSpeech(
							PartOfSpeech.load(OpenNLPPOSTagger.getWordTag(e
									.getKey())))
					.setTFScore(
							(double) e.getValue().getOccurance()
									/ _phrases.size());
		}
		// @formatter:on

		_status = Status.PARSED;
	}

	/**
	 * save the inverse document index to datastore
	 */
	private void saveIndex() {
		try {
			// save the phrases - forwardURL
			for (Entry<String, PhraseProperty> e : _phrases.entrySet()) {
				PhraseStoreHandler.put(e.getKey(), e.getValue());
			}

			// save the webpage to datastore
			DomainStoreHandler.put(_apiMeta.getForwardURL(), _domain,
					_webpageMeta.getLanguage());

		} catch (Exception e) {
			LOG.severe("Save inverse document index failed");
			logStackTrace(e);
		}
	}

	private void logStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		LOG.info(sw.toString());
	}

	/**
	 * sort Entry<String, PhraseProperty> in the descending order
	 * 
	 * @author Shuai YUAN
	 * 
	 */
	class BM25FComparator implements Comparator<Entry<String, PhraseProperty>> {
		@Override
		public int compare(Entry<String, PhraseProperty> arg0,
				Entry<String, PhraseProperty> arg1) {
			if (arg0.getValue().getBM25FScore()
					- arg1.getValue().getBM25FScore() > 0) {
				return -1; // descending order
			} else if (arg0.getValue().getBM25FScore()
					- arg1.getValue().getBM25FScore() < 0) {
				return 1;
			} else {
				if (arg0.getValue().getOccurance() > arg1.getValue()
						.getOccurance()) {
					return -1;
				} else if (arg0.getValue().getOccurance() < arg1.getValue()
						.getOccurance()) {
					return 1;
				} else {
					return 0;
				}
			}
		}
	}
}