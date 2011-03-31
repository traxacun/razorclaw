package razorclaw.datastore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.google.appengine.api.datastore.*;

import razorclaw.object.Phrase;
import razorclaw.object.PhraseProperty;
import razorclaw.object.Dictionaries.PartOfSpeech;

/**
 * save or load phrase entities from datastore
 * 
 * @author Shuai YUAN
 * 
 */
public class PharseStoreHandler {

    private static HashMap<String, ArrayList<PhraseProperty>> _cache;

    private static DatastoreService _datastore = DatastoreServiceFactory
	    .getDatastoreService();

    private static int _accessCounter = 0;

    private static final int UPDATE_THRESHOLD = 100;

    public static void put(String phrase, PhraseProperty pp) {
	if (_cache != null) {

	} else {
	    load();
	}

	ArrayList<PhraseProperty> properties = _cache.get(phrase);
	if (properties != null) {

	} else {
	    properties = new ArrayList<PhraseProperty>();
	    _cache.put(phrase, properties);
	}
	properties.add(pp);

	updateAccessCounter();
    }

    public static Phrase get(String phrase) {
	if (_cache != null) {

	} else {
	    load();
	}

	Phrase ret = new Phrase(phrase);
	ret.setProperties(_cache.get(phrase));

	updateAccessCounter();

	return ret;
    }

    private static void save() {
	if (_cache != null) {
	    for (Entry<String, ArrayList<PhraseProperty>> e : _cache.entrySet()) {
		if (e.getValue() != null) {
		    for (PhraseProperty pp : e.getValue()) {
			if (pp.isNew()) {
			    putToDS(e.getKey(), pp);
			}
		    }
		}
	    }
	} else {

	}
    }

    private static void load() {
	_cache = new HashMap<String, ArrayList<PhraseProperty>>();

	Query query = new Query("PhraseProperty");
	// result could be huge
	List<Entity> result = _datastore.prepare(query).asList(
		FetchOptions.Builder.withDefaults());

	if (result != null) {
	    String phrase = "";
	    ArrayList<PhraseProperty> properties = null;

	    for (Entity e : result) {
		if (phrase == null) {
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

		pp.setOccurance((Integer) e.getProperty("Occurance"));
		pp.setTFScore((Double) e.getProperty("TF"));
		pp.setTFIDFScore((Double) e.getProperty("TFIDF"));
		pp.setBM25FScore((Double) e.getProperty("BM25F"));
		pp.setLanguageModelScore((Double) e
			.getProperty("LanguageModel"));

		pp.setTitle((Boolean) e.getProperty("InTitle"));
		pp.setMetaKeywords((Boolean) e.getProperty("InMetaKeywords"));
		pp.setMetaDescription((Boolean) e
			.getProperty("InMetaDescription"));
		pp.setH1((Boolean) e.getProperty("InH1"));
		pp.setH2((Boolean) e.getProperty("InH2"));

		pp.setPartOfSpeech(PartOfSpeech.load((String) e
			.getProperty("PartOfSpeech")));

		pp.setNew(false);

		properties.add(pp);
	    }
	    _cache.put(phrase, properties);
	}

    }

    /**
     * save a new PhraseProperty entity to datastore
     * 
     * NOTE: duplicate check is ignored
     * 
     * @param phrase
     * @param pp
     */
    private static void putToDS(String phrase, PhraseProperty pp) {
	Entity phraseEntity = new Entity("Phrase", phrase);
	phraseEntity.setProperty("phrase", phrase);

	Entity propertyEntity = new Entity("PhraseProperty",
		phraseEntity.getKey());
	propertyEntity.setProperty("ForwardURL", pp.getForwardURL());
	propertyEntity.setProperty("PartOfSpeech", pp.getPartOfSpeech());

	propertyEntity.setProperty("Occurance", pp.getOccurance());
	propertyEntity.setProperty("TF", pp.getTFScore());
	propertyEntity.setProperty("IDF", pp.getIDFScore());
	propertyEntity.setProperty("TFIDF", pp.getTFIDFScore());
	propertyEntity.setProperty("BM25F", pp.getBM25FScore());
	propertyEntity.setProperty("LanguageModelScore",
		pp.getLanguageModelScore());

	propertyEntity.setProperty("InTitle", pp.isTitle());
	propertyEntity.setProperty("InMetaKeywords", pp.isMetaKeywords());
	propertyEntity.setProperty("InMetaDescription", pp.isMetaDescription());
	propertyEntity.setProperty("InH1", pp.isH1());
	propertyEntity.setProperty("InH2", pp.isH2());

	_datastore.put(phraseEntity);
	_datastore.put(propertyEntity);
    }

    private static void updateAccessCounter() {
	_accessCounter++;

	if (_accessCounter > UPDATE_THRESHOLD) {
	    save(); // sync to datastore

	    _accessCounter = 0;
	}
    }    
}
