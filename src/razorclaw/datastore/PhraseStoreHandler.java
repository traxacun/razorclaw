package razorclaw.datastore;

import java.util.Collections;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.AsyncDatastoreService;

/**
 * save or load phrase entities from datastore
 * 
 * @author Shuai YUAN
 * 
 */
public class PhraseStoreHandler {
	private static final Logger LOG = Logger.getLogger(PhraseStoreHandler.class
			.getName());
	/**
	 * local memcache
	 */
	private static Cache _cache;

	private static DatastoreService _syncDatastore = DatastoreServiceFactory
			.getDatastoreService();

	private static AsyncDatastoreService _asyncDatastore = DatastoreServiceFactory
			.getAsyncDatastoreService();

	/**
	 * add a new phrase to the local cache and update datastore asynchronously
	 * 
	 * @param phrase
	 */
	public static void put(String phrase) {
		if (phrase == null || phrase.isEmpty()) {
			return;
		}

		if (_cache == null || _cache.isEmpty()) {
			try {
				load();
			} catch (CacheException e) {
				e.printStackTrace();
				LOG.severe("Loading phrase cache failed");

				return;
			}
		}

		long count = 0;
		synchronized (_cache) {
			Object obj = _cache.get(phrase);
			if (obj != null) {
				count = (Long) obj;
			} else {
				count = 0;
			}
			count++;

			// no automatically overwrite?
			_cache.remove(phrase);
			_cache.put(phrase, count);
		}

		// use phrase as the key to automatically overwrite duplicates
		Entity e = new Entity("Phrases", phrase);
		e.setProperty("Count", count);

		_asyncDatastore.put(e);
	}

	/**
	 * get all forwardURl properties for the given phrase
	 * 
	 * @param phrase
	 * @return
	 */
	public static long get(String phrase) {
		if (_cache == null || _cache.isEmpty()) {
			try {
				load();
			} catch (CacheException e) {
				e.printStackTrace();
				LOG.severe("Loading phrase cache failed");

				return 0;
			}
		}
		long count = 0;
		synchronized (_cache) {
			Object obj = _cache.get(phrase);
			if (obj != null) {
				count = (Long) obj;
			} else {
				count = 0;
			}
		}

		return count;
	}

	// private static void save() {
	// if (_cache != null) {
	// for (Entry<String, ArrayList<PhraseProperty>> e : (Set<Entry<String,
	// ArrayList<PhraseProperty>>>) _cache
	// .entrySet()) {
	// if (e.getValue() != null) {
	// for (PhraseProperty pp : e.getValue()) {
	// if (pp.isNew()) {
	// saveProperty(e.getKey(), pp);
	//
	// pp.setNew(false);
	// }
	// }
	// }
	// }
	// } else {
	//
	// }
	// }

	private static void load() throws CacheException {
		// init local cache
		if ((_cache = CacheManager.getInstance().getCache("phrases_cache")) == null) {
			_cache = CacheManager.getInstance().getCacheFactory()
					.createCache(Collections.emptyMap());
			CacheManager.getInstance().registerCache("phrases_cache", _cache);
		}
		// load from datastore
		Query query = new Query("Phrases");
		// result could be huge
		for (Entity e : _syncDatastore.prepare(query).asIterable()) {
			long count = (Long) e.getProperty("Count");

			_cache.put(e.getKey().getName(), count);
		}

		LOG.info("Phrase cache loaded");
	}
	//
	// private static void savePhrase(String phrase) {
	// Entity phraseEntity = new Entity("Phrase", phrase);
	// phraseEntity.setProperty("phrase", phrase);
	//
	// _syncDatastore.put(phraseEntity);
	// }
	//
	// /**
	// * save a new PhraseProperty entity to datastore
	// *
	// * NOTE: duplicate check is ignored. checked at put().
	// *
	// * @param phrase
	// * @param pp
	// */
	// private static void saveProperty(String phrase, PhraseProperty pp) {
	// Entity phraseEntity = new Entity("Phrase", phrase);
	//
	// Entity propertyEntity = new Entity("PhraseProperty",
	// phraseEntity.getKey());
	// propertyEntity.setProperty("ForwardURL", pp.getForwardURL());
	//
	// _syncDatastore.put(propertyEntity);
	// }

	// private static void updateAccessCounter() {
	// _accessCounter++;
	//
	// if (_accessCounter >= UPDATE_THRESHOLD) {
	// save(); // sync to datastore
	//
	// _accessCounter = 0;
	// }
	// }
}
