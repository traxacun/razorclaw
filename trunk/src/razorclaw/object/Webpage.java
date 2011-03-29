package razorclaw.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import razorclaw.object.Dictionaries.*;
import razorclaw.parse.OpenNLPPOSTagger;
import razorclaw.parse.OpenNLPTokenizer;
import razorclaw.parse.PorterStemmer;
import razorclaw.parse.StopwordsHandler;
import razorclaw.parse.TextUtils;


public class Webpage implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -3083386200240092000L;

    private WebpageMeta _webpageMeta; // parsed from _document

    private APIMeta _apiMeta;

    private ArrayList<Phrase> _phrases = new ArrayList<Phrase>();

    private String[] _sentences;

    private String _url;

    private Status _status;

    // private Document _document;

    private String _html;

    private int _phrases_count = 0;

    public void parseHTML() {
	// load body
	Document doc = Jsoup.parse(_html);

	Element body = doc.body();

	// tokenize to sentences
	OpenNLPTokenizer.init();
	_sentences = OpenNLPTokenizer.tokenizeToSentence(body.text());

	// tokenize to phrases
	ArrayList<Phrase> mid = new ArrayList<Phrase>();
	for (String s : _sentences) {
	    for (String p : OpenNLPTokenizer.tokenizeToPhrases(s)) {
		// remove punctuation & non-ASCII chars
		p = TextUtils.removePunctuation(p);
		p = TextUtils.removeNonAlphabeticChars(p);
		p = p.toLowerCase().trim();

		if (p != null && !p.isEmpty() && p.length() > 0) {
		    mid.add(new Phrase(p));
		}
	    }
	}
	// load metadata
	_webpageMeta = new WebpageMeta();
	_webpageMeta.parseMeta(doc);

	Phrase phrase;
	for (String s : _webpageMeta.getTitle()) {
	    int idx = _phrases.indexOf(phrase = new Phrase(s));
	    if (idx == -1) {
		phrase.setInTitle(true);
		_phrases.add(phrase);
	    } else {
		_phrases.get(idx).setInTitle(true);
	    }
	}
	for (String s : _webpageMeta.getKeywords()) {
	    int idx = _phrases.indexOf(phrase = new Phrase(s));
	    if (idx == -1) {
		phrase.setInKeywords(true);
		_phrases.add(phrase);
	    } else {
		_phrases.get(idx).setInKeywords(true);
	    }
	}
	for (String s : _webpageMeta.getDescription()) {
	    int idx = _phrases.indexOf(phrase = new Phrase(s));
	    if (idx == -1) {
		phrase.setInDescription(true);
		_phrases.add(phrase);
	    } else {
		_phrases.get(idx).setInDescription(true);
	    }
	}
	// remove stopwords
	for (Phrase p : mid) {
	    if (StopwordsHandler.isStopwords(p.getPhrase())) {

	    } else {
		// stem
		PorterStemmer stemmer = new PorterStemmer();
		p.setPhrase(stemmer.stem(p.getPhrase()));
		_phrases.add(p);
		// total count
		_phrases_count++;
	    }
	}
	// get new temporary list
	mid = new ArrayList<Phrase>(_phrases);
	_phrases = new ArrayList<Phrase>();

	// merge
	Collections.sort(mid);
	Phrase current = null;
	for (Phrase p : mid) {
	    if (current == null) {
		current = p;
	    } else if (!current.equals(p)) {
		// save to result list
		_phrases.add(current);
		current = p;
	    } else if (current.equals(p)) {
		current.increaseOccurance();
	    } else {

	    }
	}

	// tag part-of-speech, TF
	for (Phrase p : _phrases) {
	    p.setPOS(OpenNLPPOSTagger.getWordTag(p.getPhrase()));

	    p.setTF((double) p.getOccurance() / _phrases_count);
	}	
    }

    // -----------------------getters and setters---------------------
    public WebpageMeta getMeta() {
	return _webpageMeta;
    }

    public String getUrl() {
	return _url;
    }

    public Status getStatus() {
	return _status;
    }

    public String[] getSentences() {
	return _sentences;
    }

    public ArrayList<Phrase> getPhrases() {
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
}
