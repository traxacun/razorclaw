package razorclaw.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;
import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import razorclaw.datastore.DomainStoreHandler;
import razorclaw.datastore.PhraseStoreHandler;
import razorclaw.object.APIMeta;
import razorclaw.object.Dictionaries.PartOfSpeech;
import razorclaw.object.Dictionaries.Status;
import razorclaw.object.PhraseProperty;
import razorclaw.object.Webpage;
import razorclaw.object.WebpageMeta;
import razorclaw.parser.OpenNLPPOSTagger;
import razorclaw.parser.OpenNLPTokenizer;
import razorclaw.parser.PorterStemmer;
import razorclaw.parser.StopwordsHandler;
import razorclaw.parser.TextUtils;
import razorclaw.ranker.BM25F;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;

@SuppressWarnings("serial")
public class Main extends HttpServlet {
    private final String API_BASE = "http://www.stats.tk/ucl/domain?domain=";

    private static final Logger LOG = Logger.getLogger(Main.class
	    .getName());

    private String _forwardURL, _domain;
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
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
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

	    crawlAPIMeta();

	    if (true == checkCache()) {
		loadCache();
	    } else {
		crawlWebpage();
	    }
	    _status = Status.CRAWLED;
	    // -------------parse part----------------
	    _status = Status.PARSING;
	    _phrases = new HashMap<String, PhraseProperty>();
	    parseHTML();

	    _status = Status.PARSED;
	    if (req.getParameter("buildIndex") != null) {
		resp.getWriter().println("Finished");
	    } else {
		// -------------rank part-----------------
		_status = Status.RANKING;

		BM25F.rank(_webpageMeta, _phrases);

		_status = Status.RANKED;
		// -------------sort and output------------
		String result = JSON.encode(_keyPhrase);
		resp.getWriter().println(result);

		_status = Status.FINISHED;
	    }

	    // --------always save inverse document index ------------
	    saveIndex();

	} catch (Exception e) {

	}
    }

    /**
     * used to sort KeyPhrases
     * 
     * @author Shuai YUAN
     * 
     */
    class BM25FComparator implements Comparator<PhraseProperty> {

	@Override
	public int compare(PhraseProperty arg0, PhraseProperty arg1) {
	    if (arg0.getBM25FScore() - arg1.getBM25FScore() > 0) {
		return 1;
	    } else if (arg0.getBM25FScore() - arg1.getBM25FScore() < 0) {
		return -1;
	    } else {
		return 0;
	    }
	}
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {
	resp.getWriter().println("Get method not supported!");
    }

    private void crawlAPIMeta() {
	LOG.info("Crawling metadata from stats.tk API");

	// use while loop to survive the timeout exception
	_forwardURL = null;
	while (_forwardURL == null || _forwardURL.isEmpty()) {
	    try {
		// get all meta data from dot.tk API
		// parse the response
		_apiMeta = JSON.decode(Jsoup.connect(API_BASE + _domain).get()
			.text(), APIMeta.class);
		_forwardURL = _apiMeta.getForwardURL();
	    } catch (Exception e) {
		LOG.warning("Crawling metadata from stats.tk API failed");
	    }
	}
	LOG.info("ForwardURL parsed: " + _domain + ", " + _forwardURL);
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

	    return _crawlCache.containsKey(_forwardURL);

	} catch (CacheException e) {
	    LOG.warning("Check crawling cache failed");
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

	    _webpage = (Webpage) _crawlCache.get(_forwardURL);

	} catch (CacheException e) {
	    LOG.severe("Loading webpage cache failed");
	}
    }

    /**
     * save the crawl result to memcache for parse, key=_forwardURL,
     * value=_webpage
     */
    private void saveCache() {
	LOG.info("Saving crawl result for next step");

	try {
	    if ((_crawlCache = CacheManager.getInstance().getCache(
		    "crawl_cache")) == null) {
		_crawlCache = CacheManager.getInstance().getCacheFactory()
			.createCache(Collections.emptyMap());
		// cache.put("crawl_cache", new HashMap<String, String>());
		CacheManager.getInstance().registerCache("crawl_cache",
			_crawlCache);
	    }

	    _crawlCache.put(_forwardURL, _webpage);

	} catch (CacheException e) {
	    LOG.severe("Saving crawl result to cache failed");
	}
    }

    /**
     * crawl HTML using _forwardURL instead of _domain
     */
    private void crawlWebpage() {
	LOG.info("Crawling webpage");
	try {
	    // get webpage
	    // store HTML instead of DOM which is not serializable
	    _doc = Jsoup.connect(_forwardURL).get();

	    _webpage.setHtml(_doc.html());
	    _webpage.setText(_doc.text());
	} catch (Exception e) {
	    LOG.severe("Crawling webpage failed");
	}
    }

    /**
     * detect the language of _webpage.getText();
     * 
     * @return
     */
    private String detectLanguage() {
	LOG.info("Detecting language of the webpage");
	try {
	    DetectorFactory.loadProfile("lang-profiles/");
	} catch (Exception e) {

	}
	try {
	    Detector detector = DetectorFactory.create();
	    detector.append(_webpage.getText());

	    return detector.detect();
	} catch (Exception e) {
	    LOG.severe("Detecting language of the webpage failed");

	    return "en";
	}
    }

    /**
     * parse HTML content and construct _phrases
     */
    private void parseHTML() {
	_status = Status.PARSING;
	try {
	    // load metadata
	    PhraseProperty property;

	    _webpageMeta = new WebpageMeta();
	    _webpageMeta.parseMeta(_doc);

	    for (String s : _webpageMeta.getTitle()) {
		if (_phrases.containsKey(s)) {
		    _phrases.get(s).setTitle(true).increaseOccurance();
		} else { // not exists
		    property = new PhraseProperty();
		    property.setNew(true).setTitle(true);

		    _phrases.put(s, property);
		}
	    }
	    for (String s : _webpageMeta.getKeywords()) {
		if (_phrases.containsKey(s)) {
		    _phrases.get(s).setMetaKeywords(true);
		} else { // not exists
		    property = new PhraseProperty();
		    property.setNew(true).setMetaKeywords(true);

		    _phrases.put(s, property);
		}
	    }
	    for (String s : _webpageMeta.getDescription()) {
		if (_phrases.containsKey(s)) {
		    _phrases.get(s).setMetaDescription(true);
		} else { // not exists
		    property = new PhraseProperty();
		    property.setNew(true).setMetaDescription(true);

		    _phrases.put(s, property);
		}
	    }

	    // tag part-of-speech, TF, ForwardURL
	    for (Entry<String, PhraseProperty> e : _phrases.entrySet()) {
		e.getValue().setPartOfSpeech(
			PartOfSpeech.load(OpenNLPPOSTagger.getWordTag(e
				.getKey())));
		e.getValue().setTFScore(
			(double) e.getValue().getOccurance() / _phrases.size());
		e.getValue().setForwardURL(_apiMeta.getForwardURL());
	    }

	    // check the language to load corresponding model
	    String lang = detectLanguage();
	    if (lang.equals("da") ||
		    lang.equals("de") ||
		    lang.equals("en") ||
		    lang.equals("nl") ||
		    lang.equals("pt") ||
		    lang.equals("se")) {
		// supported by opennlp
		OpenNLPTokenizer opennlpTokenizer = new OpenNLPTokenizer();

		// tokenize to phrases
		// load body
		Element body = _doc.body();
		PorterStemmer stemmer = new PorterStemmer();
		for (String p : opennlpTokenizer.tokenize(body.text())) {
		    p = TextUtils.removePunctuation(p);
		    p = TextUtils.removeNonAlphabeticChars(p);
		    p = p.toLowerCase().trim();

		    // check if the phrase is valid
		    if (p != null && !p.isEmpty()
				    && !StopwordsHandler.isStopwords(p)) {
			// stem
			p = stemmer.stem(p);
			if (_phrases.containsKey(p)) {
			    _phrases.get(p).increaseOccurance();
			} else {
			    property = new PhraseProperty();
			    property.setNew(true);
			    _phrases.put(p, property);
			}
		    }
		}

		_status = Status.PARSED;
	    } else {
		LOG.severe("Not supported language");
		_status = Status.FAILED;
	    }

	} catch (Exception e) {
	    _status = Status.FAILED;
	}
    }

    private void sort() {
	List<String> keys = new ArrayList<String>(_phrases.keySet());
	List<PhraseProperty> values = new ArrayList<PhraseProperty>(
		    _phrases.values());
	List<PhraseProperty> sortedValues = new ArrayList<PhraseProperty>(
		    _phrases.values());
	Collections.sort(sortedValues, new BM25FComparator());

	// get the top 3
	_keyPhrase = new KeyPhrase();
	_keyPhrase.setDomain(_domain);
	for (int i = 0; i < 3 && i < sortedValues.size(); i++) {
	    _keyPhrase.appendKeyPhrase(keys.get(values.indexOf(sortedValues
			.get(i))));
	}
    }

    /**
     * save the inverse document index to datastore
     */
    private void saveIndex() {
	// save the phrases - forwardURL
	for (Entry<String, PhraseProperty> e : _phrases.entrySet()) {
	    PhraseStoreHandler.put(e.getKey(), e.getValue());
	}

	// save the webpage to datastore
	DomainStoreHandler.put(_forwardURL, _domain);
    }
}
