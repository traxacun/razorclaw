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

    private ArrayList<Phrase> _phrases = new ArrayList<Phrase>();

    private String[] _sentences;

    private Status _status;

    private String _html; // store HTML string instead of DOM to implement
			  // Serializable

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
	Phrase phrase;
	PhraseProperty property;

	for (String s : _sentences) {
	    for (String p : OpenNLPTokenizer.tokenizeToPhrases(s)) {
		// remove punctuation & non-ASCII chars
		p = TextUtils.removePunctuation(p);
		p = TextUtils.removeNonAlphabeticChars(p);
		p = p.toLowerCase().trim();

		phrase = new Phrase(p);
		property = new PhraseProperty();
		phrase.getProperties().add(property);

		if (p != null && !p.isEmpty() && p.length() > 0) {
		    mid.add(phrase);
		}
	    }
	}
	// load metadata
	_webpageMeta = new WebpageMeta();
	_webpageMeta.parseMeta(doc);

	for (String s : _webpageMeta.getTitle()) {
	    int idx = _phrases.indexOf(phrase = new Phrase(s));
	    if (idx == -1) { // no such phrase
		phrase = new Phrase();
		phrase.getProperties().add(new PhraseProperty());
		phrase.getProperties().get(0).setTitle(true);

		_phrases.add(phrase);
	    } else {
		_phrases.get(idx).getProperties().get(0).setTitle(true);
	    }
	}
	for (String s : _webpageMeta.getKeywords()) {
	    int idx = _phrases.indexOf(phrase = new Phrase(s));
	    if (idx == -1) { // no such phrase
		phrase = new Phrase();
		phrase.getProperties().add(new PhraseProperty());
		phrase.getProperties().get(0).setMetaKeywords(true);

		_phrases.add(phrase);
	    } else {
		_phrases.get(idx).getProperties().get(0).setMetaKeywords(true);
	    }
	}
	for (String s : _webpageMeta.getDescription()) {
	    int idx = _phrases.indexOf(phrase = new Phrase(s));
	    if (idx == -1) { // no such phrase
		phrase = new Phrase();
		phrase.getProperties().add(new PhraseProperty());
		phrase.getProperties().get(0).setMetaDescription(true);

		_phrases.add(phrase);
	    } else {
		_phrases.get(idx).getProperties().get(0)
			.setMetaDescription(true);
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
	Collections.sort(mid); // sort by alphabet

	Phrase current = null;
	for (Phrase p : mid) {
	    if (current == null) {
		current = p;
	    } else if (!current.equals(p)) {
		// save to result list
		_phrases.add(current);
		current = p;
	    } else if (current.equals(p)) {
		current.getProperties().get(0).increaseOccurance();
	    } else {

	    }
	}

	// tag part-of-speech, TF
	for (Phrase p : _phrases) {
	    p.getProperties()
		    .get(0)
		    .setPartOfSpeech(
			    PartOfSpeech.load(OpenNLPPOSTagger.getWordTag(p
				    .getPhrase())));

	    p.getProperties()
		    .get(0)
		    .setTFScore(
			    (double) p.getProperties().get(0).getOccurance()
				    / _phrases_count);
	}
    }

    // -----------------------getters and setters---------------------
    public WebpageMeta getMeta() {
	return _webpageMeta;
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
