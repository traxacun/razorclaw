package razorclaw.object;

public class KeyPhraseScore {

	private String _phrase;

	private double _titleScore, _metaKeywordsScore, _metaDescriptionScore,
			_h1Score, _h2Score, _contentScore, _anchorTextScore,
			_spiderKeywordsScore, _adminKeywordsScore, _userKeywordsScore,
			_lengthScore, _searchQueryScore, _POSScore, _pseudoScore,
			_idfScore, _BM25FScore, _UCBScore;

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getPhrase());
		sb.append("\t");
		sb.append(getUCBScore());
		sb.append("\t");
		sb.append(getBM25FScore());
		sb.append("\t");
		sb.append(getTitleScore());
		sb.append("\t");
		sb.append(getMetaKeywordsScore());
		sb.append("\t");
		sb.append(getMetaDescriptionScore());
		sb.append("\t");
		sb.append(getH1Score());
		sb.append("\t");
		sb.append(getH2Score());
		sb.append("\t");
		sb.append(getContentScore());
		sb.append("\t");
		sb.append(getAnchorTextScore());
		sb.append("\t");
		sb.append(getSpiderKeywordsScore());
		sb.append("\t");
		sb.append(getAdminKeywordsScore());
		sb.append("\t");
		sb.append(getUserKeywordsScore());
		sb.append("\t");
		sb.append(getLengthScore());
		sb.append("\t");
		sb.append(getSearchQueryScore());
		sb.append("\t");
		sb.append(getPOSScore());
		sb.append("\t");
		sb.append(getPseudoScore());
		sb.append("\t");
		sb.append(getIDFScore());

		return sb.toString();
	}

	public void setTitleScore(double _titleScore) {
		this._titleScore = _titleScore;
	}

	public double getTitleScore() {
		return _titleScore;
	}

	public void setPOSScore(double _POSScore) {
		this._POSScore = _POSScore;
	}

	public double getPOSScore() {
		return _POSScore;
	}

	public void setSearchQueryScore(double _searchQueryScore) {
		this._searchQueryScore = _searchQueryScore;
	}

	public double getSearchQueryScore() {
		return _searchQueryScore;
	}

	public void setLengthScore(double _lengthScore) {
		this._lengthScore = _lengthScore;
	}

	public double getLengthScore() {
		return _lengthScore;
	}

	public void setSpiderKeywordsScore(double _spiderKeywordsScore) {
		this._spiderKeywordsScore = _spiderKeywordsScore;
	}

	public double getSpiderKeywordsScore() {
		return _spiderKeywordsScore;
	}

	public void setAdminKeywordsScore(double _adminKeywordsScore) {
		this._adminKeywordsScore = _adminKeywordsScore;
	}

	public double getAdminKeywordsScore() {
		return _adminKeywordsScore;
	}

	public void setUserKeywordsScore(double _userKeywordsScore) {
		this._userKeywordsScore = _userKeywordsScore;
	}

	public double getUserKeywordsScore() {
		return _userKeywordsScore;
	}

	public void setAnchorTextScore(double _anchorTextScore) {
		this._anchorTextScore = _anchorTextScore;
	}

	public double getAnchorTextScore() {
		return _anchorTextScore;
	}

	public void setContentScore(double _contentScore) {
		this._contentScore = _contentScore;
	}

	public double getContentScore() {
		return _contentScore;
	}

	public void setH2Score(double _h2Score) {
		this._h2Score = _h2Score;
	}

	public double getH2Score() {
		return _h2Score;
	}

	public void setH1Score(double _h1Score) {
		this._h1Score = _h1Score;
	}

	public double getH1Score() {
		return _h1Score;
	}

	public void setMetaKeywordsScore(double _metaKeywordsScore) {
		this._metaKeywordsScore = _metaKeywordsScore;
	}

	public double getMetaKeywordsScore() {
		return _metaKeywordsScore;
	}

	public void setMetaDescriptionScore(double _metaDescriptionScore) {
		this._metaDescriptionScore = _metaDescriptionScore;
	}

	public double getMetaDescriptionScore() {
		return _metaDescriptionScore;
	}

	public void setPhrase(String _phrase) {
		this._phrase = _phrase;
	}

	public String getPhrase() {
		return _phrase;
	}

	public void setIDFScore(double _idfScore) {
		this._idfScore = _idfScore;
	}

	public double getIDFScore() {
		return _idfScore;
	}

	public void setPseudoScore(double _pseudoScore) {
		this._pseudoScore = _pseudoScore;
	}

	public double getPseudoScore() {
		return _pseudoScore;
	}

	public void setBM25FScore(double _BM25FScore) {
		this._BM25FScore = _BM25FScore;
	}

	public double getBM25FScore() {
		return _BM25FScore;
	}

	public void setUCBScore(double _UCBScore) {
		this._UCBScore = _UCBScore;
	}

	public double getUCBScore() {
		return _UCBScore;
	}
}
