package razorclaw.linguistic.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.InvalidFormatException;

/**
 * tag the words with part-of-speech(noun, verb, etc.) using Max-Entropy model
 * 
 * @author Shuai YUAN
 * 
 */
public class OpenNLPPOSTagger {
	private static final String POSTAGGER_MODEL = "model/en-pos-maxent.bin";
	private static POSTagger _tagger = null;

	private OpenNLPPOSTagger() {
	}

	/**
	 * load the POSTagger model if not already initialised
	 */
	public static void init() {
		if (_tagger == null) {
			try {
				_tagger = new POSTaggerME(new POSModel(new FileInputStream(
						POSTAGGER_MODEL)));
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static List<String> getWordsTags(List<String> words) {
		if (_tagger == null) {
			init();
		}
		return _tagger.tag(words);
	}

	public static String getWordTag(String word) {
		if (_tagger == null) {
			init();
		}
		String tag = _tagger.tag(word);
		// trim to the tag only
		return tag.substring(tag.indexOf("/") + 1);
	}
}
