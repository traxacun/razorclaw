package ucl.GAE.razorclaw.object;

import java.util.Iterator;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ucl.GAE.razorclaw.object.Dictionaries.*;

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

public class Metadata {
    private HtmlVersion _htmlVersion;

    /**
     * These are non-formal metadata and may not exist in all webpages.
     */
    private String _language, _contentType, _charset, _date;

    private String _keywords, _description;

    private String _title;

    public String getTitle() {
	return _title;
    }

    public void setTitle(String _title) {
	this._title = _title;
    }

    public HtmlVersion getHtmlVersion() {
	return _htmlVersion;
    }

    public void setHtmlVersion(HtmlVersion _htmlVersion) {
	this._htmlVersion = _htmlVersion;
    }

    public String getLanguage() {
	return _language;
    }

    public void setLanguage(String _language) {
	this._language = _language;
    }

    public String getContentType() {
	return _contentType;
    }

    public void setContentType(String _contentType) {
	this._contentType = _contentType;
    }

    public String getCharset() {
	return _charset;
    }

    public void setCharset(String _charset) {
	this._charset = _charset;
    }

    public String getDate() {
	return _date;
    }

    public void setDate(String _date) {
	this._date = _date;
    }

    public String getKeywords() {
	return _keywords;
    }

    public void setKeywords(String _keywords) {
	this._keywords = _keywords;
    }

    public String getDescription() {
	return _description;
    }

    public void setDescription(String _description) {
	this._description = _description;
    }

    /**
     * fill attributes from input html node.
     * 
     * @param html
     *            the input html DOM node
     * 
     * 
     */
    public void parseHTML(Document html) {
	// html version
	// <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	// "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
	// Node node = html.getFirstChild();
	// String s = node.getTextContent();
	// this.setHtmlVersion(checkHtmlVersion(s));

	// keywords and description from <meta>

	Elements elements = html.getElementsByTag("meta");
	Iterator<Element> it = elements.iterator();
	while (it.hasNext()) {
	    Element e = it.next();
	    if (e.attr("name").equals("keywords")) {
		this.setKeywords(e.attr("content"));
	    } else if (e.attr("name").equals("description")) {
		this.setDescription(e.attr("content"));
	    } else {
		continue;
	    }
	}

	// title
	this.setTitle(html.title());

	// language

    }
/*
    private HtmlVersion checkHtmlVersion(String s) {
	if (s.contains("XHTML 1")) {
	    return HtmlVersion.XHTML1;
	} else if (s.contains("XHTML 2")) {
	    return HtmlVersion.XHTML2;
	} else if (s.contains("XHTML 5")) {
	    return HtmlVersion.XHTML5;
	} else if (s.contains("HTML 2")) {
	    return HtmlVersion.HTML2;
	} else if (s.contains("HTML 3")) {
	    return HtmlVersion.HTML3;
	} else if (s.contains("HTML 4")) {
	    return HtmlVersion.HTML4;
	} else if (s.contains("HTML 5")) {
	    return HtmlVersion.HTML5;
	} else {
	    return HtmlVersion.UNKNOWN;
	}
    }
*/    
}
