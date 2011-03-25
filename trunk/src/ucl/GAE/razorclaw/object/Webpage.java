package ucl.GAE.razorclaw.object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import ucl.GAE.razorclaw.object.Dictionaries.*;
import ucl.GAE.razorclaw.parse.OpenNLPPOSTagger;
import ucl.GAE.razorclaw.parse.OpenNLPTokenizer;
import ucl.GAE.razorclaw.parse.PorterStemmer;
import ucl.GAE.razorclaw.parse.StopwordsHandler;
import ucl.GAE.razorclaw.parse.TextUtils;

public class Webpage {
    private WebpageMeta _meta;

    private ArrayList<Phrase> _phrases = new ArrayList<Phrase>();

    private String[] _sentences;

    private String _url;

    private Status _status;

    private Document _html;

    private int _phrases_count = 0;

    public void parseHTML() {
	// load body
	Element body = getHtml().body();

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

	// load metadata
	_meta = new WebpageMeta();
	_meta.parseHTML(getHtml());
	for (String s : _meta.getTitle()) {
	    int idx = _phrases.indexOf(new Phrase(s));
	    if (idx == -1) {

	    } else {
		_phrases.get(idx).setInTitle(true);
	    }
	}
	for (String s : _meta.getKeywords()) {
	    int idx = _phrases.indexOf(new Phrase(s));
	    if (idx == -1) {

	    } else {
		_phrases.get(idx).setInKeywords(true);
	    }
	}
	for (String s : _meta.getDescription()) {
	    int idx = _phrases.indexOf(new Phrase(s));
	    if (idx == -1) {

	    } else {
		_phrases.get(idx).setInDescription(true);
	    }
	}
    }

    // -----------------------getters and setters---------------------
    public WebpageMeta getMeta() {
	return _meta;
    }

    public String getUrl() {
	return _url;
    }

    public Status getStatus() {
	return _status;
    }

    public Document getHtml() {
	return _html;
    }

    public String[] getSentences() {
	return _sentences;
    }

    public ArrayList<Phrase> getPhrases() {
	return _phrases;
    }

    public void setHtml(Document _html) {
	this._html = _html;
    }
}
