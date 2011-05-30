package razorclaw.object;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import razorclaw.linguistic.parser.CJKVTokenizer;
import razorclaw.linguistic.parser.StopwordsHandler;
import razorclaw.object.Dictionaries.HtmlVersion;
import razorclaw.util.CCCEDICTProcessor;
import razorclaw.util.TextUtils;

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

	private static final int MINIMUM_LENGTH = 2;

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
	 * gather words from special webpage meta, including title, keywords,
	 * description, h1, h2
	 * 
	 * @param doc
	 *            the input HTML DOM node
	 * @throws IOException
	 */
	public void parseMeta(Document doc) throws IOException {
		// keywords and description from <meta>
		Elements elements = doc.getElementsByTag("meta");
		Iterator<Element> it = elements.iterator();
		while (it.hasNext()) {
			Element e = it.next();
			if (e.attr("name").equals("keywords")) {
				// NOTE: keywords are separated by comma
				String[] arr = e.attr("content").split(",|，");

				for (String s : arr) {
					if (!s.trim().isEmpty()) {
						_keywords.add(s);
					}
				}
			} else if (e.attr("name").equals("description")) {
				if (_language.equals("zh-cn") || _language.equals("zh-tw")
						|| _language.equals("ja") || _language.equals("ka")
						|| _language.equals("vi")) {
					CJKVTokenizer.feed(new StringReader(doc.title()));
					for (String s = CJKVTokenizer.next(); s != null; s = CJKVTokenizer
							.next()) {
						if (s.length() >= MINIMUM_LENGTH) {
							_description.add(s);
						}
					}
				} else {
					for (String s : e.attr("content").split(
							TextUtils.replacePattern + "| |/")) {
						if (!s.trim().isEmpty()) {
							_description.add(s);
						}
					}
				}
			} else {
				continue;
			}
		}

		if (_language.equals("zh-cn") || _language.equals("zh-tw")
				|| _language.equals("ja") || _language.equals("ka")
				|| _language.equals("vi")) {
			CJKVTokenizer.feed(new StringReader(doc.title()));
			// title
			for (String s = CJKVTokenizer.next(); s != null; s = CJKVTokenizer
					.next()) {
				if (s.length() >= MINIMUM_LENGTH) {
					_title.add(s);
				}
			}
			// h1
			elements = doc.getElementsByTag("h1");
			for (it = elements.iterator(); it.hasNext();) {
				CJKVTokenizer.feed(new StringReader(it.next().text()));
				for (String s = CJKVTokenizer.next(); s != null; s = CJKVTokenizer
						.next()) {
					if (s.length() >= MINIMUM_LENGTH) {
						_h1.add(s);
					}
				}
			}
			// h2
			elements = doc.getElementsByTag("h2");
			for (it = elements.iterator(); it.hasNext();) {
				CJKVTokenizer.feed(new StringReader(it.next().text()));
				for (String s = CJKVTokenizer.next(); s != null; s = CJKVTokenizer
						.next()) {
					if (s.length() >= MINIMUM_LENGTH) {
						_h2.add(s);
					}
				}
			}
		} else {
			// title
			String[] arr = doc.title().split(TextUtils.replacePattern + "| |/");
			for (String s : arr) {
				if (!s.trim().isEmpty() && !StopwordsHandler.isStopwords(s)) {
					_title.add(s);
				}
			}
			// h1
			elements = doc.getElementsByTag("h1");
			for (it = elements.iterator(); it.hasNext();) {
				arr = it.next().text().split(TextUtils.replacePattern + "| |/");
				for (String s : arr) {
					if (!s.trim().isEmpty() && !StopwordsHandler.isStopwords(s)) {
						_h1.add(s);
					}
				}
			}
			// h2
			elements = doc.getElementsByTag("h2");
			for (it = elements.iterator(); it.hasNext();) {
				arr = it.next().text().split(TextUtils.replacePattern + "| |/");
				for (String s : arr) {
					if (!s.trim().isEmpty() && !StopwordsHandler.isStopwords(s)) {
						_h2.add(s);
					}
				}
			}
		}
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
