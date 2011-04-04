package razorclaw.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import razorclaw.object.APIMeta;
import razorclaw.object.Webpage;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;

/**
 * invoke this handler to create a crawl task, which will download the webpage
 * and extract metadata from stats.tk API
 * 
 * @author Shuai YUAN
 * 
 */
@SuppressWarnings("deprecation")
public class CrawlTaskHandler extends HttpServlet {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -1200053906967311297L;

    private final String API_BASE = "http://www.stats.tk/ucl/domain?domain=";

    // private final String HTTP_BASE = "http://";

    private String _domain; // dot.tk domain

    private String _forwardURL;

    private Webpage _webpage; // content and metadata

    /**
     * stores <forwardURL, HTML> pair for further parsing and ranking
     */
    private Cache _cache;

    private static final Logger LOG = Logger.getLogger(CrawlTaskHandler.class
	    .getName());

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {
	// parse parameters
	if ((_domain = req.getParameter("domain").toLowerCase()) != null) {
	    LOG.info("Received crawl request for domain: " + _domain);

	    _webpage = new Webpage();
	    // get API metadata from stats.tk
	    crawlAPIMeta();
	    // if already holding the content of the webpage
	    if (checkCache()) {
		load();
	    } else {
		crawlWebpage();

		// save the HTML to cache
		save();
	    }
	    // we always create a parse task for the forwardURL. although
	    // the webpage may be already parsed, there would be different
	    // API metadata associated with the dot.tk domain
	    ParseTaskHandler.createParseTask(_forwardURL, _domain);
	} else {
	    LOG.severe("Wrong parameter \"domain\"");
	}
    }

    /**
     * save the crawl result to memcache for parse, key=_forwardURL,
     * value=_webpage
     */
    private void save() {
	LOG.info("Saving crawl result for next step");

	try {
	    if ((_cache = CacheManager.getInstance().getCache("crawl_cache")) == null) {
		_cache = CacheManager.getInstance().getCacheFactory()
			.createCache(Collections.emptyMap());
		// cache.put("crawl_cache", new HashMap<String, String>());
		CacheManager.getInstance().registerCache("crawl_cache", _cache);
	    }

	    _cache.put(_forwardURL, _webpage);

	} catch (CacheException e) {
	    LOG.severe("Saving crawl result to cache failed");
	}
    }

    /**
     * crawl metadata from stats.tk API
     */
    private void crawlAPIMeta() {
	LOG.info("Crawling metadata from stats.tk API");

	// use while loop to survive the timeout exception
	_forwardURL = null;
	while (_forwardURL == null || _forwardURL.isEmpty()) {
	    try {
		// get all meta data from dot.tk API
		URL fullURL = new URL(API_BASE + _domain);
		BufferedReader in = new BufferedReader(new InputStreamReader(
			fullURL.openStream()));

		String content = "", s = "";

		while ((s = in.readLine()) != null) {
		    content += s;
		}
		JSONObject jsonobj = new JSONObject(content);

		// parse the response
		APIMeta apiMeta = new APIMeta(jsonobj);
		_webpage.setAPIMeta(apiMeta);

		_forwardURL = apiMeta.getForwardURL();
	    } catch (IOException e) {
		LOG.warning("Crawling metadata from stats.tk API failed");
	    } catch (JSONException e) {
		LOG.warning("Crawling metadata from stats.tk API failed");
	    }
	}
	LOG.info("ForwardURL parsed: " + _domain + ", " + _forwardURL);
    }

    /**
     * crawl HTML using _forwardURL instead of _domain
     */
    private void crawlWebpage() {
	LOG.info("Crawling webpage");
	try {
	    // get webpage
	    Document doc = Jsoup.connect(_forwardURL).get();

	    // save HTML text instead of DOM
	    // Document is not Serializable therefore couldn't be saved to cache
	    _webpage.setHtml(doc.html());

	} catch (IOException e) {
	    LOG.severe("Crawling webpage failed");
	}
    }

    /**
     * some users setup multiple dot.tk domains referring to the same URL. check
     * if the destination webpage exists in the cache, use forwardurl as the key
     */
    private boolean checkCache() {
	LOG.info("Check crawling cache");

	try {
	    if ((_cache = CacheManager.getInstance().getCache("crawl_cache")) == null) {
		_cache = CacheManager.getInstance().getCacheFactory()
			.createCache(Collections.emptyMap());
		// cache.put("crawl_cache", new HashMap<String, String>());
		CacheManager.getInstance().registerCache("crawl_cache", _cache);
	    }

	    return _cache.containsKey(_forwardURL);

	} catch (CacheException e) {
	    LOG.warning("Check crawling cache failed");
	    return false;
	}
    }

    /**
     * load a webpage from memcache if it's already crawled
     */
    private void load() {
	LOG.info("Loading webpage cache");

	try {
	    if ((_cache = CacheManager.getInstance().getCache("crawl_cache")) == null) {
		_cache = CacheManager.getInstance().getCacheFactory()
			.createCache(Collections.emptyMap());
		// cache.put("crawl_cache", new HashMap<String, String>());
		CacheManager.getInstance().registerCache("crawl_cache", _cache);
	    }

	    _webpage = (Webpage) _cache.get(_forwardURL);

	} catch (CacheException e) {
	    LOG.severe("Loading webpage cache failed");
	}
    }

    /**
     * create a crawl task for the given domain
     * 
     * @param domain
     *            dot.tk domain
     */
    public static void createCrawlTask(String domain) {
	LOG.info("Creating crawl task");

	try {
	    Queue queue = QueueFactory.getQueue("crawl-queue");
	    queue.add(TaskOptions.Builder.url("/CrawlTaskHandler").param(
		    "domain", domain));
	} catch (Exception ex) {
	    ex.printStackTrace();

	    LOG.severe("Creating crawl task failed");
	}
    }
}
