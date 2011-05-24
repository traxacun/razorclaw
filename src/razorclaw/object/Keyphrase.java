package razorclaw.object;

/**
 * response the Keyphrase in JSON
 * 
 * @author Shuai YUAN
 * 
 */
public class KeyPhrase {
	private String _domain;
	private String _keyphrase;

	public void setDomain(String _domain) {
		this._domain = _domain;
	}

	public String getDomain() {
		return _domain;
	}

	public void setKeyphrase(String _keyphrase) {
		this._keyphrase = _keyphrase;
	}

	public String getKeyphrase() {
		return _keyphrase;
	}

	public void appendKeyPhrase(String s) {
		if (_keyphrase == null || _keyphrase.isEmpty()) {
			setKeyphrase(s);
		} else {
			_keyphrase += ",";
			_keyphrase += s;
		}
	}
}