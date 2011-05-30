package razorclaw.linguistic.parser;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 * use opennlp package to break content of a web page to phrases, collecting
 * basic statistic at the same time
 * 
 * @author Shuai YUAN
 * 
 */
public class OpenNLPTokenizer {
    private final static String SENTENCE_DETECTOR_BASE = "model/LANG-sent.bin";
    private final static String TOKENIZER_BASE = "model/LANG-token.bin";

    private static final Logger LOG = Logger.getLogger(OpenNLPTokenizer.class
	    .getName());

    private static Tokenizer _tokenizerEn, _tokenizerDa, _tokenizerDe,
	    _tokenizerNl, _tokenizerPt, _tokenizerSe;
    private static SentenceDetector _sentDetectorEn, _sentDetectorDa,
	    _sentDetectorDe, _sentDetectorNl, _sentDetectorPt, _sentDetectorSe;

    /**
     * load SentenceDetector and Tokenizer model
     */
    private static void init() {
	try {
	    // sentence detector models
	    _sentDetectorEn = new SentenceDetectorME(new SentenceModel(
			new FileInputStream(SENTENCE_DETECTOR_BASE.replace(
				"LANG", "en"))));
	    _sentDetectorDa = new SentenceDetectorME(new SentenceModel(
			new FileInputStream(SENTENCE_DETECTOR_BASE.replace(
				"LANG", "da"))));
	    _sentDetectorDe = new SentenceDetectorME(new SentenceModel(
			new FileInputStream(SENTENCE_DETECTOR_BASE.replace(
				"LANG", "de"))));
	    _sentDetectorNl = new SentenceDetectorME(new SentenceModel(
			new FileInputStream(SENTENCE_DETECTOR_BASE.replace(
				"LANG", "nl"))));
	    _sentDetectorPt = new SentenceDetectorME(new SentenceModel(
			new FileInputStream(SENTENCE_DETECTOR_BASE.replace(
				"LANG", "pt"))));
	    _sentDetectorSe = new SentenceDetectorME(new SentenceModel(
			new FileInputStream(SENTENCE_DETECTOR_BASE.replace(
				"LANG", "se"))));
	    // tokenizer models
	    _tokenizerEn = new TokenizerME(new TokenizerModel(
			new FileInputStream(
				TOKENIZER_BASE.replace("LANG", "en"))));
	    _tokenizerDa = new TokenizerME(new TokenizerModel(
			new FileInputStream(
				TOKENIZER_BASE.replace("LANG", "da"))));
	    _tokenizerDe = new TokenizerME(new TokenizerModel(
			new FileInputStream(
				TOKENIZER_BASE.replace("LANG", "de"))));
	    _tokenizerNl = new TokenizerME(new TokenizerModel(
			new FileInputStream(
				TOKENIZER_BASE.replace("LANG", "nl"))));
	    _tokenizerPt = new TokenizerME(new TokenizerModel(
			new FileInputStream(
				TOKENIZER_BASE.replace("LANG", "pt"))));
	    _tokenizerSe = new TokenizerME(new TokenizerModel(
			new FileInputStream(
				TOKENIZER_BASE.replace("LANG", "se"))));

	} catch (Exception e) {
	    LOG.severe("Loading language model failed");
	    e.printStackTrace();
	}
    }

    public static ArrayList<String> tokenize(String text, String lang) {
	Tokenizer tokenizer;
	SentenceDetector sentDetector;

	if (_tokenizerEn == null) {
	    init();
	}

	if (lang.equals("da")) {
	    tokenizer = _tokenizerDa;
	    sentDetector = _sentDetectorDa;
	} else if (lang.equals("de")) {
	    tokenizer = _tokenizerDe;
	    sentDetector = _sentDetectorDe;
	} else if (lang.equals("en")) {
	    tokenizer = _tokenizerEn;
	    sentDetector = _sentDetectorEn;
	} else if (lang.equals("nl")) {
	    tokenizer = _tokenizerNl;
	    sentDetector = _sentDetectorNl;
	} else if (lang.equals("pt")) {
	    tokenizer = _tokenizerPt;
	    sentDetector = _sentDetectorPt;
	} else if (lang.equals("se")) {
	    tokenizer = _tokenizerSe;
	    sentDetector = _sentDetectorSe;
	} else {
	    // no model found for the language
	    LOG.severe("No model found for the language " + lang
		    + ", using English as the default");

	    tokenizer = _tokenizerEn;
	    sentDetector = _sentDetectorEn;
	}

	try {
	    ArrayList<String> ret = new ArrayList<String>();

	    String[] sents = sentDetector.sentDetect(text);
	    if (sents != null && sents.length > 0) {
		for (String s : sents) {
		    String[] phrases = tokenizer.tokenize(s);
		    if (phrases != null && phrases.length > 0) {
			for (String p : phrases) {
			    if (p != null && !p.isEmpty()) {
				ret.add(p);
			    }
			}
		    }
		}
	    }
	    return ret;
	} catch (Exception e) {
	    e.printStackTrace();

	    return null;
	}
    }
}
