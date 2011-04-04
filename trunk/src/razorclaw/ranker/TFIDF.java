package razorclaw.ranker;

import java.util.HashMap;
import java.util.Map.Entry;

import razorclaw.datastore.DomainStoreHandler;
import razorclaw.datastore.PhraseStoreHandler;
import razorclaw.object.PhraseProperty;

/**
 * TFIDF ranker for generating scores for a given list of phrases extracted from
 * the same webpage.
 * 
 * @author Shuai YUAN
 * 
 */
public class TFIDF {

    /**
     * compute the TFIDF score for all phrases in the given document
     * 
     * @param phrases
     *            phrases extracted from a webpage
     */
    public static void rank(String forwardURL,
	    HashMap<String, PhraseProperty> phrases) {
	if (phrases != null) {
	    double tfScore, idfScore, score;

	    for (Entry<String, PhraseProperty> e : phrases.entrySet()) {
		// get the TF
		tfScore = e.getValue().getTFScore();

		// get the IDF
		int documentCount = PhraseStoreHandler.get(e.getKey()).size();
		idfScore = Math
			.log((DomainStoreHandler.getDocumentsNumber() + 1)
				/ (documentCount + 1));

		// record the TFIDF
		score = tfScore * idfScore;

		e.getValue().setTFIDFScore(score);
	    }
	}
    }

}
