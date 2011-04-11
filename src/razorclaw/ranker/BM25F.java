package razorclaw.ranker;

import java.util.HashMap;
import java.util.Map.Entry;

import razorclaw.datastore.DomainStoreHandler;
import razorclaw.datastore.PhraseStoreHandler;
import razorclaw.object.PhraseProperty;
import razorclaw.object.WebpageMeta;

/**
 * for reference http://nlp.uned.es/~jperezi/Lucene-BM25/
 * 
 * @author Shuai YUAN
 * 
 */
public class BM25F {
    // average length(number of phrases) for different features
    private static final double _avgTitleLength = 3.5,
	    _avgMetaKeywordsLength = 5.5, _avgMetaDescriptionLength = 10.5;
    private static final double _avgH1Length = 5.5, _avgH2Length = 10.5;
    private static final double _avgContentLength = 123.5;

    // length of phrases(number of characters)
    private static final double _avgPhraseLength = 5.5;

    private static final double _titleBoost = 1.0, _metaKeywordsBoost = 1.0,
	    _metaDescriptionBoost = 1.0;
    private static final double _h1Boost = 1.0, _h2Boost = 1.0;
    private static final double _contentBoost = 1.0;

    private static final double _paraB = 0.75, _paraK = 2.0;

    /**
     * 
     */
    public static void rank(WebpageMeta webpageMeta,
	    HashMap<String, PhraseProperty> phrases) {

	// rank all phrases against the document
	for (Entry<String, PhraseProperty> e : phrases.entrySet()) {
	    // weight on different features
	    // title
	    double titleWeight = 0.0;
	    if (e.getValue().isTitle()) {
		titleWeight = _titleBoost
			/ ((1 - _paraB) + _paraB
				* webpageMeta.getTitle().size()
				/ _avgTitleLength);
	    }
	    // meta keywords
	    double metaKeywordsWeight = 0.0;
	    if (e.getValue().isMetaKeywords()) {
		metaKeywordsWeight = _metaKeywordsBoost
			/ ((1 - _paraB) + _paraB
				* webpageMeta.getKeywords().size()
				/ _avgMetaKeywordsLength);
	    }
	    // meta description
	    double metaDescriptionWeight = 0.0;
	    if (e.getValue().isMetaDescription()) {
		metaDescriptionWeight = _metaDescriptionBoost
			/ ((1 - _paraB) + _paraB
				* webpageMeta.getDescription()
					.size() / _avgMetaDescriptionLength);
	    }
	    // h1
	    double h1Weight = 0.0;
	    if (e.getValue().isH1()) {
		h1Weight = _h1Boost
			/ ((1 - _paraB) + _paraB
				* webpageMeta.getH1().size()
				/ _avgH1Length);
	    }
	    // h2
	    double h2Weight = 0.0;
	    if (e.getValue().isH2()) {
		h2Weight = _h2Boost
			/ ((1 - _paraB) + _paraB
				* webpageMeta.getH2().size()
				/ _avgH2Length);
	    }
	    // content
	    double contentWeight = 0.0;
	    contentWeight = _contentBoost
		    / ((1 - _paraB) + _paraB * phrases.size()
			    / _avgContentLength);

	    // IDF
	    int documentCount = PhraseStoreHandler.get(e.getKey()).size() + 1;
	    double idfScore = Math.log((DomainStoreHandler.getDocumentsNumber()
		    - documentCount + 0.5)
		    / (documentCount + 0.5));

	    // final score
	    double weightScore = titleWeight + metaKeywordsWeight
		    + metaDescriptionWeight + h1Weight + h2Weight
		    + contentWeight;
	    double score = idfScore * weightScore / (_paraK + weightScore);

	    // record the score
	    e.getValue().setBM25FScore(score);
	}
    }
}
