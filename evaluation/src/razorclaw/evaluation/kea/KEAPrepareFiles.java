package razorclaw.evaluation.kea;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Logger;

import razorclaw.evaluation.storage.StorageHandler;
import razorclaw.object.APIMeta;
import razorclaw.object.Webpage;
import razorclaw.object.WebpageMeta;

public class KEAPrepareFiles {
	private static final Logger LOG = Logger.getLogger(KEAPrepareFiles.class
			.getName());

	public static void main(String[] args) {
		ArrayList<String> domains = StorageHandler
				.loadDomains("data/full-list/cn.txt");

		int count = 0;
		for (String domain : domains) {
			// get forward URL
			APIMeta apiMeta = razorclaw.tk.StatsAPI.crawl(domain);

			// load web content
			Webpage web = new Webpage();
			web.setAPIMeta(apiMeta);

			WebpageMeta webpageMeta = new WebpageMeta();
			web.setWebpageMeta(webpageMeta);

			try {
				web = razorclaw.crawler.HTMLCrawler.crawl(web);
			} catch (URISyntaxException e) {
				LOG.warning("Wrong forward URL format");
				e.printStackTrace();
				continue;
			} catch (Exception e) {
				LOG.warning("Crawling forward URL failed");
				e.printStackTrace();
				continue;
			}

			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(
						"data/full-list/kea-cn/"
								+ domain.substring(0, domain.indexOf("."))
								+ ".txt"));

				out.write(web.getText());
				out.close();
			} catch (IOException e) {
				e.printStackTrace();

				LOG.warning("Downloading webpage failed");
			} finally {
				if (count % 10 == 0) {
					System.out.println(count + " domains downloaded");
				}
				count++;
			}
		}
	}
}
