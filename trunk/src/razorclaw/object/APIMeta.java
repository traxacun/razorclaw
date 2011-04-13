package razorclaw.object;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * holds metadata extracted from stats.tk API
 * 
 * @author Shuai YUAN
 * 
 */
public class APIMeta implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 5267241848326190002L;

    private ArrayList<String> _adminKeywords, _userKeywords;
    private ArrayList<String> _referers;

    private String _spiderKeywords;

    private ArrayList<VisitorCountry> _visitorCountries;
    private ArrayList<RefererAnchorText> _refererAnchorTexts;

    private String _forwardURL;
    private String _domainName;

    private String _registeredFrom;

    public class VisitorCountry {
	private String _country;
	private double _percentage;

	public void setCountry(String _country) {
	    this._country = _country;
	}

	public String getCountry() {
	    return _country;
	}

	public void setPercentage(double _percentage) {
	    this._percentage = _percentage;
	}

	public double getPercentage() {
	    return _percentage;
	}
    }

    public class RefererAnchorText {
	private String _url;
	private String _anchorText;

	public void setUrl(String _url) {
	    this._url = _url;
	}

	public String getUrl() {
	    return _url;
	}

	public void setAnchor_text(String _anchorText) {
	    this._anchorText = _anchorText;
	}

	public String getAnchorText() {
	    return _anchorText;
	}

    }

    /**
     * NOTE: possible mismatch, like car-care
     * 
     * @param s
     * @return
     */
    public boolean isAnchorText(String s) {
	for (RefererAnchorText text : _refererAnchorTexts) {
	    if (text.getAnchorText().contains(s)) {
		return true;
	    }
	}

	return false;
    }

    /**
     * parse the API response
     * 
     * @param obj
     */
    // public APIMeta(JSONObject obj) {
    // _spiderKeywords = new ArrayList<String>();
    // _adminKeywords = new ArrayList<String>();
    // _userKeywords = new ArrayList<String>();
    // _referers = new ArrayList<String>();
    // _visitorCountries = new HashMap<CountryCode, Double>();
    // _refererAnchorTexts = new HashMap<String, String>();
    //
    // try {
    // _spiderKeywords = obj.getJSONArray("spider_keywords").toArrayList();
    // _adminKeywords = obj.getJSONArray("admin_keywords").toArrayList();
    // _userKeywords = obj.getJSONArray("user_keywords").toArrayList();
    // _referers = obj.getJSONArray("referers").toArrayList();
    //
    // JSONArray array = obj.getJSONArray("visitor_countries");
    // for (int i = 0; i < array.length(); i++) {
    // _visitorCountries.put(
    // CountryCode.load(array.getJSONObject(i).getString(
    // "country")),
    // array.getJSONObject(i).getDouble("percentage"));
    // }
    // array = obj.getJSONArray("referer_anchor_texts");
    // for (int i = 0; i < array.length(); i++) {
    // _refererAnchorTexts.put(
    // array.getJSONObject(i).getString("url"),
    // array.getJSONObject(i).getString("anchor_text"));
    // }
    //
    // _forwardURL = obj.getString("forwardurl");
    // _domainName = obj.getString("domainname");
    //
    // _registeredFrom = CountryCode
    // .load(obj.getString("registered_from"));
    //
    // } catch (JSONException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }

    @Override
    public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append("domain: ");
	sb.append(_domainName);
	sb.append("\n");

	sb.append("forward URL: ");
	sb.append(_forwardURL);
	sb.append("\n");

	sb.append("registered from: ");
	sb.append(_registeredFrom.toString());
	sb.append("\n");

	sb.append("spider keywords: ");
	sb.append(_spiderKeywords.toString());
	sb.append("\n");

	sb.append("admin keywords: ");
	sb.append(_adminKeywords.toString());
	sb.append("\n");

	sb.append("user keywords: ");
	sb.append(_userKeywords.toString());
	sb.append("\n");

	sb.append("visitor countries: ");
	sb.append(_visitorCountries.toString());
	sb.append("\n");

	sb.append("referer anchor texts: ");
	sb.append(_refererAnchorTexts.toString());
	sb.append("\n");

	sb.append("referers: ");
	sb.append(_referers.toString());
	sb.append("\n");

	return sb.toString();
    }

    // -----------------------getters and setters---------------------
    public ArrayList<String> getSpiderKeywords() {
	ArrayList<String> ret = new ArrayList<String>();
	if (_spiderKeywords != null && !_spiderKeywords.isEmpty()) {
	    for (String s : _spiderKeywords.split(" ")) {
		ret.add(s);
	    }
	}
	return ret;
    }

    public ArrayList<String> getAdminKeywords() {
	return _adminKeywords;
    }

    public ArrayList<String> getUserKeywords() {
	return _userKeywords;
    }

    public ArrayList<VisitorCountry> getVisitorCountries() {
	return _visitorCountries;
    }

    public String getForwardURL() {
	return _forwardURL;
    }

    public String getRegisteredFrom() {
	return _registeredFrom;
    }

    public ArrayList<String> getReferers() {
	return _referers;
    }

    public ArrayList<RefererAnchorText> getRefererAnchorTexts() {
	return _refererAnchorTexts;
    }

    public String getDomainName() {
	return _domainName;
    }

    public void setSpider_keywords(String obj) {
	_spiderKeywords = obj;
    }

    public void setAdmin_keywords(ArrayList<String> obj) {
	_adminKeywords = obj;
    }

    public void setVisitor_countries(ArrayList<VisitorCountry> obj) {
	_visitorCountries = obj;
    }

    public void setReferers(ArrayList<String> obj) {
	_referers = obj;
    }

    public void setUser_keywords(ArrayList<String> obj) {
	_userKeywords = obj;
    }

    public void setRegistered_from(String obj) {
	_registeredFrom = obj;
    }

    public void setReferer_anchor_texts(ArrayList<RefererAnchorText> obj) {
	_refererAnchorTexts = obj;
    }

    public void setDomainname(String obj) {
	_domainName = obj;
    }

    public void setForwardurl(String obj) {
	_forwardURL = obj;
    }
}
