package razorclaw.util;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

import org.jsoup.Jsoup;

import razorclaw.util.InverseDocumentIndex.DomainList;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;

public class LanguageStatistics extends HttpServlet {
    private static final long serialVersionUID = -3537756064086240288L;

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {
	String url;
	if ((url = req.getParameter("url")) == null) {

	}

	DomainList list = JSON.decode(Jsoup.connect(url).get().text(),
		DomainList.class);

	for (String domain : list.getDomains()) {
	    createTask(domain);
	}
    }

    private void createTask(String url) {
	try {
	    Queue queue = QueueFactory.getQueue("util-queue");
	    queue.add(TaskOptions.Builder.url("/Main")
		    .param("url", url).param("languageStatistics", "1"));
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
}
