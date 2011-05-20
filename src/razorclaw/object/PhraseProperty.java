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

	private int _occurance;

	private double _tfScore, _idfScore, _tfidfScore, _bm25fScore,
			_languageModelScore;

	private boolean _isTitle, _isH1, _isH2, _isMetaKeywords,
			_isMetaDescription, _isCapital, _isAnchorText, _isSpiderKeywords,
			_isAdminKeywords, _isUserKeywords;

	private int _TFContent, _TFTitle, _TFH1, _TFH2, _TFMetaKeywords,
			_TFMetaDescription, _TFCaptital, _TFAnchor;

	private PartOfSpeech _partOfSpeech;
	/**
	 * flag for sync
	 */
	private boolean _isNew;

	public PhraseProperty() {
		_forwardURL = "";

		_occurance = 1;

		_tfScore = _idfScore = _tfidfScore = _bm25fScore = _languageModelScore = 0.0;

		_isTitle = _isH1 = _isH2 = _isMetaKeywords = _isMetaDescription = _isCapital = _isAnchorText = _isAdminKeywords = _isUserKeywords = _isSpiderKeywords = false;

		_isNew = true;

		_partOfSpeech = PartOfSpeech.UNSPECIFIED;
	}

	@Override
	public String toString() {
		return "Occurance: " + _occurance + " BM25F: " + _bm25fScore
				+ " isAnchor: " + _isAnchorText + "\n";
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

	public PhraseProperty setOccurance(int _occurance) {
		this._occurance = _occurance;
		return this;
	}

	public int getOccurance() {
		return _occurance;
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

	public PhraseProperty setMetaDescription(boolean _isMetaDescription) {
		this._isMetaDescription = _isMetaDescription;
		return this;
	}

	public boolean isMetaDescription() {
		return _isMetaDescription;
	}

	public PhraseProperty setMetaKeywords(boolean _isMetaKeywords) {
		this._isMetaKeywords = _isMetaKeywords;
		return this;
	}

	public boolean isMetaKeywords() {
		return _isMetaKeywords;
	}

	public PhraseProperty setH2(boolean _isH2) {
		this._isH2 = _isH2;
		return this;
	}

	public boolean isH2() {
		return _isH2;
	}

	public PhraseProperty setH1(boolean _isH1) {
		this._isH1 = _isH1;
		return this;
	}

	public boolean isH1() {
		return _isH1;
	}

	public PhraseProperty setTitle(boolean _isTitle) {
		this._isTitle = _isTitle;
		return this;
	}

	public boolean isTitle() {
		return _isTitle;
	}

	public PhraseProperty setCapital(boolean _isCapital) {
		this._isCapital = _isCapital;
		return this;
	}

	public boolean isCapital() {
		return _isCapital;
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

	public PhraseProperty increaseOccurance() {
		_occurance++;
		return this;
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

	public PhraseProperty setAnchorText(boolean _isAnchorText) {
		this._isAnchorText = _isAnchorText;
		return this;
	}

	public boolean isAnchorText() {
		return _isAnchorText;
	}

	public PhraseProperty setUserKeywords(boolean _isUserKeywords) {
		this._isUserKeywords = _isUserKeywords;
		return this;
	}

	public boolean isUserKeywords() {
		return _isUserKeywords;
	}

}
