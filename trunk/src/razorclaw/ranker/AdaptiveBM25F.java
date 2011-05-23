package razorclaw.ranker;

import java.util.logging.Logger;

public class AdaptiveBM25F {
	private static final Logger LOG = Logger.getLogger(AdaptiveBM25F.class
			.getName());

	// average length(number of phrases) for different features
	private static final double _avgTitleLength = 3.5,
			_avgMetaKeywordsLength = 5.5, _avgMetaDescriptionLength = 10.5,
			_avgH1Length = 5.5, _avgH2Length = 10.5, _avgContentLength = 123.5,
			_avgAnchorLength = 4.5, _avgUserKeywordsLength = 3.0,
			_avgAdminKeywordsLength = 3.0, _avgSpiderKeywordsLength = 3.0;

	// length of phrases(number of characters)
	private static final double _avgLength = 5.5;

	private static final double _titleW = 13.5, _metaKeywordsW = 5.0,
			_metaDescriptionW = 3.0, _anchorW = 5.0, _userKeywordsW = 30.0,
			_adminKeywordsW = 20.0, _spiderKeywordsW = 5.0, _h1W = 2.0,
			_h2W = 1.5, _contentW = 1.0, _lengthW = 1.0;

	private static final double _contentB = 0.3, _titleB = 0.4,
			_metaKeywordsB = 0.4, _metaDescriptionB = 0.4, _anchorB = 0.4,
			_adminKeywordsB = 0.4, _spiderKeywordsB = 0.4,
			_userKeywordsB = 0.4, _h1B = 0.4, _h2B = 0.4, _lengthB = 0.4;

	private static final double _paraK = 4.9;

	public static void rank() {

	}
}
