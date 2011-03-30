package razorclaw.datastore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.*;

import razorclaw.object.Phrase;

/**
 * save or load phrase entities from datastore
 * 
 * @author Shuai YUAN
 * 
 */
public class PharseStoreHandler {
    private String _language;

    private String _encode;

    private static ArrayList<Phrase> _cache;

    private Date _lastSync;

    private static DatastoreService _datastore = DatastoreServiceFactory
	    .getDatastoreService();

    public static void put(Phrase phrase) {

    }

    public static Phrase get(String phrase) {

    }

    private static void save() {

    }

    private static void load() {
	_cache = new ArrayList<Phrase>();

	Query query = new Query("PhraseProperty");
	List<Entity> result = _datastore.prepare(query).asList(
		FetchOptions.Builder.withDefaults());
	if (result != null) {
	    Phrase p = 
	    for(Entity e : result){
		
	}
	    
	}

    }

    private static void saveToDS(Phrase phrase) {

    }
}
