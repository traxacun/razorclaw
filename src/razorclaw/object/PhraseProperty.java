package razorclaw.object;

import razorclaw.object.Dictionaries.PartOfSpeech;

/**
 * holds properties for a phrases in a given document(forwardURL)
 * 
 * @author Shuai Yuan
 *
 */
public class PhraseProperty {
	private String _forwardURL;
	
	private int _occurance;
	
	private double _tfScore, _idfScore, _tfidfScore, _bm25fScore, _languageModelScore;
	
	private boolean _isTitle, _isH1, _isH2, _isMetaKeywords, _isMetaDescription, _isCapital;
	
	private PartOfSpeech _partOfSpeech;
	
	/**
	 * flag for sync
	 */
	private boolean _isNew;

	public void setForwardURL(String _forwardURL) {
		this._forwardURL = _forwardURL;
	}

	public String getForwardURL() {
		return _forwardURL;
	}

	public void setOccurance(int _occurance) {
		this._occurance = _occurance;
	}

	public int getOccurance() {
		return _occurance;
	}

	public void setTFScore(double _tfScore) {
		this._tfScore = _tfScore;
	}

	public double getTFScore() {
		return _tfScore;
	}

	public void setIDFScore(double _idfScore) {
		this._idfScore = _idfScore;
	}

	public double getIDFScore() {
		return _idfScore;
	}

	public void setTFIDFScore(double _tfidfScore) {
		this._tfidfScore = _tfidfScore;
	}

	public double getTFIDFScore() {
		return _tfidfScore;
	}

	public void setBM25FScore(double _bm25fScore) {
		this._bm25fScore = _bm25fScore;
	}

	public double getBM25FScore() {
		return _bm25fScore;
	}

	public void setLanguageModelScore(double _languageModelScore) {
		this._languageModelScore = _languageModelScore;
	}

	public double getLanguageModelScore() {
		return _languageModelScore;
	}

	public void setMetaDescription(boolean _isMetaDescription) {
		this._isMetaDescription = _isMetaDescription;
	}

	public boolean isMetaDescription() {
		return _isMetaDescription;
	}

	public void setMetaKeywords(boolean _isMetaKeywords) {
		this._isMetaKeywords = _isMetaKeywords;
	}

	public boolean isMetaKeywords() {
		return _isMetaKeywords;
	}

	public void setH2(boolean _isH2) {
		this._isH2 = _isH2;
	}

	public boolean isH2() {
		return _isH2;
	}

	public void setH1(boolean _isH1) {
		this._isH1 = _isH1;
	}

	public boolean isH1() {
		return _isH1;
	}

	public void setTitle(boolean _isTitle) {
		this._isTitle = _isTitle;
	}

	public boolean isTitle() {
		return _isTitle;
	}

	public void setCapital(boolean _isCapital) {
		this._isCapital = _isCapital;
	}

	public boolean isCapital() {
		return _isCapital;
	}

	public void setPartOfSpeech(PartOfSpeech _partOfSpeech) {
		this._partOfSpeech = _partOfSpeech;
	}

	public PartOfSpeech getPartOfSpeech() {
		return _partOfSpeech;
	}

	public void setNew(boolean _isNew) {
		this._isNew = _isNew;
	}

	public boolean isNew() {
		return _isNew;
	}

	public void increaseOccurance() {
	    _occurance ++;	    
	}
	
	
}
