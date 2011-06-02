package razorclaw.datastore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;

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
	private static final Logger LOG = Logger.getLogger(DomainStoreHandler.class
			.getName());

	/**
	 * simple wrapper class to identify new entries in local cache
	 * 
	 * @author Shuai YUAN
	 * 
	 */
	static class SimpleDomainEntity implements Serializable {
		private static final long serialVersionUID = 3694451220038905611L;

		private String _domain;

		private String _language;
		/**
		 * if existing in datastore
		 */
		private boolean _new;

		public SimpleDomainEntity(String domain, String language,
				boolean newEntry) {
			_domain = domain;
			_language = language;
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

		@Override
		public boolean equals(Object domain) {
			return _domain.equals(((SimpleDomainEntity) domain).getDomain());
		}

		public void setLanguage(String _language) {
			this._language = _language;
		}

		public String getLanguage() {
			return _language;
		}
	}

	private static DatastoreService _datastore = DatastoreServiceFactory
			.getDatastoreService();

	/**
	 * local cache as <forwardURL, <domain, ...>>
	 */
	private static Cache _cache;

	/**
	 * check if a webpage(forwardURL) exists in the statistic datastore
	 * 
	 * @param forwardURL
	 * @return
	 */
	public static boolean exists(String forwardURL) {
		if (_cache == null || _cache.isEmpty()) {
			load();
		}

		return _cache.containsKey(forwardURL);
	}

	/**
	 * add a new dot.tk domain for the given webpage(forwardURL)
	 * 
	 * @param forwardURL
	 * @param domain
	 */
	public static void saveDomain(String domain) {
		LOG.info("Saving domains to datastore");

		Entity e = new Entity("Domains", domain);
		// pageEntity.setProperty("Name", forwardURL);

		// Entity domainEntity = new Entity("Domain", pageEntity.getKey());
		// domainEntity.setProperty("Name", domain);
		// domainEntity.setProperty("Language", language);

		_datastore.put(e);
	}

	/**
	 * create a new forwardURL in datastore. invoked immediately after
	 * discovering a new one.
	 * 
	 * @param forwardURL
	 */
	private static void saveForwardURL(String forwardURL) {
		LOG.info("Saving forwardURL to datastore");

		if (forwardURL == null || forwardURL.isEmpty()) {
			return;
		} else {
			Entity urlEntity = new Entity("ForwardURL", forwardURL);

			_datastore.put(urlEntity);
		}
	}

	/**
	 * get the total number of webpages(forwardURL) in the statistic datastore
	 * 
	 * NOTE: GAE datastore statistics doesn't work locally
	 * 
	 * @return
	 * @throws CacheException
	 */
	public static long getDocumentsNumber() {
		// synchronized (_cache) {
		// if (_cache == null || _cache.isEmpty()) {
		// load();
		// }
		//
		// return _cache.size();
		// }

		// ---now we try to get document number from Datastore stats---
		Query q = new Query("__Stat_Kind__");
		q.addFilter("kind_name", FilterOperator.EQUAL, "Domains");

		Entity stat = _datastore.prepare(q).asSingleEntity();

		if (stat != null) {
			return (Long) stat.getProperty("count");
		} else {
			return 0;
		}
	}

	/**
	 * load cache from datastore
	 * 
	 * @throws CacheException
	 */
	private static void load() {
		LOG.info("Loading domain cache");
		try {
			// init local cache
			if ((_cache = CacheManager.getInstance().getCache("domain_cache")) == null) {
				_cache = CacheManager.getInstance().getCacheFactory()
						.createCache(Collections.emptyMap());
				CacheManager.getInstance()
						.registerCache("domain_cache", _cache);
			}
		} catch (CacheException e) {
			LOG.severe("Creating domain cache failed");
			e.printStackTrace();
		}

		// load from datastore
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
							.getProperty("Language"), false));

				} else if (forwardURL.equals(e.getParent().getName())) {
					domains.add(new SimpleDomainEntity((String) e
							.getProperty("Name"), (String) e
							.getProperty("Language"), false));

				} else if (!forwardURL.equals(e.getParent().getName())) {
					_cache.put(forwardURL, domains);

					forwardURL = e.getParent().getName();
					domains = new ArrayList<DomainStoreHandler.SimpleDomainEntity>();
					domains.add(new SimpleDomainEntity((String) e
							.getProperty("Name"), (String) e
							.getProperty("Language"), false));
				}
			}
			if (domains != null && !domains.isEmpty()) { // leftover
				_cache.put(forwardURL, domains);
			}
		}
	}

	/**
	 * save cache to datastore
	 */
	// private static void save() {
	// synchronized (_cache) {
	// // iterate to find new entries
	// for (Entry<String, ArrayList<SimpleDomainEntity>> e : _cache
	// .entrySet()) {
	// if (e.getValue() != null) {
	// for (SimpleDomainEntity dp : e.getValue()) {
	// if (dp.isNew()) {
	// saveDomain(e.getKey(), dp.getDomain(),
	// dp.getLanguage());
	//
	// dp.setNew(false);
	// }
	// }
	// }
	// }
	// }
	// }

	/**
	 * get dot.tk domains for the given webpage
	 * 
	 * @param forwardURL
	 * @return
	 */
	// public static ArrayList<SimpleDomainEntity> get(String forwardURL) {
	// if (_cache == null || _cache.isEmpty()) {
	// load();
	// }
	//
	// synchronized (_cache) {
	// Object obj = _cache.get(forwardURL);
	// if (obj != null) {
	// return (ArrayList<SimpleDomainEntity>) obj;
	// } else {
	// return null;
	// }
	// }
	// }

	/**
	 * get the KeyPhrase directly for the given dot.tk domain. cannot provide
	 * the forwardURL yet, so we need to query datastore directly.
	 * 
	 * @param domain
	 * @return
	 */
	// public static String getKeyPhrase(String domain) {
	// Query query = new Query("Domain");
	// query.addFilter("Name", FilterOperator.EQUAL, domain);
	//
	// Entity result = _datastore.prepare(query).asSingleEntity();
	// if (result != null) {
	// return (String) result.getProperty("KeyPhrase");
	// } else {
	// return null;
	// }
	// }

	/**
	 * set a new dot.tk domain for the given webpage
	 * 
	 * @param forwardURL
	 * @param domain
	 */
	public static void put(String forwardURL, String domain, String language) {
		if (_cache == null || _cache.isEmpty()) {
			load();
		}

		synchronized (_cache) {
			Object obj = _cache.get(forwardURL);
			ArrayList<SimpleDomainEntity> domains = null;
			if (obj != null) {
				domains = (ArrayList<SimpleDomainEntity>) obj;
			}

			if (domains == null) { // forwardURL not existing?
				domains = new ArrayList<SimpleDomainEntity>();
				domains.add(new SimpleDomainEntity(domain, language, true));
				_cache.put(forwardURL, domains);

				saveForwardURL(forwardURL);
				// saveDomain(forwardURL, domain, language);
			} else { // forwardURL exists
				SimpleDomainEntity e = new SimpleDomainEntity(domain, language,
						true);

				if (domains.contains(e)) { // domain existing?
					// exists, ignore
				} else {
					domains.add(e);

					// saveDomain(forwardURL, domain, language);
				}
			}
		}
	}

	// private static void updateAccessCounter() {
	// _accessCounter++;
	//
	// if (_accessCounter >= UPDATE_THRESHOLD) {
	// save(); // sync to datastore
	//
	// _accessCounter = 0;
	// }
	// }

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
		 * load(); for (String s : DomainStoreHandler.get("forwardURL1")) {
		 * System.out.println(s); } for (String s :
		 * DomainStoreHandler.get("forwardURL3")) { System.out.println(s); }
		 * System.out.println(DomainStoreHandler.getDocumentsNumber());
		 */
	}
}
