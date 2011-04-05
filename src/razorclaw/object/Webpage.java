package razorclaw.object;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import razorclaw.object.Dictionaries.PartOfSpeech;
import razorclaw.object.Dictionaries.Status;
import razorclaw.parser.OpenNLPPOSTagger;
import razorclaw.parser.OpenNLPTokenizer;
import razorclaw.parser.PorterStemmer;
import razorclaw.parser.StopwordsHandler;
import razorclaw.parser.TextUtils;

/**
 * represents a webpage crawled from a dot.tk domain NOTE: there could be
 * multiple dot.tk domains pointing to the same webpage
 * 
 * @author Shuai YUAN
 * 
 */
public class Webpage implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -3083386200240092000L;

    private WebpageMeta _webpageMeta; // parsed from _document

    private APIMeta _apiMeta; // parsed from stats.tk API

    private final HashMap<String, PhraseProperty> _phrases;

    private String[] _sentences;

    private Status _status;

    private String _html; // store HTML string instead of DOM to implement
			  // Serializable

    private String _keyPhrases;

    public Webpage() {
	_phrases = new HashMap<String, PhraseProperty>();
    }

    public void parseHTML() {
	try {
	    // load body
	    Document doc = Jsoup.parse(_html);

	    Element body = doc.body();

	    // tokenize to sentences
	    OpenNLPTokenizer.init();
	    _sentences = OpenNLPTokenizer.tokenizeToSentence(body.text());

	    // tokenize to phrases
	    PhraseProperty property;
	    PorterStemmer stemmer = new PorterStemmer();
	    for (String s : _sentences) {
		for (String p : OpenNLPTokenizer.tokenizeToPhrases(s)) {
		    // remove punctuation & non-ASCII chars
		    p = TextUtils.removePunctuation(p);
		    p = TextUtils.removeNonAlphabeticChars(p);
		    p = p.toLowerCase().trim();

		    // check if the phrase is valid
		    if (p != null && !p.isEmpty()
			    && !StopwordsHandler.isStopwords(p)) {
			// stem
			p = stemmer.stem(p);
			if (_phrases.containsKey(p)) {
			    _phrases.get(p).increaseOccurance();
			} else {
			    property = new PhraseProperty();
			    property.setNew(true);
			    _phrases.put(p, property);
			}
		    }
		}
	    }
	    // load metadata
	    _webpageMeta = new WebpageMeta();
	    _webpageMeta.parseMeta(doc);

	    for (String s : _webpageMeta.getTitle()) {
		if (_phrases.containsKey(s)) {
		    _phrases.get(s).setTitle(true).increaseOccurance();
		} else { // not exists
		    property = new PhraseProperty();
		    property.setNew(true).setTitle(true);

		    _phrases.put(s, property);
		}
	    }
	    for (String s : _webpageMeta.getKeywords()) {
		if (_phrases.containsKey(s)) {
		    _phrases.get(s).setMetaKeywords(true);
		} else { // not exists
		    property = new PhraseProperty();
		    property.setNew(true).setMetaKeywords(true);

		    _phrases.put(s, property);
		}
	    }
	    for (String s : _webpageMeta.getDescription()) {
		if (_phrases.containsKey(s)) {
		    _phrases.get(s).setMetaDescription(true);
		} else { // not exists
		    property = new PhraseProperty();
		    property.setNew(true).setMetaDescription(true);

		    _phrases.put(s, property);
		}
	    }

	    // tag part-of-speech, TF, ForwardURL
	    for (Entry<String, PhraseProperty> e : _phrases.entrySet()) {
		e.getValue().setPartOfSpeech(
			PartOfSpeech.load(OpenNLPPOSTagger.getWordTag(e
				.getKey())));
		e.getValue().setTFScore(
			(double) e.getValue().getOccurance() / _phrases.size());
		e.getValue().setForwardURL(_apiMeta.getForwardURL());
	    }
	} catch (Exception e) {
	    _status = Status.FAILED;
	}
    }

    // -----------------------getters and setters---------------------
    public WebpageMeta getWebpageMeta() {
	return _webpageMeta;
    }

    public Status getStatus() {
	return _status;
    }

    public String[] getSentences() {
	return _sentences;
    }

    public HashMap<String, PhraseProperty> getPhrases() {
	return _phrases;
    }

    public void setHtml(String _html) {
	this._html = _html;
    }

    public void setAPIMeta(APIMeta _apiMeta) {
	this._apiMeta = _apiMeta;
    }

    public APIMeta getAPIMeta() {
	return _apiMeta;
    }

    public void setStatus(Status _status) {
	this._status = _status;
    }

    public void setKeyPhrases(String _keyPhrases) {
	this._keyPhrases = _keyPhrases;
    }

    public String getKeyPhrases() {
	return _keyPhrases;
    }
}
