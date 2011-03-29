package razorclaw.parse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

/**
 * use opennlp package to break content of a web page to phrases, 
 * collecting basic statistic at the same time
 * 
 * @author Shuai YUAN
 *
 */
public class OpenNLPTokenizer {
    private static final String SENTENCE_DETECTOR_MODEL = "model/en-sent.bin";
    private static final String TOKENIZER_MODEL = "model/en-token.bin";

    private static Tokenizer _tokenizer = null;
    private static SentenceDetector _sentDetector = null;

    private OpenNLPTokenizer() {
    }

    /**
     * load SentenceDetector and Tokenizer model; initialise static instances
     * 
     */
    public static void init() {
	try {
	    // sentence detector model
	    if (_sentDetector == null) {
		_sentDetector = new SentenceDetectorME(new SentenceModel(
			new FileInputStream(SENTENCE_DETECTOR_MODEL)));
	    }
	    if (_tokenizer == null) {
		// tokenizer model
		_tokenizer = new TokenizerME(new TokenizerModel(
			new FileInputStream(TOKENIZER_MODEL)));
	    }

	} catch (InvalidFormatException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (FileNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    /**
     * break a paragraph into sentences.
     * 
     * @param bodyText
     * @return
     */
    public static String[] tokenizeToSentence(String bodyText) {
	/*
	 * List<String> retokenizedSentences = new ArrayList<String>(); for
	 * (String sentence : sentDetector.sentDetect(bodyText)) { String[]
	 * innerSentences = sentence.split("\\.' |\\. '");
	 * 
	 * for (int i = 0; i < innerSentences.length; i++) { if
	 * (innerSentences[i].trim().length() > 0) { if ((innerSentences.length
	 * == 1 && innerSentences[i] .length() < sentence.length()) || i + 1 !=
	 * innerSentences.length) { innerSentences[i] += ".' "; }
	 * retokenizedSentences.add(innerSentences[i]); } } }
	 */

	// return (String[]) retokenizedSentences.toArray(new String[0]);
	// return sentDetector.sentDetect(bodyText);
	return _sentDetector.sentDetect(bodyText);
    }

    /**
     * break a sentence into phrases.
     * 
     * @param sentenceText
     * @return
     */
    public static String[] tokenizeToPhrases(String sentenceText) {
	return _tokenizer.tokenize(sentenceText);
    }
}
