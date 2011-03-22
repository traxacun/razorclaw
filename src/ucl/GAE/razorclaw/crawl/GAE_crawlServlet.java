package ucl.GAE.razorclaw.crawl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import ucl.GAE.razorclaw.object.Metadata;
import ucl.GAE.razorclaw.object.Phrase;
import ucl.GAE.razorclaw.object.Webpage;

@SuppressWarnings("serial")
public class GAE_crawlServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {
	// resp.setContentType("text/plain");
	// resp.getWriter().println("Hello, world");

	// URL u = new URL("http://www.dot.tk");
	// Document doc = getPage(u);

	Document doc = Jsoup.connect("http://searchenginewatch.com/2167931")
		.get();

	// Metadata md = new Metadata();
	// md.parseHTML(doc);

	Webpage w = new Webpage();
	w.setHtml(doc);
	w.parseHTML();

	ArrayList<Phrase> phrases = w.getPhrases();
	// sort by TF
	Collections.sort(phrases, new Phrase.ByTF());
	for (Phrase p : phrases) {
	    resp.getWriter().println(p.toString());
	}

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
