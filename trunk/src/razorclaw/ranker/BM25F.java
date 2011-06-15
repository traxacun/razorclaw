package razorclaw.ranker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import razorclaw.datastore.DomainStoreHandler;
import razorclaw.datastore.PhraseStoreHandler;
import razorclaw.object.APIMeta;
import razorclaw.object.KeyPhraseScore;
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

	private static final double _titleW = 1.5, _metaKeywordsW = 1.0,
			_metaDescriptionW = 1.0, _anchorW = 1.5, _userKeywordsW = 1.5,
			_adminKeywordsW = 1.5, _spiderKeywordsW = 0.5, _h1W = 0.5,
			_h2W = 0.5, _contentW = 0.5, _lengthW = 0.5, _searchQueryW = 1.0,
			_POSW = 1.5;

	// 1st iteration
	// private static final double _titleW = 1.0, _metaKeywordsW = 1.0,
	// _metaDescriptionW = 1.0, _anchorW = 1.0, _userKeywordsW = 1.0,
	// _adminKeywordsW = 1.0, _spiderKeywordsW = 1.0, _h1W = 1.0,
	// _h2W = 1.0, _contentW = 1.0, _lengthW = 1.0, _searchQueryW = 1.0,
	// _POSW = 1.0;

	private static final double _contentB = 0.4, _titleB = 0.4,
			_metaKeywordsB = 0.4, _metaDescriptionB = 0.4, _anchorB = 0.4,
			_adminKeywordsB = 0.4, _spiderKeywordsB = 0.4,
			_userKeywordsB = 0.4, _h1B = 0.4, _h2B = 0.4, _lengthB = 0.4,
			_searchQueryB = 0.4, _POSB = 0.4;

	private static final double _paraK = 10;

	/**
     * 
     */
	public static ArrayList<KeyPhraseScore> rank(
			WebpageMeta webpageMeta,
			APIMeta apiMeta,
			HashMap<String, PhraseProperty> phrases) {

		ArrayList<KeyPhraseScore> ret = new ArrayList<KeyPhraseScore>();

		// rank all phrases against the document
		for (Entry<String, PhraseProperty> e : phrases.entrySet()) {
			KeyPhraseScore kps = new KeyPhraseScore();
			kps.setPhrase(e.getKey());

			// Score on different features
			// ---------------length----------------
			double lengthScore = _lengthW
					/ ((1 - _lengthB) + _lengthB * _avgLength
							/ e.getKey().length());
			kps.setLengthScore(lengthScore);
			// --------------title-------------------
			double titleScore = e.getValue().getTFTitle()
					* _titleW
					/ ((1 - _titleB) + _titleB * webpageMeta.getTitle().size()
							/ _avgTitleLength);
			kps.setTitleScore(titleScore);
			// ---------------meta keywords---------------
			double metaKeywordsScore = e.getValue().getTFMetaKeywords()
					* _metaKeywordsW
					/ ((1 - _metaKeywordsB) + _metaKeywordsB
							* webpageMeta.getKeywords().size()
							/ _avgMetaKeywordsLength);
			kps.setMetaKeywordsScore(metaKeywordsScore);
			// --------------meta description-----------------
			double metaDescriptionScore = e.getValue().getTFMetaDescription()
					* _metaDescriptionW
					/ ((1 - _metaDescriptionB) + _metaDescriptionB
							* webpageMeta.getDescription().size()
							/ _avgMetaDescriptionLength);
			kps.setMetaDescriptionScore(metaDescriptionScore);
			// ----------------------h1---------------------
			double h1Score = e.getValue().getTFH1()
					* _h1W
					/ ((1 - _h1B) + _h1B * webpageMeta.getH1().size()
							/ _avgH1Length);
			kps.setH1Score(h1Score);
			// ----------------------h2------------------
			double h2Score = e.getValue().getTFH2()
					* _h2W
					/ ((1 - _h2B) + _h2B * webpageMeta.getH2().size()
							/ _avgH2Length);
			kps.setH2Score(h2Score);
			// --------------------content-------------------
			double contentScore = e.getValue().getTFContent()
					* _contentW
					/ ((1 - _contentB) + _contentB * phrases.size()
							/ _avgContentLength);
			kps.setContentScore(contentScore);
			// ------------anchor text----------------
			double anchorTextScore = e.getValue().getTFAnchor()
					* _anchorW
					/ ((1 - _anchorB) + _anchorB
							* apiMeta.getRefererAnchorTexts().size()
							/ _avgAnchorLength);
			kps.setAnchorTextScore(anchorTextScore);
			// -----------spider keywords-------------
			double spiderKeywordsScore = 0.0;
			if (e.getValue().isSpiderKeywords()) {
				spiderKeywordsScore = _spiderKeywordsW
						/ ((1 - _spiderKeywordsB) + _spiderKeywordsB
								* apiMeta.getSpiderKeywords().size()
								/ _avgSpiderKeywordsLength);
			}
			kps.setSpiderKeywordsScore(spiderKeywordsScore);
			// -----------admin keywords-------------
			double adminKeywordsScore = 0.0;
			if (e.getValue().isAdminKeywords()) {
				adminKeywordsScore = _adminKeywordsW
						/ ((1 - _adminKeywordsB) + _adminKeywordsB
								* apiMeta.getAdminKeywords().size()
								/ _avgAdminKeywordsLength);
			}
			kps.setAdminKeywordsScore(adminKeywordsScore);
			// -----------user keywords-------------
			double userKeywordsScore = 0.0;
			if (e.getValue().isUserKeywords()) {
				userKeywordsScore = _userKeywordsW
						/ ((1 - _userKeywordsB) + _userKeywordsB
								* apiMeta.getUserKeywords().size()
								/ _avgUserKeywordsLength);
			}
			kps.setUserKeywordsScore(userKeywordsScore);
			// -----------search query-------------
			double searchQueryScore = e.getValue().getTFSearchQuery()
					* _searchQueryW
					/ ((1 - _searchQueryB) + _searchQueryB
							* e.getKey().length() / _avgSearchQueryLength);
			kps.setSearchQueryScore(searchQueryScore);
			// -------------part of speech-----------
			double POSScore = 0.0;
			if (e.getValue().getPartOfSpeech().equals("NOUN")) {
				POSScore = _POSW;
			}
			kps.setPOSScore(POSScore);

			// IDF
			// long count = PhraseStoreHandler.get(e.getKey());
			// double idfScore =
			// Math.log((DomainStoreHandler.getDocumentsNumber()
			// - count + 0.1)
			// / (count + 0.1));
			// kps.setIDFScore(idfScore);

			// pseudo score
			double pseudoScore = titleScore + metaKeywordsScore
					+ metaDescriptionScore + h1Score + h2Score + contentScore
					+ anchorTextScore + spiderKeywordsScore
					+ adminKeywordsScore + userKeywordsScore + lengthScore
					+ searchQueryScore + POSScore;
			kps.setPseudoScore(pseudoScore);

			// double score = idfScore * pseudoScore;
			kps.setBM25FScore(pseudoScore);
			// saturation
			// double score = idfScore * weightScore / (_paraK + weightScore);

			// record the score
			e.getValue().setBM25FScore(pseudoScore);

			ret.add(kps);
		}

		return ret;
	}
}
