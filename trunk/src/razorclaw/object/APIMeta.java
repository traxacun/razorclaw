package razorclaw.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import razorclaw.object.Dictionaries.CountryCode;


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
    
    private ArrayList<String> _spiderKeywords, _adminKeywords, _userKeywords;
    private ArrayList<String> _referers;

    private HashMap<CountryCode, Double> _visitorCountries;
    private HashMap<String, String> _refererAnchorTexts;

    private String _forwardURL;
    private String _domainName;

    private CountryCode _registeredFrom;

    /**
     * parse the API response
     * 
     * @param obj
     */
    public APIMeta(JSONObject obj) {
	_spiderKeywords = new ArrayList<String>();
	_adminKeywords = new ArrayList<String>();
	_userKeywords = new ArrayList<String>();
	_referers = new ArrayList<String>();
	_visitorCountries = new HashMap<CountryCode, Double>();
	_refererAnchorTexts = new HashMap<String, String>();

	try {
	    _spiderKeywords = obj.getJSONArray("spider_keywords").toArrayList();
	    _adminKeywords = obj.getJSONArray("admin_keywords").toArrayList();
	    _userKeywords = obj.getJSONArray("user_keywords").toArrayList();
	    _referers = obj.getJSONArray("referers").toArrayList();

	    JSONArray array = obj.getJSONArray("visitor_countries");
	    for (int i = 0; i < array.length(); i++) {
		_visitorCountries.put(
			CountryCode.load(array.getJSONObject(i).getString("country")),
			array.getJSONObject(i).getDouble("percentage"));
	    }
	    array = obj.getJSONArray("referer_anchor_texts");
	    for (int i = 0; i < array.length(); i++) {
		_refererAnchorTexts.put(array.getJSONObject(i).getString("url"),
			array.getJSONObject(i).getString("anchor_text"));
	    }

	    _forwardURL = obj.getString("forwardurl");
	    _domainName = obj.getString("domainname");

	    _registeredFrom = CountryCode
		    .load(obj.getString("registered_from"));

	} catch (JSONException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
    
    public String toString(){
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
	return _spiderKeywords;
    }

    public ArrayList<String> getAdminKeywords() {
	return _adminKeywords;
    }

    public ArrayList<String> getUserKeywords() {
	return _userKeywords;
    }

    public HashMap<CountryCode, Double> getVisitorCountries() {
	return _visitorCountries;
    }

    public String getForwardURL() {
	return _forwardURL;
    }

    public CountryCode getRegisteredFrom() {
	return _registeredFrom;
    }

    public ArrayList<String> getReferers() {
	return _referers;
    }

    public HashMap<String, String> getRefererAnchorTexts() {
	return _refererAnchorTexts;
    }

    public String getDomainName() {
	return _domainName;
    }

}
