package razorclaw.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class Main extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {
	/*
	 * try { // load stats.tk/ucl/top100 String topList =
	 * Jsoup.connect("http://www.stats.tk/ucl/top100") .get().body().text();
	 * 
	 * // convert to JSON object JSONObject list = new JSONObject(topList);
	 * 
	 * JSONArray domainArray = (JSONArray) list.get("domains"); // create
	 * crawl tasks for (int i = 0; i < domainArray.length(); i++) {
	 * CrawlTaskHandler.createCrawlTask(domainArray.getString(i));
	 * 
	 * resp.getWriter().println( "Created crawl task for " +
	 * domainArray.getString(i)); } } catch (Exception ex) {
	 * ex.printStackTrace(); }
	 */

	CrawlTaskHandler.createCrawlTask(req.getParameter("domain"));
    }
}
