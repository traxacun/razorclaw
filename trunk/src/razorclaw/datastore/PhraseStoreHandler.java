package razorclaw.datastore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheManager;
import razorclaw.object.PhraseProperty;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

/**
 * save or load phrase entities from datastore
 * 
 * @author Shuai YUAN
 * 
 */
public class PhraseStoreHandler {
    /**
     * local memcache
     */
    private static Cache _cache;

    private static DatastoreService _datastore = DatastoreServiceFactory
	    .getDatastoreService();

    /**
     * add a new forwardURL for a phrase to the local cache
     * 
     * @param phrase
     * @param pp
     */
    public static void put(String phrase, PhraseProperty pp) {
	if (phrase == null || phrase.isEmpty()) {
	    return;
	}

	if (_cache == null || _cache.isEmpty()) {
	    load();
	}
	synchronized (_cache) {
	    Object obj = _cache.get(phrase);
	    ArrayList<PhraseProperty> properties = null;
	    if (obj != null) {
		properties = (ArrayList<PhraseProperty>) obj;
	    }

	    if (properties != null) { // phrase existing?
		if (properties.contains(pp)) { // forwardURL existing?

		} else { // new property
		    properties.add(pp);
		    _cache.put(phrase, properties);
		    saveProperty(phrase, pp);
		}
	    } else { // new phrase
		properties = new ArrayList<PhraseProperty>();
		pp.setNew(true);
		properties.add(pp);
		_cache.put(phrase, properties);
		savePhrase(phrase);
		saveProperty(phrase, pp);
	    }
	}
    }

    /**
     * get all forwardURl properties for the given phrase
     * 
     * @param phrase
     * @return
     */
    public static ArrayList<PhraseProperty> get(String phrase) {
	if (_cache == null || _cache.isEmpty()) {
	    load();
	}
	synchronized (_cache) {
	    Object obj = _cache.get(phrase);
	    if (obj != null) {
		return (ArrayList<PhraseProperty>) obj;
	    } else {
		return null;
	    }
	}
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

    private static void load() {
	try {
	    // init local cache
	    if ((_cache = CacheManager.getInstance().getCache(
			"phrases_cache")) == null) {
		_cache = CacheManager.getInstance().getCacheFactory()
			    .createCache(Collections.emptyMap());
		CacheManager.getInstance().registerCache("phrases_cache",
			    _cache);
	    }
	    // load from datastore
	    Query query = new Query("PhraseProperty");
	    // result could be huge
	    List<Entity> result = _datastore.prepare(query).asList(
			FetchOptions.Builder.withDefaults());

	    if (result != null) {
		String phrase = null;
		ArrayList<PhraseProperty> properties = null;

		for (Entity e : result) {
		    if (phrase == null || phrase.isEmpty()) {
			phrase = e.getParent().getName(); // phrase
			properties = new ArrayList<PhraseProperty>();

		    } else if (phrase.equals(e.getParent().getName())) {

		    } else if (!phrase.equals(e.getParent().getName())) {
			_cache.put(phrase, properties);

			phrase = e.getParent().getName(); // phrase
			properties = new ArrayList<PhraseProperty>();
		    }

		    PhraseProperty pp = new PhraseProperty();
		    // properties
		    pp.setForwardURL((String) e.getProperty("ForwardURL"));
		    pp.setNew(false);
		    properties.add(pp);
		}
		if (properties != null && !properties.isEmpty()) { // leftover
		    _cache.put(phrase, properties);
		}
	    }
	} catch (Exception e) {

	}

    }

    private static void savePhrase(String phrase) {
	Entity phraseEntity = new Entity("Phrase", phrase);
	phraseEntity.setProperty("phrase", phrase);

	_datastore.put(phraseEntity);
    }

    /**
     * save a new PhraseProperty entity to datastore
     * 
     * NOTE: duplicate check is ignored. checked at put().
     * 
     * @param phrase
     * @param pp
     */
    private static void saveProperty(String phrase, PhraseProperty pp) {
	Entity phraseEntity = new Entity("Phrase", phrase);

	Entity propertyEntity = new Entity("PhraseProperty",
		phraseEntity.getKey());
	propertyEntity.setProperty("ForwardURL", pp.getForwardURL());

	_datastore.put(propertyEntity);
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
}
