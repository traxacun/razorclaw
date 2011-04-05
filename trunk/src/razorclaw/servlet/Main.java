package razorclaw.servlet;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;
import razorclaw.object.Dictionaries.Status;
import razorclaw.object.Webpage;

@SuppressWarnings("serial")
public class Main extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {

	// try { // load stats.tk/ucl/top100
	// String topList = Jsoup.connect("http://www.stats.tk/ucl/top100")
	// .get().body().text();
	//
	// // convert to JSON object
	// JSONObject list = new JSONObject(topList);
	//
	// JSONArray domainArray = (JSONArray) list.get("domains"); // create
	// // crawl
	// // tasks
	//
	// for (int i = 0; i < 5; i++) {
	// CrawlTaskHandler.createCrawlTask(domainArray.getString(i));
	//
	// resp.getWriter().println(
	// "Created crawl task for "
	// + domainArray.getString(i).toLowerCase());
	// }
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// }

	CrawlTaskHandler.createCrawlTask(req.getParameter("domain"));

	// poll and output the result

	try {
	    Cache cache;
	    if ((cache = CacheManager.getInstance().getCache("parse_cache")) == null) {
		cache = CacheManager.getInstance().getCacheFactory()
			.createCache(Collections.emptyMap());
		// cache.put("crawl_cache", new HashMap<String, String>());
		CacheManager.getInstance().registerCache("parse_cache", cache);
	    }
	    while (true) {
		Webpage webpage = (Webpage) cache.get(req
			.getParameter("domain").toLowerCase());

		if (webpage != null && webpage.getStatus() == Status.RANKED) {
		    resp.getWriter().println(webpage.getPhrases());
		    break;
		} else {
		    Thread.sleep(2000);
		}
	    }
	} catch (CacheException e) {

	} catch (InterruptedException e) {

	}

    }
}
