package razorclaw.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

/**
 * use opennlp package to break content of a web page to phrases, collecting
 * basic statistic at the same time
 * 
 * @author Shuai YUAN
 * 
 */
public class OpenNLPTokenizer implements ITokenizer {
    private final String SENTENCE_DETECTOR_BASE = "model/LANG-sent.bin";
    private final String TOKENIZER_BASE = "model/LANG-token.bin";

    private Tokenizer _tokenizer = null;
    private SentenceDetector _sentDetector = null;

    private String _sentModel, _tokenModel;

    private static final Logger LOG = Logger.getLogger(OpenNLPTokenizer.class
	    .getName());

    /**
     * 2-letter language code
     */
    private String _lang;

    /**
     * load SentenceDetector and Tokenizer model
     */
    private void init() {
	// load corresponding language model
	if (_lang.equals("da") ||
		_lang.equals("de") ||
		_lang.equals("en") ||
		_lang.equals("nl") ||
		_lang.equals("pt") ||
		_lang.equals("se")) {

	} else {
	    // no model found for the language
	    LOG.severe("No model found for the language " + _lang
		    + ", using English as the default");
	    _lang = "en";
	}

	_sentModel = SENTENCE_DETECTOR_BASE.replace("LANG", _lang);
	LOG.info("Using sentence model: " + _sentModel);

	_tokenModel = TOKENIZER_BASE.replace("LANG", _lang);
	LOG.info("Using tokenizer model: " + _tokenModel);

	try {
	    // sentence detector model
	    if (_sentDetector == null) {
		_sentDetector = new SentenceDetectorME(new SentenceModel(
			new FileInputStream(_sentModel)));
	    }
	    if (_tokenizer == null) {
		// tokenizer model
		_tokenizer = new TokenizerME(new TokenizerModel(
			new FileInputStream(_tokenModel)));
	    }

	} catch (InvalidFormatException e) {
	    LOG.severe("Wrong language model format");
	    e.printStackTrace();
	} catch (FileNotFoundException e) {
	    LOG.severe("Language model file not found");
	    e.printStackTrace();
	} catch (IOException e) {
	    LOG.severe("Loading language model failed");
	    e.printStackTrace();
	}
    }

    @Override
    public ArrayList<String> tokenize(String text) {
	init();
	ArrayList<String> ret = new ArrayList<String>();

	String[] sents = _sentDetector.sentDetect(text);
	if (sents != null && sents.length > 0) {
	    for (String s : sents) {
		String[] phrases = _tokenizer.tokenize(s);
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
    }

    // --------------------getter and setter------------------------
    public void setLang(String _lang) {
	this._lang = _lang;
    }

    public String getLang() {
	return _lang;
    }
}
