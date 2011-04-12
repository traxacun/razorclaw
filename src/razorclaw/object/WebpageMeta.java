package razorclaw.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import razorclaw.object.Dictionaries.HtmlVersion;

/**
 * Stores metadata extracted from a webpage, including title, h1, h2, and
 * meta(keywords & description).
 * 
 * A lot of webmasters have used <meta> tags for spamming, like repeating
 * keywords (or using wrong keywords) for higher ranking. Therefore, most search
 * engines have stopped using <meta> tags to index/rank pages.
 * 
 * @author Shuai YUAN
 */

public class WebpageMeta implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -7118304523690937732L;

    private final HtmlVersion _htmlVersion = HtmlVersion.UNKNOWN;

    /**
     * These are non-formal metadata and may not exist in all webpages.
     */
    private String _language, _charset;

    private final String _contentType = "", _date = "";

    // breaking into words would be faster than search in a long string
    private final ArrayList<String> _keywords, _description, _title, _h1, _h2;

    public WebpageMeta() {
	_keywords = new ArrayList<String>();
	_description = new ArrayList<String>();
	_title = new ArrayList<String>();
	_h1 = new ArrayList<String>();
	_h2 = new ArrayList<String>();
    }

    /**
     * fill attributes from input html node.
     * 
     * @param doc
     *            the input html DOM node
     */
    public void parseMeta(Document doc) {
	// html version
	// <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	// "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
	// Node node = html.getFirstChild();
	// String s = node.getTextContent();
	// this.setHtmlVersion(checkHtmlVersion(s));

	// keywords and description from <meta>
	Elements elements = doc.getElementsByTag("meta");
	Iterator<Element> it = elements.iterator();
	while (it.hasNext()) {
	    Element e = it.next();
	    if (e.attr("name").equals("keywords")) {
		// NOTE: keywords are separated by comma
		String[] arr = e.attr("content").split(",");

		for (String s : arr) {
		    if (!s.trim().isEmpty()) {
			_keywords.add(s);
		    }
		}
	    } else if (e.attr("name").equals("description")) {
		// String content =
		// TextUtils.removePunctuation(e.attr("content"));
		// content = TextUtils.removeNonAlphabeticChars(content);
		String[] arr = e.attr("content").split(" ");

		for (String s : arr) {
		    if (!s.trim().isEmpty()) {
			_description.add(s);
		    }
		}
	    } else {
		continue;
	    }
	}

	// title
	String[] arr = doc.title().split("");

	for (String s : arr) {
	    if (!s.trim().isEmpty()) {
		_title.add(s);
	    }
	}

	// h1
	elements = doc.getElementsByTag("h1");
	for (it = elements.iterator(); it.hasNext();) {
	    arr = it.next().text().split("");

	    for (String s : arr) {
		if (!s.trim().isEmpty()) {
		    _h1.add(s);
		}
	    }
	}

	// h2
	elements = doc.getElementsByTag("h2");
	for (it = elements.iterator(); it.hasNext();) {
	    arr = it.next().text().split("");

	    for (String s : arr) {
		if (!s.trim().isEmpty()) {
		    _h2.add(s);
		}
	    }
	}

	// language

    }

    // -----------------------getters and setters---------------------
    public ArrayList<String> getTitle() {
	return _title;
    }

    public HtmlVersion getHtmlVersion() {
	return _htmlVersion;
    }

    public String getLanguage() {
	return _language;
    }

    public String getContentType() {
	return _contentType;
    }

    public String getCharset() {
	return _charset;
    }

    public String getDate() {
	return _date;
    }

    public ArrayList<String> getKeywords() {
	return _keywords;
    }

    public ArrayList<String> getDescription() {
	return _description;
    }

    public ArrayList<String> getH2() {
	return _h2;
    }

    public ArrayList<String> getH1() {
	return _h1;
    }

    public void setLanguage(String _language) {
	this._language = _language;
    }

    public void setCharset(String _charset) {
	this._charset = _charset;
    }

    /*
     * private HtmlVersion checkHtmlVersion(String s) { if
     * (s.contains("XHTML 1")) { return HtmlVersion.XHTML1; } else if
     * (s.contains("XHTML 2")) { return HtmlVersion.XHTML2; } else if
     * (s.contains("XHTML 5")) { return HtmlVersion.XHTML5; } else if
     * (s.contains("HTML 2")) { return HtmlVersion.HTML2; } else if
     * (s.contains("HTML 3")) { return HtmlVersion.HTML3; } else if
     * (s.contains("HTML 4")) { return HtmlVersion.HTML4; } else if
     * (s.contains("HTML 5")) { return HtmlVersion.HTML5; } else { return
     * HtmlVersion.UNKNOWN; } }
     */
}
