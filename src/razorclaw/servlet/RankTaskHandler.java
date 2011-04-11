package razorclaw.servlet;

import java.io.IOException;
import java.util.Collections;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;
import razorclaw.object.Webpage;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;

@SuppressWarnings("deprecation")
public class RankTaskHandler extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 3562652882984169646L;

    private static final Logger LOG = Logger.getLogger(RankTaskHandler.class
	    .getName());

    private String _forwardURL;

    private String _domain; // dot.tk domain

    private Webpage _webpage; // content and metadata

    private Cache _parseCache;

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {
	if ((_domain = req.getParameter("domain").toLowerCase()) != null) {

	    LOG.info("Received rank request for domain: " + _domain);
	    // load parse cache
	    load();
	    // tokenize, remove stopwords, stem, merge and etc.
	    rank();
	    // save rank result
	    save();

	} else {
	    LOG.severe("Wrong parameter \"domain\"");
	}
    }

    // public class TFIDFSort implements Comparator<PhraseProperty> {
    // @Override
    // public int compare(PhraseProperty arg0, PhraseProperty arg1) {
    // if (arg0.getTFIDFScore() - arg1.getTFIDFScore() > 0) {
    // return 1;
    // } else if (arg0.getTFIDFScore() - arg1.getTFIDFScore() == 0) {
    // return 0;
    // } else {
    // return -1;
    // }
    // }
    // }

    private void save() {
	// _webpage.setStatus(Status.RANKED);

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
	    // for (Entry<String, PhraseProperty> e : _webpage.getPhrases()
	    // .entrySet()) {
	    // PhraseStoreHandler.put(e.getKey(), e.getValue());
	    // }

	    // save the webpage to datastore
	    // DomainStoreHandler.put(_forwardURL, _domain,
	    // _webpage.getKeyPhrases());
	} catch (CacheException e) {
	    LOG.severe("Saving parse result failed");
	}
    }

    private void load() {
	LOG.info("Loading parse cache");

	// _webpage = new Webpage();
	try {
	    if ((_parseCache = CacheManager.getInstance().getCache(
		    "parse_cache")) == null) {
		_parseCache = CacheManager.getInstance().getCacheFactory()
			.createCache(Collections.emptyMap());
		// cache.put("crawl_cache", new HashMap<String, String>());
		CacheManager.getInstance().registerCache("parse_cache",
			_parseCache);
	    }

	    _webpage = (Webpage) _parseCache.get(_domain);
	} catch (CacheException e) {
	    LOG.severe("Loading parse cache failed");
	}

	// _webpage.setStatus(Status.RANKING);
    }

    private void rank() {
	LOG.info("Ranking");

	// TODO: introduce more ranker and finally use the ML ranker
	try {
	    // TFIDF.rank(_webpage);
	} catch (Exception e) {
	    LOG.severe("Ranking Failed");
	}
    }

    public static void createRankTask(String domain) {
	LOG.info("Creating rank task");

	try {
	    Queue queue = QueueFactory.getQueue("rank-queue");
	    queue.add(TaskOptions.Builder.url("/RankTaskHandler").param(
		    "domain", domain));
	} catch (Exception ex) {
	    ex.printStackTrace();

	    LOG.severe("Creating rank task failed");
	}
    }
}
