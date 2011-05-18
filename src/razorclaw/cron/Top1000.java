package razorclaw.cron;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

/**
 * Get top1000 .tk domains from stats.tk/ucl/top100 every 1 hour.\n This is a
 * cron job in GAE.
 * 
 * @author Shuai Yuan
 * 
 */
public class Top1000 extends HttpServlet {

	private static final Logger log = Logger.getLogger(Top1000.class.getName());
	private static DatastoreService _datastore = DatastoreServiceFactory
			.getDatastoreService();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doExecute(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doExecute(req, resp);
	}

	protected void doExecute(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			// get all meta data from dot.tk API
			URLConnection conn = (new URL("http://www.stats.tk/ucl/top100"))
					.openConnection();
			conn.setConnectTimeout(0);
			conn.setReadTimeout(0);
			conn.setRequestProperty("UserAgent", "razorclaw");

			String s, content = "";
			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			while ((s = in.readLine()) != null) {
				content += s;
			}
			// parse the response
			DomainList domains = JSON.decode(content, DomainList.class);

			for (String domain : domains.domains) {
				// use domain as the key to remove duplication
				Entity domainEntity = new Entity("Top1000", domain);
				domainEntity.setProperty("name", domain);

				_datastore.put(domainEntity);
			}

		} catch (Exception e) {
			// timeout exception
			e.printStackTrace();

			log.severe("parsing top1000 list failed");
		}

		log.info("parsing top1000 list finished");
	}

	class DomainList {
		public ArrayList<String> domains;
	}
}
