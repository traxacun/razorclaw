package razorclaw.datastore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

/**
 * save or load dot.tk domain entities from datastore the structure is
 * <forwardURL, <domain, ...>>
 * 
 * @author Shuai YUAN
 * 
 */
public class DomainStoreHandler {
    /**
     * simple wrapper class to identify new entries in local cache
     * 
     * @author Shuai YUAN
     * 
     */
    static class DomainPair {
	private String _domain;
	private boolean _new;

	public DomainPair(String domain, boolean newEntry) {
	    _domain = domain;
	    _new = newEntry;
	}

	public void setNew(boolean _new) {
	    this._new = _new;
	}

	public boolean isNew() {
	    return _new;
	}

	public void setDomain(String _domain) {
	    this._domain = _domain;
	}

	public String getDomain() {
	    return _domain;
	}
    }

    private static DatastoreService _datastore = DatastoreServiceFactory
	    .getDatastoreService();

    /**
     * local cache
     */
    private static HashMap<String, ArrayList<DomainPair>> _cache;

    private static int _accessCounter = 0;

    private static final int UPDATE_THRESHOLD = 100;

    /**
     * check if a webpage(forwardURL) exists in the statistic datastore
     * 
     * @param forwardURL
     * @return
     */
    public static boolean exists(String forwardURL) {
	Query query = new Query("ForwardURL").setKeysOnly();
	query.addFilter("name", Query.FilterOperator.EQUAL, forwardURL);

	if (_datastore.prepare(query)
		.asList(FetchOptions.Builder.withDefaults()).isEmpty()) {
	    return false;
	} else {
	    return true;
	}
    }

    /**
     * add a new dot.tk domain for the given webpage(forwardURL)
     * 
     * @param forwardURL
     * @param domain
     */
    private static void putToDS(String forwardURL, String domain) {
	Entity pageEntity = new Entity("ForwardURL", forwardURL);
	pageEntity.setProperty("name", forwardURL);

	Entity domainEntity = new Entity("Domain", pageEntity.getKey());
	domainEntity.setProperty("name", domain);

	_datastore.put(pageEntity);
	_datastore.put(domainEntity);
    }

    /**
     * get the total number of webpages(forwardURL) in the statistic datastore
     * 
     * NOTE: GAE datastore statistics doesn't work locally
     * 
     * @return
     */
    public static long getDocumentsNumber() {
	return _cache.size();
	/*
	Query query = new Query("__Stat_Kind__");

	List<Entity> result = _datastore.prepare(query).asList(
		FetchOptions.Builder.withDefaults());
	if (!result.isEmpty()) {
	    for (Entity e : result) {
		if (e.getProperty("kind_name").equals("ForwardURL")) {
		    return (Long) e.getProperty("count");
		}
	    }
	} else {
	    // if running locally, iterate to get the total number
	    query = new Query("ForwardURL").setKeysOnly();
	    result = _datastore.prepare(query).asList(
		    FetchOptions.Builder.withDefaults());
	    if (!result.isEmpty()) {
		return result.size(); // NOTE: integer here
	    }
	}

	return 0;*/
    }

    /**
     * load cache from datastore
     */
    private static void load() {
	_cache = new HashMap<String, ArrayList<DomainPair>>();

	Query query = new Query("Domain");
	List<Entity> result = _datastore.prepare(query).asList(
		FetchOptions.Builder.withDefaults());
	if (result != null) {
	    String forwardURL = "";
	    ArrayList<DomainPair> domains = null;

	    for (Entity e : result) {
		if (forwardURL == null || forwardURL.isEmpty()) {
		    forwardURL = e.getParent().getName();

		    domains = new ArrayList<DomainPair>();
		    domains.add(new DomainPair((String) e.getProperty("name"),
			    false));
		    
		    System.out.println("new forwardURL: " + forwardURL);
		    System.out.println("new domain: " + e.getProperty("name"));
		} else if (forwardURL.equals(e.getParent().getName())) {
		    domains.add(new DomainPair((String) e.getProperty("name"),
			    false));
		    
		    System.out.println("new domain: " + e.getProperty("name"));
		} else if (!forwardURL.equals(e.getParent().getName())) {
		    _cache.put(forwardURL, domains);

		    forwardURL = e.getParent().getName();
		    domains = new ArrayList<DomainStoreHandler.DomainPair>();
		    domains.add(new DomainPair((String) e.getProperty("name"),
			    false));
		    
		    System.out.println("new forwardURL: " + forwardURL);
		    System.out.println("new domain: " + e.getProperty("name"));
		}
	    }

	    _cache.put(forwardURL, domains);
	}

    }

    /**
     * save cache to datastore
     */
    private static void save() {
	// iterate to find new entries
	for (Entry<String, ArrayList<DomainPair>> e : _cache.entrySet()) {
	    for (DomainPair dp : e.getValue()) {
		if (dp.isNew()) {
		    putToDS(e.getKey(), dp.getDomain());
		}
	    }
	}
    }

    /**
     * get dot.tk domains for the given webpage
     * 
     * @param forwardURL
     * @return
     */
    public static ArrayList<String> get(String forwardURL) {
	ArrayList<String> result = new ArrayList<String>();

	ArrayList<DomainPair> domains = _cache.get(forwardURL);
	if (domains != null) {
	    for (DomainPair dp : domains) {
		result.add(dp.getDomain());
	    }
	}
	
	updateAccessCounter();

	return result;
    }

    /**
     * set a new dot.tk domain for the given webpage
     * 
     * @param forwardURL
     * @param domain
     */
    public static void put(String forwardURL, String domain) {
	ArrayList<DomainPair> domains = _cache.get(forwardURL);
	
	if (domains == null) {
	    domains = new ArrayList<DomainStoreHandler.DomainPair>();
	    domains.add(new DomainPair(domain, true));
	    _cache.put(forwardURL, domains);
	} else {
	    if (domains.contains(domain)) {

	    } else {
		domains.add(new DomainPair(domain, true));
	    }
	}

	updateAccessCounter();
    }

    private static void updateAccessCounter() {
	_accessCounter++;

	if (_accessCounter > UPDATE_THRESHOLD) {
	    save(); // sync to datastore

	    _accessCounter = 0;
	}
    }

    public static void test() {
	// create new entities
	// DomainStoreHandler.put("forwardURL1", "domain11");
	// DomainStoreHandler.put("forwardURL1", "domain12");
	// DomainStoreHandler.put("forwardURL1", "domain13");
	//
	// DomainStoreHandler.put("forwardURL2", "domain21");
	// DomainStoreHandler.put("forwardURL2", "domain22");
	// DomainStoreHandler.put("forwardURL2", "domain23");
	
	load();
	
	DomainStoreHandler.put("forwardURL3", "domain31");
	DomainStoreHandler.put("forwardURL3", "domain32");
	DomainStoreHandler.put("forwardURL3", "domain33");	

	for (String s : DomainStoreHandler.get("forwardURL1")) {
	    System.out.println(s);
	}
	for (String s : DomainStoreHandler.get("forwardURL3")) {
	    System.out.println(s);
	}

	System.out.println(DomainStoreHandler.getDocumentsNumber());

//	System.out.println(DomainStoreHandler.exists("forwardURL1"));
//	System.out.println(DomainStoreHandler.exists("forwardURL3"));
	
	//save();
	
	/*load();
	
	for (String s : DomainStoreHandler.get("forwardURL1")) {
	    System.out.println(s);
	}
	for (String s : DomainStoreHandler.get("forwardURL3")) {
	    System.out.println(s);
	}

	System.out.println(DomainStoreHandler.getDocumentsNumber());*/
    }
}
