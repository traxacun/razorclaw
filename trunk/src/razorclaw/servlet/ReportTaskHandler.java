package razorclaw.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;

/**
 * report generated <Domain, KeyPhrase> to dot.tk
 * 
 * @author Shuai YUAN
 * 
 */
@SuppressWarnings("deprecation")
public class ReportTaskHandler extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 312162986545077269L;

    private static final Logger LOG = Logger.getLogger(CrawlTaskHandler.class
	    .getName());

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {
	String domain = req.getParameter("domain");
	String keyPhrase = req.getParameter("keyPhrase");

	System.out.println("Domain: " + domain);
	System.out.println("KeyPhrase: " + keyPhrase);
    }

    public static void createReportTask(String domain, String keyPhrase) {
	LOG.info("Creating report task");

	try {
	    Queue queue = QueueFactory.getQueue("report-queue");
	    queue.add(TaskOptions.Builder.url("/ReportTaskHandler")
		    .param("domain", domain).param("keyPhrase", keyPhrase));
	} catch (Exception ex) {
	    ex.printStackTrace();

	    LOG.severe("Creating parse task failed");
	}
    }
}
