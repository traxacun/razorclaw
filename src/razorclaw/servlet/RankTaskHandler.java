package razorclaw.servlet;

import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;

@SuppressWarnings("deprecation")
public class RankTaskHandler extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 3562652882984169646L;

    private static final Logger LOG = Logger.getLogger(ParseTaskHandler.class
	    .getName());

    private void save() {

    }

    public static void createRankTask(String domain) {
	LOG.info("Creating rank task");

	try {
	    Queue queue = QueueFactory.getQueue("rank-queue");
	    queue.add(TaskOptions.Builder.url("/RankTaskHandler").param(
		    "domain", domain));
	} catch (Exception ex) {
	    ex.printStackTrace();

	    LOG.severe("Creating rank task failed");
	}
    }
}
