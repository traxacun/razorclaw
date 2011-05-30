package razorclaw.object;

import java.io.Serializable;

import razorclaw.object.Dictionaries.PartOfSpeech;

/**
 * holds properties for a phrases in a given document(forwardURL)
 * 
 * @author Shuai Yuan
 * 
 */
public class PhraseProperty implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7543270972725805035L;

	private String _forwardURL;

	private double _tfScore, _idfScore, _tfidfScore, _bm25fScore,
			_languageModelScore;

	private boolean _isSpiderKeywords, _isAdminKeywords, _isUserKeywords;

	private int _TFContent, _TFTitle, _TFH1, _TFH2, _TFMetaKeywords,
			_TFMetaDescription, _TFCaptital, _TFAnchor, _TFSearchQuery;

	private PartOfSpeech _partOfSpeech;
	/**
	 * flag for sync
	 */
	private boolean _isNew;

	public PhraseProperty() {
		_forwardURL = "";

		_TFContent = _TFTitle = _TFH1 = _TFH2 = _TFMetaDescription = _TFMetaKeywords = _TFCaptital = _TFAnchor = _TFSearchQuery = 0;

		_tfScore = _idfScore = _tfidfScore = _bm25fScore = _languageModelScore = 0.0;

		_isAdminKeywords = _isUserKeywords = _isSpiderKeywords = false;

		_isNew = true;

		_partOfSpeech = PartOfSpeech.UNSPECIFIED;
	}

	@Override
	public String toString() {
		return "TF in content: " + getTFContent() + " BM25F: "
				+ getBM25FScore() + "\n";
	}

	/**
	 * override to serve PhraseStoreHandler
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || _forwardURL == null) {
			return false;
		} else {
			return _forwardURL.equals(((PhraseProperty) obj).getForwardURL());
		}
	}

	// ------------------getter and setter-----------------------
	public PhraseProperty setForwardURL(String _forwardURL) {
		this._forwardURL = _forwardURL;
		return this;
	}

	public String getForwardURL() {
		return _forwardURL;
	}

	public PhraseProperty setTFScore(double _tfScore) {
		this._tfScore = _tfScore;
		return this;
	}

	public double getTFScore() {
		return _tfScore;
	}

	public PhraseProperty setIDFScore(double _idfScore) {
		this._idfScore = _idfScore;
		return this;
	}

	public double getIDFScore() {
		return _idfScore;
	}

	public PhraseProperty setTFIDFScore(double _tfidfScore) {
		this._tfidfScore = _tfidfScore;
		return this;
	}

	public double getTFIDFScore() {
		return _tfidfScore;
	}

	public PhraseProperty setBM25FScore(double _bm25fScore) {
		this._bm25fScore = _bm25fScore;
		return this;
	}

	public double getBM25FScore() {
		return _bm25fScore;
	}

	public PhraseProperty setLanguageModelScore(double _languageModelScore) {
		this._languageModelScore = _languageModelScore;
		return this;
	}

	public double getLanguageModelScore() {
		return _languageModelScore;
	}

	public PhraseProperty setPartOfSpeech(PartOfSpeech _partOfSpeech) {
		this._partOfSpeech = _partOfSpeech;
		return this;
	}

	public PartOfSpeech getPartOfSpeech() {
		return _partOfSpeech;
	}

	public PhraseProperty setNew(boolean _isNew) {
		this._isNew = _isNew;
		return this;
	}

	public boolean isNew() {
		return _isNew;
	}

	public PhraseProperty setAdminKeywords(boolean _isAdminKeywords) {
		this._isAdminKeywords = _isAdminKeywords;
		return this;
	}

	public boolean isAdminKeywords() {
		return _isAdminKeywords;
	}

	public PhraseProperty setSpiderKeywords(boolean _isSpiderKeywords) {
		this._isSpiderKeywords = _isSpiderKeywords;
		return this;
	}

	public boolean isSpiderKeywords() {
		return _isSpiderKeywords;
	}

	public PhraseProperty setUserKeywords(boolean _isUserKeywords) {
		this._isUserKeywords = _isUserKeywords;
		return this;
	}

	public boolean isUserKeywords() {
		return _isUserKeywords;
	}

	public PhraseProperty increaseTFContent() {
		this._TFContent++;
		return this;
	}

	public int getTFContent() {
		return _TFContent;
	}

	public PhraseProperty increaseTFTitle() {
		this._TFTitle++;
		return this;
	}

	public int getTFTitle() {
		return _TFTitle;
	}

	public PhraseProperty increaseTFH1() {
		this._TFH1++;
		return this;
	}

	public int getTFH1() {
		return _TFH1;
	}

	public PhraseProperty increaseTFH2() {
		this._TFH2++;
		return this;
	}

	public int getTFH2() {
		return _TFH2;
	}

	public PhraseProperty increaseTFMetaKeywords() {
		this._TFMetaKeywords++;
		return this;
	}

	public int getTFMetaKeywords() {
		return _TFMetaKeywords;
	}

	public PhraseProperty increaseTFMetaDescription() {
		this._TFMetaDescription++;
		return this;
	}

	public int getTFMetaDescription() {
		return _TFMetaDescription;
	}

	public PhraseProperty increaseTFAnchor() {
		this._TFAnchor++;
		return this;
	}

	public int getTFAnchor() {
		return _TFAnchor;
	}

	public PhraseProperty increaseTFCaptital() {
		this._TFCaptital++;
		return this;
	}

	public int getTFCaptital() {
		return _TFCaptital;
	}

	public PhraseProperty increaseTFSearchQuery() {
		this._TFSearchQuery++;
		return this;
	}

	public int getTFSearchQuery() {
		return _TFSearchQuery;
	}

}
