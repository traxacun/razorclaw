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
import com.google.appengine.api.datastore.Query.FilterOperator;

/**
 * save or load dot.tk domain entities from datastore. the structure is
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
    static class SimpleDomainEntity {
	private String _domain;
	/**
	 * we pair KeyPhrase with Domain rather than webpage since the KeyPhrase
	 * may vary due to the information related to Domain
	 */
	private String _keyPhrase;
	/**
	 * if existing in datastore
	 */
	private boolean _new;

	public SimpleDomainEntity(String domain, String keyPhrase,
		boolean newEntry) {
	    _domain = domain;
	    _keyPhrase = keyPhrase;
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

	public void setKeyPhrase(String _keyPhrase) {
	    this._keyPhrase = _keyPhrase;
	}

	public String getKeyPhrase() {
	    return _keyPhrase;
	}
    }

    private static DatastoreService _datastore = DatastoreServiceFactory
	    .getDatastoreService();

    /**
     * local cache as <forwardURL, <domain, ...>>
     */
    private static HashMap<String, ArrayList<SimpleDomainEntity>> _cache;

    private static int _accessCounter = 0;

    private static final int UPDATE_THRESHOLD = 10;

    /**
     * check if a webpage(forwardURL) exists in the statistic datastore
     * 
     * @param forwardURL
     * @return
     */
    public static boolean exists(String forwardURL) {
	Query query = new Query("ForwardURL").setKeysOnly();
	query.addFilter("Name", Query.FilterOperator.EQUAL, forwardURL);

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
    private static void putToDS(String forwardURL, String domain,
	    String keyPhrase) {
	Entity pageEntity = new Entity("ForwardURL", forwardURL);
	pageEntity.setProperty("Name", forwardURL);

	Entity domainEntity = new Entity("Domain", pageEntity.getKey());
	domainEntity.setProperty("Name", domain);
	domainEntity.setProperty("KeyPhrase", keyPhrase);

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
	 * Query query = new Query("__Stat_Kind__");
	 * 
	 * List<Entity> result = _datastore.prepare(query).asList(
	 * FetchOptions.Builder.withDefaults()); if (!result.isEmpty()) { for
	 * (Entity e : result) { if
	 * (e.getProperty("kind_name").equals("ForwardURL")) { return (Long)
	 * e.getProperty("count"); } } } else { // if running locally, iterate
	 * to get the total number query = new
	 * Query("ForwardURL").setKeysOnly(); result =
	 * _datastore.prepare(query).asList(
	 * FetchOptions.Builder.withDefaults()); if (!result.isEmpty()) { return
	 * result.size(); // NOTE: integer here } }
	 * 
	 * return 0;
	 */
    }

    /**
     * load cache from datastore
     */
    private static void load() {
	_cache = new HashMap<String, ArrayList<SimpleDomainEntity>>();

	Query query = new Query("Domain");
	List<Entity> result = _datastore.prepare(query).asList(
		FetchOptions.Builder.withDefaults());
	if (result != null) {
	    String forwardURL = "";
	    ArrayList<SimpleDomainEntity> domains = null;

	    for (Entity e : result) {
		if (forwardURL == null || forwardURL.isEmpty()) {
		    forwardURL = e.getParent().getName();

		    domains = new ArrayList<SimpleDomainEntity>();
		    domains.add(new SimpleDomainEntity((String) e
			    .getProperty("Name"), (String) e
			    .getProperty("KeyPhrase"), false));

		} else if (forwardURL.equals(e.getParent().getName())) {
		    domains.add(new SimpleDomainEntity((String) e
			    .getProperty("Name"), (String) e
			    .getProperty("KeyPhrase"), false));

		} else if (!forwardURL.equals(e.getParent().getName())) {
		    _cache.put(forwardURL, domains);

		    forwardURL = e.getParent().getName();
		    domains = new ArrayList<DomainStoreHandler.SimpleDomainEntity>();
		    domains.add(new SimpleDomainEntity((String) e
			    .getProperty("Name"), (String) e
			    .getProperty("KeyPhrase"), false));

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
	for (Entry<String, ArrayList<SimpleDomainEntity>> e : _cache.entrySet()) {
	    for (SimpleDomainEntity dp : e.getValue()) {
		if (dp.isNew()) {
		    putToDS(e.getKey(), dp.getDomain(), dp.getKeyPhrase());
		}
	    }
	}
    }

    /**
     * get dot.tk domains for the given webpage
     * 
     * @param forwardURL
     * @return a HashMap<Domain, KeyPhrase> for the given webpage
     */
    public static HashMap<String, String> get(String forwardURL) {
	HashMap<String, String> ret = new HashMap<String, String>();

	ArrayList<SimpleDomainEntity> domains = _cache.get(forwardURL);
	if (domains != null) {
	    for (SimpleDomainEntity dp : domains) {
		ret.put(dp.getDomain(), dp.getKeyPhrase());
	    }
	}

	updateAccessCounter();

	return ret;
    }

    /**
     * get the KeyPhrase directly for the given dot.tk domain. cannot provide
     * the forwardURL yet, so we need to query datastore directly.
     * 
     * @param domain
     * @return
     */
    public static String getKeyPhrase(String domain) {
	Query query = new Query("Domain");
	query.addFilter("Name", FilterOperator.EQUAL, domain);

	Entity result = _datastore.prepare(query).asSingleEntity();
	if (result != null) {
	    return (String) result.getProperty("KeyPhrase");
	} else {
	    return null;
	}
    }

    /**
     * set a new dot.tk domain for the given webpage
     * 
     * @param forwardURL
     * @param domain
     */
    public static void put(String forwardURL, String domain, String keyPhrase) {
	ArrayList<SimpleDomainEntity> domains = _cache.get(forwardURL);

	if (domains == null) {
	    domains = new ArrayList<SimpleDomainEntity>();
	    domains.add(new SimpleDomainEntity(domain, keyPhrase, true));
	    _cache.put(forwardURL, domains);
	} else {
	    if (domains.contains(domain)) {

	    } else {
		domains.add(new SimpleDomainEntity(domain, keyPhrase, true));
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
	//
	// DomainStoreHandler.put("forwardURL3", "domain31");
	// DomainStoreHandler.put("forwardURL3", "domain32");
	// DomainStoreHandler.put("forwardURL3", "domain33");
	//
	// for (String s : DomainStoreHandler.get("forwardURL1")) {
	// System.out.println(s);
	// }
	// for (String s : DomainStoreHandler.get("forwardURL3")) {
	// System.out.println(s);
	// }
	//
	// System.out.println(DomainStoreHandler.getDocumentsNumber());

	// System.out.println(DomainStoreHandler.exists("forwardURL1"));
	// System.out.println(DomainStoreHandler.exists("forwardURL3"));

	// save();

	/*
	 * load();
	 * 
	 * for (String s : DomainStoreHandler.get("forwardURL1")) {
	 * System.out.println(s); } for (String s :
	 * DomainStoreHandler.get("forwardURL3")) { System.out.println(s); }
	 * 
	 * System.out.println(DomainStoreHandler.getDocumentsNumber());
	 */
    }
}
