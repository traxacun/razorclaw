package razorclaw.ranker;

import java.util.Map.Entry;

import razorclaw.datastore.DomainStoreHandler;
import razorclaw.datastore.PhraseStoreHandler;
import razorclaw.object.PhraseProperty;
import razorclaw.object.Webpage;

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
     */
    public static void rank(Webpage webpage) {
	if (webpage.getPhrases() != null) {
	    double tfScore, idfScore, score;

	    for (Entry<String, PhraseProperty> e : webpage.getPhrases()
		    .entrySet()) {
		// get the TF
		tfScore = e.getValue().getTFScore();

		// get the IDF
		int documentCount = PhraseStoreHandler.get(e.getKey()).size();
		idfScore = Math.log((DomainStoreHandler.getDocumentsNumber()
			- documentCount + 0.5)
			/ (documentCount + 0.5));

		// record the TFIDF
		score = tfScore * idfScore;

		e.getValue().setTFIDFScore(score);
	    }
	}
    }

}
