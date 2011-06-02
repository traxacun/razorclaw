package razorclaw.ranker;

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
			_avgAdminKeywordsLength = 3.0, _avgSpiderKeywordsLength = 3.0,
			_avgSearchQueryLength = 2.5;

	// length of phrases(number of characters)
	private static final double _avgLength = 5.5;

	private static final double _titleW = 10.0, _metaKeywordsW = 1.5,
			_metaDescriptionW = 1.0, _anchorW = 5.0, _userKeywordsW = 10.0,
			_adminKeywordsW = 15.0, _spiderKeywordsW = 1.0, _h1W = 2.0,
			_h2W = 1.5, _contentW = 1.0, _lengthW = 1.0, _searchQueryW = 5.0,
			_POSW = 10.0;

	private static final double _contentB = 0.3, _titleB = 0.4,
			_metaKeywordsB = 0.4, _metaDescriptionB = 0.4, _anchorB = 0.4,
			_adminKeywordsB = 0.4, _spiderKeywordsB = 0.4,
			_userKeywordsB = 0.4, _h1B = 0.4, _h2B = 0.4, _lengthB = 0.4,
			_searchQueryB = 0.4, _POSB = 0.4;

	private static final double _paraK = 10;

	/**
     * 
     */
	public static void rank(WebpageMeta webpageMeta, APIMeta apiMeta,
			HashMap<String, PhraseProperty> phrases) {

		// rank all phrases against the document
		for (Entry<String, PhraseProperty> e : phrases.entrySet()) {
			// weight on different features
			// ---------------length----------------
			double lengthWeight = _lengthW
					/ ((1 - _lengthB) + _lengthB * _avgLength
							/ e.getKey().length());
			// --------------title-------------------
			double titleWeight = e.getValue().getTFTitle()
					* _titleW
					/ ((1 - _titleB) + _titleB * webpageMeta.getTitle().size()
							/ _avgTitleLength);
			// ---------------meta keywords---------------
			double metaKeywordsWeight = e.getValue().getTFMetaKeywords()
					* _metaKeywordsW
					/ ((1 - _metaKeywordsB) + _metaKeywordsB
							* webpageMeta.getKeywords().size()
							/ _avgMetaKeywordsLength);
			// --------------meta description-----------------
			double metaDescriptionWeight = e.getValue().getTFMetaDescription()
					* _metaDescriptionW
					/ ((1 - _metaDescriptionB) + _metaDescriptionB
							* webpageMeta.getDescription().size()
							/ _avgMetaDescriptionLength);
			// ----------------------h1---------------------
			double h1Weight = e.getValue().getTFH1()
					* _h1W
					/ ((1 - _h1B) + _h1B * webpageMeta.getH1().size()
							/ _avgH1Length);
			// ----------------------h2------------------
			double h2Weight = e.getValue().getTFH2()
					* _h2W
					/ ((1 - _h2B) + _h2B * webpageMeta.getH2().size()
							/ _avgH2Length);
			// --------------------content-------------------
			double contentWeight = e.getValue().getTFContent()
					* _contentW
					/ ((1 - _contentB) + _contentB * phrases.size()
							/ _avgContentLength);
			// ------------anchor text----------------
			double anchorTextWeight = e.getValue().getTFAnchor()
					* _anchorW
					/ ((1 - _anchorB) + _anchorB
							* apiMeta.getRefererAnchorTexts().size()
							/ _avgAnchorLength);
			// -----------spider keywords-------------
			double spiderKeywordsWeight = 0.0;
			if (e.getValue().isSpiderKeywords()) {
				spiderKeywordsWeight = _spiderKeywordsW
						/ ((1 - _spiderKeywordsB) + _spiderKeywordsB
								* apiMeta.getSpiderKeywords().size()
								/ _avgSpiderKeywordsLength);
			}
			// -----------admin keywords-------------
			double adminKeywordsWeight = 0.0;
			if (e.getValue().isAdminKeywords()) {
				adminKeywordsWeight = _adminKeywordsW
						/ ((1 - _adminKeywordsB) + _adminKeywordsB
								* apiMeta.getAdminKeywords().size()
								/ _avgAdminKeywordsLength);
			}
			// -----------user keywords-------------
			double userKeywordsWeight = 0.0;
			if (e.getValue().isUserKeywords()) {
				userKeywordsWeight = _userKeywordsW
						/ ((1 - _userKeywordsB) + _userKeywordsB
								* apiMeta.getUserKeywords().size()
								/ _avgUserKeywordsLength);
			}
			// -----------user keywords-------------
			double searchQueryWeight = e.getValue().getTFSearchQuery()
					* _searchQueryW
					/ ((1 - _searchQueryB) + _searchQueryB
							* e.getKey().length() / _avgSearchQueryLength);

			// -------------part of speech-----------
			double POSWeight = 0.0;
			if (e.getValue().getPartOfSpeech().equals("NOUN")) {
				POSWeight = 5.0;
			}
			// IDF
			// TODO: resume after test
			// long count = PhraseStoreHandler.get(e.getKey());
			// double idfScore =
			// Math.log((DomainStoreHandler.getDocumentsNumber()
			// - count + 0.1)
			// / (count + 0.1));

			// pseudo score
			double weightScore = titleWeight + metaKeywordsWeight
					+ metaDescriptionWeight + h1Weight + h2Weight
					+ contentWeight + anchorTextWeight + spiderKeywordsWeight
					+ adminKeywordsWeight + userKeywordsWeight + lengthWeight
					+ searchQueryWeight + POSWeight;

			// saturation
			// double score = idfScore * weightScore / (_paraK + weightScore);

			// record the score
			e.getValue().setBM25FScore(weightScore);

			// output
			// System.out.println(e.getKey() + ": " + score);
		}
	}
}
