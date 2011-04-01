package razorclaw.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import razorclaw.object.Dictionaries.HtmlVersion;
import razorclaw.parser.TextUtils;

/**
 * Stores metadata tags extracted from <head> for a webpage, including title and
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
    private final String _language = "", _contentType = "", _charset = "",
	    _date = "";

    // breaking into words would be faster than search in a long string
    private final ArrayList<String> _keywords, _description, _title;

    public WebpageMeta() {
	_keywords = new ArrayList<String>();
	_description = new ArrayList<String>();
	_title = new ArrayList<String>();
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
		String[] arr = e.attr("content")
			.split(TextUtils.replacePattern);
		for (String s : arr) {
		    if (!s.isEmpty()) {
			_keywords.add(s);
		    }
		}
	    } else if (e.attr("name").equals("description")) {
		String[] arr = e.attr("content")
			.split(TextUtils.replacePattern);
		for (String s : arr) {
		    if (!s.isEmpty()) {
			_description.add(s);
		    }
		}
	    } else {
		continue;
	    }
	}

	// title
	String[] arr = doc.title().split(TextUtils.replacePattern);
	for (String s : arr) {
	    if (!s.isEmpty()) {
		_title.add(s);
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
