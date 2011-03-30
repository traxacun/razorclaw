package razorclaw.servlet;

import java.io.IOException;
import javax.servlet.http.*;

import razorclaw.datastore.DomainStoreHandler;

import com.google.appengine.api.labs.taskqueue.*;

@SuppressWarnings("serial")
public class Main extends HttpServlet {
    @SuppressWarnings("deprecation")
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {
//	try {
//	    Queue queue = QueueFactory.getQueue("crawl-queue");
//	    queue.add(TaskOptions.Builder.url("/CrawlTaskHandler").param(
//		    "domain", "chomeo.tk"));
//
//	    resp.getWriter()
//		    .println("Successfully created a Task in the Queue");
//	} catch (Exception ex) {
//	    ex.printStackTrace();
//	}
	
	DomainStoreHandler.test();

	// resp.setContentType("text/plain");
	// resp.getWriter().println("Hello, world");

	// URL u = new URL("http://www.dot.tk");
	// Document doc = getPage(u);
	// InputHandler in = new InputHandler();
	// in.loadDomain("smrl.tk");
	/*
	 * Document doc = Jsoup.connect("http://searchenginewatch.com/2167931")
	 * .get();
	 * 
	 * // Metadata md = new Metadata(); // md.parseHTML(doc);
	 * 
	 * Webpage w = new Webpage(); w.setHtml(doc); w.parseHTML();
	 * 
	 * ArrayList<Phrase> phrases = w.getPhrases(); // sort by TF
	 * Collections.sort(phrases, new Phrase.ByTF()); for (Phrase p :
	 * phrases) { resp.getWriter().println(p.toString()); }
	 */
	// System.out.println(w.getMeta().getTitle());
	// System.out.println(w.getMeta().getKeywords());
	// System.out.println(w.getMeta().getDescription());
	//
	// System.out.println(w.getWords());
	// Node head = doc.getElementsByTagName("head").item(0);

	// resp.getWriter().println(head.getTextContent());
    }
    /*
     * private Document getPage(URL u) { Tidy tidy = new Tidy();
     * tidy.setQuiet(true); tidy.setShowWarnings(false);
     * 
     * try { InputStream in = u.openStream();
     * 
     * Document doc = tidy.parseDOM(in, System.out);
     * 
     * return doc; } catch (IOException e) { // TODO Auto-generated catch block
     * e.printStackTrace();
     * 
     * return null; }
     * 
     * }
     */
}
