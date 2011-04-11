package razorclaw.util;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

import org.jsoup.Jsoup;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;

/**
 * go over the a domain list in (JSON) to build the initial inverse document
 * index. works as a task.
 * 
 * @author Shuai YUAN
 * 
 */
@SuppressWarnings("deprecation")
public class InverseDocumentIndex extends HttpServlet {
    private static final long serialVersionUID = -3923252430330491422L;

    private final String TOP100 = "http://www.stats.tk/ucl/top100";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {
	DomainList list = JSON.decode(Jsoup.connect(TOP100).get().text(),
		DomainList.class);

	for (String domain : list.getDomains()) {
	    createTask(domain);
	}
    }

    private void createTask(String domain) {
	try {
	    Queue queue = QueueFactory.getQueue("util-queue");
	    queue.add(TaskOptions.Builder.url("/razorclaw")
		    .param("domain", domain).param("buildIndex", "1"));
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    /**
     * used to hold top100 list
     * 
     * @author Shuai YUAN
     * 
     */
    class DomainList {
	private ArrayList<String> _domains;

	public ArrayList<String> getDomains() {
	    return _domains;
	}

	public void setDomains(ArrayList<String> obj) {
	    _domains = obj;
	}
    }
}
