package razorclaw.servlet;

import java.io.IOException;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;
import razorclaw.datastore.DomainStoreHandler;
import razorclaw.datastore.PhraseStoreHandler;
import razorclaw.object.Dictionaries.Status;
import razorclaw.object.PhraseProperty;
import razorclaw.object.Webpage;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;

@SuppressWarnings("deprecation")
public class ParseTaskHandler extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 7641969297404354685L;

    private String _forwardURL;

    private String _domain; // dot.tk domain

    private Webpage _webpage; // content and metadata

    private Cache _crawlCache, _parseCache;

    private static final Logger LOG = Logger.getLogger(ParseTaskHandler.class
	    .getName());

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {
	if ((_domain = req.getParameter("domain").toLowerCase()) != null) {
	    if ((_forwardURL = req.getParameter("forwardURL")) != null) {
		LOG.info("Received parse request for URL: " + _forwardURL);
		// load webpage cache
		load();
		// tokenize, remove stopwords, stem, merge and etc.
		parse();
		if (_webpage.getStatus() != Status.FAILED) {
		    // save parse result
		    save();

		    RankTaskHandler.createRankTask(_domain);
		} else {
		    LOG.severe("Parse failed for URL: " + _forwardURL);
		}
	    } else {
		LOG.severe("Wrong parameter \"forwardURL\"");
	    }
	} else {
	    LOG.severe("Wrong parameter \"domain\"");
	}

    }

    private void load() {
	LOG.info("Loading webpage cache");

	// _webpage = new Webpage();
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

    private void parse() {
	if (_webpage != null) {
	    LOG.info("Parsing the webpage");

	    _webpage.setStatus(Status.PARSING);

	    _webpage.parseHTML();
	    if (_webpage.getStatus() != Status.FAILED) {
		_webpage.setStatus(Status.PARSED);
	    } else {

	    }
	} else {
	    LOG.severe("Parsing the webpage failed");
	}
    }

    private void save() {
	LOG.info("Saving parse result for next step");

	try {
	    // save to memcache
	    if ((_parseCache = CacheManager.getInstance().getCache(
		    "parse_cache")) == null) {
		_parseCache = CacheManager.getInstance().getCacheFactory()
			.createCache(Collections.emptyMap());
		// cache.put("crawl_cache", new HashMap<String, String>());
		CacheManager.getInstance().registerCache("parse_cache",
			_parseCache);
	    }
	    _parseCache.put(_domain, _webpage);

	    // save the phrases to datastore
	    for (Entry<String, PhraseProperty> e : _webpage.getPhrases()
		    .entrySet()) {
		PhraseStoreHandler.put(e.getKey(), e.getValue());
	    }

	    // save the webpage to datastore
	    // TODO: move this to ranker after getting the keyphrases
	    DomainStoreHandler.put(_forwardURL, _domain, null);
	} catch (CacheException e) {
	    LOG.severe("Saving parse result failed");
	}
    }

    /**
     * create a parse task after crawling
     */
    public static void createParseTask(String forwardURL, String domain) {
	LOG.info("Creating parse task");

	try {
	    Queue queue = QueueFactory.getQueue("parse-queue");
	    queue.add(TaskOptions.Builder.url("/ParseTaskHandler")
		    .param("forwardURL", forwardURL).param("domain", domain));
	} catch (Exception ex) {
	    ex.printStackTrace();

	    LOG.severe("Creating parse task failed");
	}
    }
}
