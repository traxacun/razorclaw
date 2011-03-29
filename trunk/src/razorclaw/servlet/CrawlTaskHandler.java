package ucl.GAE.razorclaw.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import razorclaw.object.APIMeta;
import razorclaw.object.Webpage;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.appengine.api.urlfetch.*;

import static com.google.appengine.api.urlfetch.FetchOptions.Builder.*;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;


/**
 * invoke this handler to create a crawl task, which will download the webpage
 * and extract metadata from stats.tk API
 * 
 * @author Shuai YUAN
 * 
 */
public class CrawlTaskHandler extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = -1200053906967311297L;

    private final String API_BASE = "http://www.stats.tk/ucl/domain?domain=";

    private final String HTTP_BASE = "http://";

    private String _domain; // dot.tk domain

    private String _forwardURL;

    private Webpage _webpage; // content and metadata

    private Cache _cache;

    private static final Logger LOG = Logger.getLogger(CrawlTaskHandler.class
	    .getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {
	// parse parameters
	if ((_domain = req.getParameter("domain").toLowerCase()) != null) {
	    LOG.info("Received crawl request for domain: " + _domain);

	    _webpage = new Webpage();

	    crawlAPI();

	    if (checkCache()) {
		loadCache();
	    } else {
		crawlWebpage();

		saveResult();
	    }

	    createParseTask();
	} else {
	    LOG.severe("Wrong parameter \"domain\"");
	}

    }

    /**
     * save the crawl result to memcache for parse, key=_domain, value=_webpage
     */
    private void saveResult() {
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
     * create a parse task after crawling
     */
    @SuppressWarnings("deprecation")
    private void createParseTask() {
	LOG.info("Creating parse task");

	try {
	    Queue queue = QueueFactory.getQueue("parse-queue");
	    queue.add(TaskOptions.Builder.url("/ParseTaskHandler")
		    .param("forwardURL", _forwardURL).param("domain", _domain));
	} catch (Exception ex) {
	    ex.printStackTrace();

	    LOG.severe("Creating parse task failed");
	}
    }

    /**
     * crawl metadata from stats.tk API
     */
    private void crawlAPI() {
	LOG.info("Crawling metadata from stats.tk API");

	while (_forwardURL == null) {
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

	    } catch (JSONException e) {

	    }
	}
    }

    private void crawlWebpage() {
	LOG.info("Crawling webpage");
	try {
	    // get webpage
	    Document doc = Jsoup.connect(HTTP_BASE + _domain).get();

	    // save HTML text instead of DOM
	    // Document is not Serializable therefore couldn't be saved to cache
	    _webpage.setHtml(doc.html());

	} catch (IOException e) {

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

	    return false;
	}
    }

    private void loadCache() {
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

	}
    }
}
