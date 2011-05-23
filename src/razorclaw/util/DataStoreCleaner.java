package razorclaw.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;

public class DataStoreCleaner extends HttpServlet {
	private static final long serialVersionUID = -5783708496632040L;

	private static DatastoreService _datastore = DatastoreServiceFactory
			.getDatastoreService();

	private static final Logger LOG = Logger.getLogger(DataStoreCleaner.class
			.getName());

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doExecute(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doExecute(req, resp);
	}

	public void doExecute(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String kind = req.getParameter("kind");

		run(kind);
	}

	/**
	 * remove data in <param>kind</param> in datastore
	 * 
	 * @param kind
	 */
	public static void run(String kind) {
		LOG.info("Purging data in " + kind);

		final Query query = new Query(kind);

		query.setKeysOnly();

		final ArrayList<Key> keys = new ArrayList<Key>();

		boolean finished = false;

		long count = 0;

		while (!finished) {
			for (final Entity entity : _datastore.prepare(query).asIterable(
					FetchOptions.Builder.withLimit(10240))) {
				keys.add(entity.getKey());
			}

			keys.trimToSize();

			if (keys.size() == 0) {
				finished = true;
			} else {
				count += keys.size();

				_datastore.delete(keys);
			}
		}

		LOG.info(count + " keys deleted");
	}

	/**
	 * remove all data in datastore
	 */
	public static void run() {

	}
}
