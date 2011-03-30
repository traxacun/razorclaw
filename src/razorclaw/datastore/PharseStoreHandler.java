package razorclaw.datastore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

	private static HashMap<String, PhraseProperty> _cache;

	private static DatastoreService _datastore = DatastoreServiceFactory
			.getDatastoreService();

	public static void put(String phrase, PhraseProperty pp) {
		if (_cache != null) {

		}
	}

	public static PhraseProperty get(String phrase) {

	}

	private static void save() {

	}

	private static void load() {
		_cache = new ArrayList<Phrase>();

		Query query = new Query("PhraseProperty");
		List<Entity> result = _datastore.prepare(query).asList(
				FetchOptions.Builder.withDefaults());
		if (result != null) {
			Phrase p = null;
			PhraseProperty pp = null;

			for (Entity e : result) {
				if (p == null) {
					p = new Phrase(e.getParent().getName()); // phrase

				} else if (p.getPhrase().equals(e.getParent().getName())) {

				} else if (!p.getPhrase().equals(e.getParent().getName())) {
					_cache.add(p);

					p = new Phrase(e.getParent().getName()); // phrase

				}

				pp = new PhraseProperty();
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

				p.getProperties().add(pp);
			}
			_cache.add(p);
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
}
