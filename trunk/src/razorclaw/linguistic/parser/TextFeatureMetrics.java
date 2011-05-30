package razorclaw.linguistic.parser;

import java.util.List;

/*import ucl.GAE.razorclaw.linguistic.Document;
import ucl.GAE.razorclaw.linguistic.Word;*/

public class TextFeatureMetrics {
    private static int LONG_WORD_THRESHOLD = 6;

    public static double getReadability(String sentence) {
	String[] words = sentence.split(" ");
	int noLongWords = 0;
	for (String word : words) {
	    if (word.length() > LONG_WORD_THRESHOLD)
		noLongWords++;
	}
	double normalisedLongWords = ((double) noLongWords / 100)
		/ words.length;
	return words.length + normalisedLongWords;
    }

    public static double getReadability(List<String> sentences) {
	int noWords = 0;
	int noLongWords = 0;

	for (String sentence : sentences) {
	    String[] words = sentence.split(" ");
	    noWords += words.length;
	    for (String word : words) {
		if (word.length() > LONG_WORD_THRESHOLD)
		    noLongWords++;
	    }
	}

	double normalisedLongWords = ((double) noLongWords / 100) / noWords;
	double normalisedStdWords = (double) noWords / sentences.size();
	return normalisedStdWords + normalisedLongWords;
    }

    /*public static double getInformativity(String content) {
	return getInformativity(Document.loadAsAscii(content));
    }

    public static double getInformativity(Document document) {
	int noContentTerms = 0;
	int noFunctionTerms = 0;

	for (Word word : document.getWords()) {
	    // TODO: I am not sure I have made the right choices here - test
	    // this
	    switch (word.getPartOfSpeech()) {
	    case ADJECTIVE:
	    case ADVERB:
	    case DETERMINER:
	    case QUALIFIER:
		noContentTerms++;
	    case NOUN:
	    case PRONOUN:
	    case PROPERNOUN:
	    case VERB:
		noFunctionTerms++;
	    }

	}

	return (double) noContentTerms / noFunctionTerms;
    }*/
}
