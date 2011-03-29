package razorclaw.datastore;

import java.util.ArrayList;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

/**
 * save or load document entities from datastore
 * 
 * @author Shuai YUAN
 * 
 */
public class DocumentStoreHandler {
    private static DatastoreService _datastore = DatastoreServiceFactory
	    .getDatastoreService();

    /**
     * check if a webpage(forwardURL) exists in the statistic datastore
     * 
     * @param forwardURL
     * @return
     */
    public static boolean exists(String forwardURL) {
	Query query = new Query("Document").setKeysOnly();
	query.addFilter("forwardURL", Query.FilterOperator.EQUAL, forwardURL);

	// PreparedQuery contains the methods for fetching query results
	// from the datastore
	PreparedQuery pq = _datastore.prepare(query);

	if (pq.asIterator().hasNext()) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * get all dot.tk domains for the given webpage(forwardURL)
     * 
     * @param forwardURL
     * @return
     */
    public static ArrayList<String> getDomains(String forwardURL) {
	Query query = new Query("Document");
	query.addFilter("forwardURL", Query.FilterOperator.EQUAL, forwardURL);
	PreparedQuery pq = _datastore.prepare(query);
	String domains = "";

	Entity e = pq.asSingleEntity();
	domains = (String) e.getProperty("domains");

	ArrayList<String> result = new ArrayList<String>();
	for (String s : domains.split("|")) {
	    if (s == null || s == "") {

	    } else {
		result.add(s);
	    }
	}

	return result;
    }

    /**
     * add a new dot.tk for the given webpage(forwardURL)
     * 
     * @param forwardURL
     * @param domain
     */
    public static void putDomains(String forwardURL, String domain) {
	ArrayList<String> domains = getDomains(forwardURL);
	if (domains.contains(domain)) {

	} else {
	    String newDomains = "";
	    for (String s : domains) {
		newDomains += s;
		newDomains += "|";
	    }

	    Entity e = new Entity("Document");
	    e.setProperty("forwardURL", forwardURL);
	    e.setProperty("domains", domains);

	    _datastore.put(e);
	}
    }

    /**
     * get the total number of webpages(forwardURL) in the statistic datastore
     * @return
     */
    public static long getDocumentsNumber() {
	Query query = new Query("__Stat_Kind__");
	query.addFilter("kind_name", Query.FilterOperator.EQUAL, "Document");
	Entity globalStat = _datastore.prepare(query).asSingleEntity();
	
	return (Long) globalStat.getProperty("count");
    }
}
