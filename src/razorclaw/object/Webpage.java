package razorclaw.object;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

import razorclaw.object.APIMeta.RefererAnchorText;
import razorclaw.object.Dictionaries.PartOfSpeech;
import razorclaw.parser.BasicTokenizer;
import razorclaw.parser.CJKVTokenizer;
import razorclaw.parser.OpenNLPPOSTagger;
import razorclaw.parser.OpenNLPTokenizer;
import razorclaw.parser.StopwordsHandler;
import razorclaw.parser.stemmer.SremovalStemmer;
import razorclaw.util.TextUtils;

/**
 * represents a webpage crawled from a dot.tk domain. NOTE: there could be
 * multiple dot.tk domains pointing to the same webpage
 * 
 * @author Shuai YUAN
 * 
 */
public class Webpage implements Serializable, nsICharsetDetectionObserver {
	/**
     * 
     */
	private static final long serialVersionUID = -3083386200240092000L;

	/**
	 * content between <html> and </html>
	 */
	private String _html;

	/**
	 * content between <html> and </html> with HTML tag removed.
	 */
	private String _text;

	private static final Logger LOG = Logger.getLogger(Webpage.class.getName());

	private APIMeta _apiMeta;

	private WebpageMeta _webpageMeta;

	private HashMap<String, PhraseProperty> _phraseMap;

	public Webpage() {
	}

	public void parse() throws IOException {
		LOG.info("Parsing HTML");

		_phraseMap = new HashMap<String, PhraseProperty>();

		PhraseProperty property;

		// check the language to load corresponding model
		String lang = getWebpageMeta().getLanguage();

		if (lang == null || lang.isEmpty()) {
			LOG.severe("Language unidentified");

			throw new IOException();
		} else if (lang.equals("da") || lang.equals("de") || lang.equals("en")
				|| lang.equals("nl") || lang.equals("pt") || lang.equals("se")) {
			LOG.info("Using opennlp tokenizer");

			// supported by opennlp
			// tokenize
			SremovalStemmer stemmer = new SremovalStemmer();

			for (String p : OpenNLPTokenizer.tokenize(getText(), lang)) {
				p = TextUtils.removePunctuation(p);
				p = TextUtils.removeNonAlphabeticChars(p);
				p = p.toLowerCase().trim();

				// check if the phrase is valid after stem
				if (p != null && !p.isEmpty()) {
					p = stemmer.stem(p);
					if (StopwordsHandler.isStopwords(p) || p.length() < 3) {
						continue;
					}
					// combine
					if (getPhraseMap().containsKey(p)) {
						getPhraseMap().get(p).increaseTFContent();
					} else {
						property = new PhraseProperty();
						property.setNew(true)
								.setForwardURL(getAPIMeta().getForwardURL())
								.increaseTFContent();
						getPhraseMap().put(p, property);
					}
				}
			}
		} else if (lang.equals("zh-cn") || lang.equals("zh-tw")
				|| lang.equals("ja") || lang.equals("ka") || lang.equals("vi")) {
			LOG.info("Using CJKV tokenizer");

			CJKVTokenizer.feed(new StringReader(getText()));

			for (String p = CJKVTokenizer.next(); p != null; p = CJKVTokenizer
					.next()) {
				// System.out.println(p);

				if (p != null && !p.isEmpty() && p.length() > 1) {
					// combine
					if (getPhraseMap().containsKey(p)) {
						getPhraseMap().get(p).increaseTFContent();
					} else {
						property = new PhraseProperty();
						property.setNew(true)
								.setForwardURL(getAPIMeta().getForwardURL())
								.increaseTFContent();

						getPhraseMap().put(p, property);
					}
				}
			}
		} else {
			LOG.warning("Using basic tokenizer");
			// use default String.split()
			for (String p : BasicTokenizer.tokenize(getText(), lang)) {
				if (p != null && !p.isEmpty() && p.length() > 2) {
					// combine
					if (getPhraseMap().containsKey(p)) {
						getPhraseMap().get(p).increaseTFContent();
					} else {
						property = new PhraseProperty();
						property.setNew(true)
								.setForwardURL(getAPIMeta().getForwardURL())
								.increaseTFContent();

						getPhraseMap().put(p, property);
					}
				}
			}
		}

		parseProperties();
	}

	private void parseProperties() {
		LOG.info("Parsing fields");

		PhraseProperty property;

		// ---------title------------
		for (String s : getWebpageMeta().getTitle()) {
			if (getPhraseMap().containsKey(s)) {
				getPhraseMap().get(s).increaseTFTitle();
			} else { // not exists
				property = new PhraseProperty();
				property.increaseTFTitle().setNew(true)
						.setForwardURL(getAPIMeta().getForwardURL());

				getPhraseMap().put(s, property);
			}
		}
		// ------------metaKeywords-------------
		for (String s : getWebpageMeta().getKeywords()) {
			if (getPhraseMap().containsKey(s)) {
				getPhraseMap().get(s).increaseTFMetaKeywords();
			} else { // not exists
				property = new PhraseProperty();
				property.increaseTFMetaKeywords().setNew(true)
						.setForwardURL(getAPIMeta().getForwardURL());

				getPhraseMap().put(s, property);
			}
		}

		// -------------metaDescription--------------
		for (String s : getWebpageMeta().getDescription()) {

			if (getPhraseMap().containsKey(s)) {
				getPhraseMap().get(s).increaseTFMetaDescription();
			} else { // not exists
				property = new PhraseProperty();
				property.increaseTFMetaDescription().setNew(true)
						.setForwardURL(getAPIMeta().getForwardURL());

				getPhraseMap().put(s, property);
			}
		}
		// -------------anchor text--------------------
		for (RefererAnchorText text : getAPIMeta().getRefererAnchorTexts()) {
			String s = text.getAnchorText();

			if (getPhraseMap().containsKey(s)) {
				getPhraseMap().get(s).increaseTFAnchor();
			} else { // not exists
				property = new PhraseProperty();
				property.increaseTFAnchor().setNew(true)
						.setForwardURL(getAPIMeta().getForwardURL());

				getPhraseMap().put(s, property);
			}
		}
		// ------------------spider keywords-----------------
		for (String s : getAPIMeta().getSpiderKeywords()) {

			s = s.toLowerCase().trim();

			if (getPhraseMap().containsKey(s)) {
				getPhraseMap().get(s).setSpiderKeywords(true);
			} else { // not exists
				property = new PhraseProperty();
				property.setSpiderKeywords(true).setNew(true)
						.setForwardURL(getAPIMeta().getForwardURL());

				getPhraseMap().put(s, property);
			}
		}
		// ------------------admin keywords-----------------
		for (String s : getAPIMeta().getAdminKeywords()) {

			s = s.toLowerCase().trim();

			if (getPhraseMap().containsKey(s)) {
				getPhraseMap().get(s).setAdminKeywords(true);
			} else { // not exists
				property = new PhraseProperty();
				property.setAdminKeywords(true).setNew(true)
						.setForwardURL(getAPIMeta().getForwardURL());

				getPhraseMap().put(s, property);
			}
		}
		// ------------------user keywords-----------------
		for (String s : getAPIMeta().getUserKeywords()) {

			s = s.toLowerCase().trim();

			if (getPhraseMap().containsKey(s)) {
				getPhraseMap().get(s).setUserKeywords(true);
			} else { // not exists
				property = new PhraseProperty();
				property.setUserKeywords(true).setNew(true)
						.setForwardURL(getAPIMeta().getForwardURL());

				getPhraseMap().put(s, property);
			}
		}

		// tag part-of-speech, TF
		// @formatter:off
		for (Entry<String, PhraseProperty> e : getPhraseMap().entrySet()) {
			e.getValue()
			// .setPartOfSpeech(
			// PartOfSpeech.load(OpenNLPPOSTagger.getWordTag(e
			// .getKey())))
					.setTFScore(
							(double) e.getValue().getTFContent()
									/ getPhraseMap().size());
		}
		// @formatter:on
	}

	/**
	 * invoked when charset is detected
	 */
	@Override
	public void Notify(String result) {
		getWebpageMeta().setCharset(result);
	}

	// -----------------------getters and setters---------------------

	public void setHtml(String _html) {
		this._html = _html;
	}

	public String getHtml() {
		return _html;
	}

	public void setText(String _text) {
		this._text = _text;
	}

	public String getText() {
		return _text;
	}

	public void setAPIMeta(APIMeta _apiMeta) {
		this._apiMeta = _apiMeta;
	}

	public APIMeta getAPIMeta() {
		return _apiMeta;
	}

	public void setWebpageMeta(WebpageMeta _webpageMeta) {
		this._webpageMeta = _webpageMeta;
	}

	public WebpageMeta getWebpageMeta() {
		return _webpageMeta;
	}

	public void setPhraseMap(HashMap<String, PhraseProperty> _phraseMap) {
		this._phraseMap = _phraseMap;
	}

	public HashMap<String, PhraseProperty> getPhraseMap() {
		return _phraseMap;
	}

}
