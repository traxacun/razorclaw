package razorclaw.object;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * basic unit after text extraction
 * 
 * @author Shuai YUAN
 * 
 */
public class Phrase implements Comparable<Phrase> {

    private boolean _isCaseSentitive = false;

    private String _phrase;

    private ArrayList<PhraseProperty> _properties = new ArrayList<PhraseProperty>();

    private String _language, _code;

    public Phrase(String s) {
	_phrase = s;
    }

    public Phrase() {
    }

    @Override
    public int hashCode() {
	return getPhrase().hashCode();
    }

    @Override
    public boolean equals(Object object) {
	Phrase altPhrase = (Phrase) object;
	if (_isCaseSentitive)
	    return altPhrase.getPhrase().equals(getPhrase());
	return altPhrase.getPhrase().equalsIgnoreCase(getPhrase());
    }

    // ----------------------comparator--------------------------
    @Override
    public int compareTo(Phrase o) {
	//
	return getPhrase().compareTo(o.getPhrase());

    }

    // ----------------------getter and setter--------------------
    public void setPhrase(String _phrase) {
	this._phrase = _phrase;
    }

    public String getPhrase() {
	return _phrase;
    }

    public void setCaseSensitive(boolean isCaseSentitive) {
	_isCaseSentitive = isCaseSentitive;
    }

    public void setProperties(ArrayList<PhraseProperty> _properties) {
	this._properties = _properties;
    }

    public ArrayList<PhraseProperty> getProperties() {
	return _properties;
    }
}
