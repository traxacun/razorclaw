package razorclaw.ranker;

import java.util.ArrayList;

import razorclaw.datastore.DomainStoreHandler;
import razorclaw.datastore.PhraseStoreHandler;
import razorclaw.object.Phrase;

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
    public static void rank(String forwardURL, ArrayList<Phrase> phrases) {
	if (phrases != null) {
	    double tfScore, idfScore, score;

	    for (Phrase p : phrases) {
		// get the TF
		tfScore = p.getProperties().get(0).getTFScore();

		// get the IDF
		int documentCount = PhraseStoreHandler.get(p.getPhrase())
			.getProperties().size();
		idfScore = Math.log(DomainStoreHandler.getDocumentsNumber()
			/ documentCount);

		// record the TFIDF
		score = tfScore * idfScore;

		p.getProperties().get(0).setTFIDFScore(score);
	    }
	}
    }
}
