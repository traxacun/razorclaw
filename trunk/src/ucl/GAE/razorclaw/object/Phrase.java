package ucl.GAE.razorclaw.object;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;

/**
 * basic unit after text extraction
 * 
 * @author Shuai YUAN
 * 
 */
public class Phrase implements Comparable<Phrase> {
    // private List<? extends Word> _words;
    private boolean _isCaseSentitive = false;

    /*
     * public Phrase(List<? extends Word> words) { _words = words; }
     */

    public Phrase(String s) {
	_phrase = s;
    }

    public void setCaseSensitive(boolean isCaseSentitive) {
	_isCaseSentitive = isCaseSentitive;
    }

    /*
     * public String getPhrase() { StringBuilder phrase = new StringBuilder();
     * for (Word word : _words) { phrase.append(word.getWord() + " "); }
     * 
     * return phrase.toString().substring(0, phrase.length() - 1); }
     */

    /*
     * public List<? extends Word> getWords() { return _words; }
     */

    /*
     * public int getNoWords() { return _words.size(); }
     */

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

    private String _phrase;

    // features list
    private int _occurance = 1;

    private double _tf = 0.0, _idf = 0.0, _tf_idf = 0.0;

    private String _pos;

    private boolean _inTitle = false, _inKeywords = false, _inDescription = false;

    // features list ends

    @Override
    public int compareTo(Phrase o) {
	//
	return getPhrase().compareTo(o.getPhrase());

    }

    public static class ByTF implements Comparator<Phrase> {
	@Override
	public int compare(Phrase arg0, Phrase arg1) {
	    return (arg0.getTF() > arg1.getTF()) ? 1 : 0;
	}
    }

    public static class ByIDF implements Comparator<Phrase> {
	@Override
	public int compare(Phrase arg0, Phrase arg1) {
	    return (arg0.getIDF() > arg1.getIDF()) ? 1 : 0;
	}
    }

    public static class ByTF_IDF implements Comparator<Phrase> {
	@Override
	public int compare(Phrase arg0, Phrase arg1) {
	    return (arg0.getTF_IDF() > arg1.getTF_IDF()) ? 1 : 0;
	}
    }

    public void setPhrase(String _phrase) {
	this._phrase = _phrase;
    }

    public String getPhrase() {
	return _phrase;
    }

    public int getOccurance() {
	return _occurance;
    }

    public void setTF(double _tf) {
	this._tf = _tf;
    }

    public double getTF() {
	return _tf;
    }

    public void setIDF(double _idf) {
	this._idf = _idf;
    }

    public double getIDF() {
	return _idf;
    }

    public void setTF_IDF(double _tf_idf) {
	this._tf_idf = _tf_idf;
    }

    public double getTF_IDF() {
	return _tf_idf;
    }

    public void setOccurance(int _occurance) {
	this._occurance = _occurance;
    }

    public void increaseOccurance() {
	this._occurance++;
    }

    public String toString() {
	DecimalFormat f = new DecimalFormat("#.####");

	StringBuilder sb = new StringBuilder();
	sb.append(_phrase);
	sb.append(": ");
	sb.append(_occurance);
	sb.append(", ");
	sb.append(f.format(_tf));
	sb.append(", ");
	sb.append(_pos);
	sb.append(", ");
	sb.append(_inTitle);
	sb.append(", ");
	sb.append(_inKeywords);
	sb.append(", ");
	sb.append(_inDescription);

	return sb.toString();
    }

    public void setPOS(String _pos) {
	this._pos = _pos;
    }

    public String getPOS() {
	return _pos;
    }

    public void setInTitle(boolean _inTitle) {
	this._inTitle = _inTitle;
    }

    public boolean isInTitle() {
	return _inTitle;
    }

    public void setInKeywords(boolean _inKeywords) {
	this._inKeywords = _inKeywords;
    }

    public boolean isInKeywords() {
	return _inKeywords;
    }

    public void setInDescription(boolean _inDescription) {
	this._inDescription = _inDescription;
    }

    public boolean isInDescription() {
	return _inDescription;
    }

}
