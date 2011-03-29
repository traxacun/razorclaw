package ucl.GAE.razorclaw.servlet;

import java.io.IOException;
import java.util.Collections;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import razorclaw.object.Webpage;
import razorclaw.object.Dictionaries.Status;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;

public class ParseTaskHandler extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 7641969297404354685L;

    private String _forwardURL;

    private String _domain; // dot.tk domain

    private Webpage _webpage; // content and metadata

    private String _html;

    private Cache _cache;

    private static final Logger LOG = Logger.getLogger(ParseTaskHandler.class
	    .getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {
	if ((_domain = req.getParameter("domain").toLowerCase()) != null) {
	    if ((_forwardURL = req.getParameter("forwardURL")) != null) {
		LOG.info("Received parse request for URL: " + _forwardURL);

		_webpage = new Webpage();

		loadCache();

		parse();

		saveResult();
		
		System.out.println(_webpage.getPhrases().toString());
	    } else {
		LOG.severe("Wrong parameter \"forwardURL\"");
	    }
	} else {
	    LOG.severe("Wrong parameter \"domain\"");
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

    private void parse() {
	if (_webpage != null) {
	    LOG.info("Parsing the webpage");

	    _webpage.setStatus(Status.PARSING);

	    _webpage.parseHTML();

	    _webpage.setStatus(Status.PARSED);
	} else {
	    LOG.severe("Parsing the webpage failed");
	}
    }

    private void saveResult() {
	LOG.info("Saving parse result for next step");

	try {
	    if ((_cache = CacheManager.getInstance().getCache("parse_cache")) == null) {
		_cache = CacheManager.getInstance().getCacheFactory()
			.createCache(Collections.emptyMap());
		// cache.put("crawl_cache", new HashMap<String, String>());
		CacheManager.getInstance().registerCache("parse_cache", _cache);
	    }

	    _cache.put(_forwardURL, _webpage);

	} catch (CacheException e) {
	    LOG.severe("Saving parse result failed");
	}
    }

    @SuppressWarnings("deprecation")
    private void createRankTask() {
	LOG.info("Creating rank task");

	try {
	    Queue queue = QueueFactory.getQueue("rank-queue");
	    queue.add(TaskOptions.Builder.url("/RankTaskHandler").param(
		    "domain", _forwardURL));
	} catch (Exception ex) {
	    ex.printStackTrace();

	    LOG.severe("Creating rank task failed");
	}
    }
}
