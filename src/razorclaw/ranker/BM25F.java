package razorclaw.ranker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import razorclaw.datastore.DomainStoreHandler;
import razorclaw.datastore.PhraseStoreHandler;
import razorclaw.object.APIMeta;
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
	    _avgMetaKeywordsLength = 5.5, _avgMetaDescriptionLength = 10.5,
	    _avgH1Length = 5.5, _avgH2Length = 10.5, _avgContentLength = 123.5,
	    _avgAnchorLength = 4.5, _avgUserKeywordsLength = 3.0,
	    _avgAdminKeywordsLength = 3.0, _avgSpiderKeywordsLength = 3.0;

    // length of phrases(number of characters)
    private static final double _avgPhraseLength = 5.5;

    private static final double _titleBoost = 13.5, _metaKeywordsBoost = 5.0,
	    _metaDescriptionBoost = 3.0, _anchorBoost = 5.0,
	    _userKeywordsBoost = 30.0, _adminKeywordsBoost = 20.0,
	    _spiderKeywordsBoost = 5.0, _h1Boost = 2.0, _h2Boost = 1.5,
	    _contentBoost = 1.0;

    private static final double _contentB = 0.3,
	    _paraK = 4.9,
	    _titleB = 0.4,
	    _metaKeywordsB = 0.4,
	    _metaDescriptionB = 0.4,
	    _anchorB = 0.4, _adminKeywordsB = 0.4, _spiderKeywordsB = 0.4,
	    _userKeywordsB = 0.4,
	    _h1B = 0.4, _h2B = 0.4;

    /**
     * 
     */
    public static void rank(WebpageMeta webpageMeta, APIMeta apiMeta,
	    HashMap<String, PhraseProperty> phrases) {

	// rank all phrases against the document
	for (Entry<String, PhraseProperty> e : phrases.entrySet()) {
	    // weight on different features
	    // --------------title-------------------
	    double titleWeight = 0.0;
	    if (e.getValue().isTitle()) {
		titleWeight = _titleBoost
			/ ((1 - _titleB) + _titleB
				* webpageMeta.getTitle().size()
				/ _avgTitleLength);
	    }
	    // ---------------meta keywords---------------
	    double metaKeywordsWeight = 0.0;
	    if (e.getValue().isMetaKeywords()) {
		metaKeywordsWeight = _metaKeywordsBoost
			/ ((1 - _metaKeywordsB) + _metaKeywordsB
				* webpageMeta.getKeywords().size()
				/ _avgMetaKeywordsLength);
	    }
	    // --------------meta description-----------------
	    double metaDescriptionWeight = 0.0;
	    if (e.getValue().isMetaDescription()) {
		metaDescriptionWeight = _metaDescriptionBoost
			/ ((1 - _metaDescriptionB) + _metaDescriptionB
				* webpageMeta.getDescription()
					.size() / _avgMetaDescriptionLength);
	    }
	    // ----------------------h1---------------------
	    double h1Weight = 0.0;
	    if (e.getValue().isH1()) {
		h1Weight = _h1Boost
			/ ((1 - _h1B) + _h1B
				* webpageMeta.getH1().size()
				/ _avgH1Length);
	    }
	    // ----------------------h2------------------
	    double h2Weight = 0.0;
	    if (e.getValue().isH2()) {
		h2Weight = _h2Boost
			/ ((1 - _h2B) + _h2B
				* webpageMeta.getH2().size()
				/ _avgH2Length);
	    }
	    // --------------------content-------------------
	    double contentWeight = 0.0;
	    contentWeight = _contentBoost
		    / ((1 - _contentB) + _contentB * phrases.size()
			    / _avgContentLength);
	    // ------------anchor text----------------
	    double anchorTextWeight = 0.0;
	    if (e.getValue().isAnchorText()) {
		anchorTextWeight = _anchorBoost
			/ ((1 - _anchorB) + _anchorB
				* apiMeta.getRefererAnchorTexts().size()
				/ _avgAnchorLength);
	    }
	    // -----------spider keywords-------------
	    double spiderKeywordsWeight = 0.0;
	    if (e.getValue().isSpiderKeywords()) {
		spiderKeywordsWeight = _spiderKeywordsBoost
			/ ((1 - _spiderKeywordsB) + _spiderKeywordsB
				* apiMeta.getSpiderKeywords().size()
				/ _avgSpiderKeywordsLength);
	    }
	    // -----------admin keywords-------------
	    double adminKeywordsWeight = 0.0;
	    if (e.getValue().isAdminKeywords()) {
		adminKeywordsWeight = _adminKeywordsBoost
			/ ((1 - _adminKeywordsB) + _adminKeywordsB
				* apiMeta.getAdminKeywords().size()
				/ _avgAdminKeywordsLength);
	    }
	    // -----------user keywords-------------
	    double userKeywordsWeight = 0.0;
	    if (e.getValue().isUserKeywords()) {
		userKeywordsWeight = _userKeywordsBoost
			/ ((1 - _userKeywordsB) + _userKeywordsB
				* apiMeta.getUserKeywords().size()
				/ _avgUserKeywordsLength);
	    }
	    // IDF
	    ArrayList<PhraseProperty> properties = PhraseStoreHandler.get(e
		    .getKey());
	    int documentCount = 1;
	    if (properties != null) {
		documentCount = properties.size();
	    }
	    double idfScore = Math.log((DomainStoreHandler.getDocumentsNumber()
		    - documentCount + 0.1)
		    / (documentCount + 0.1));

	    // final score
	    double weightScore = titleWeight + metaKeywordsWeight
		    + metaDescriptionWeight + h1Weight + h2Weight
		    + contentWeight + anchorTextWeight + spiderKeywordsWeight
		    + adminKeywordsWeight + userKeywordsWeight;

	    double score = weightScore / (_paraK + weightScore);

	    // record the score
	    e.getValue().setBM25FScore(score);
	}
    }
}
