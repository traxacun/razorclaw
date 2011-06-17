package razorclaw.evaluation.razorclaw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;

import net.arnx.jsonic.JSON;
import razorclaw.crawler.HTMLCrawler;
import razorclaw.evaluation.storage.StorageHandler;
import razorclaw.object.APIMeta;
import razorclaw.object.KeyPhrase;
import razorclaw.object.KeyPhraseScore;
import razorclaw.object.Webpage;
import razorclaw.object.WebpageMeta;
import razorclaw.ranker.BM25F;
import razorclaw.ranker.UniversalComparator;
import razorclaw.tk.StatsAPI;

public class UCBAnalysis {
	private static final Logger LOG = Logger.getLogger(UCBAnalysis.class
			.getName());

	// private static final String RAZORCLAW_URL =
	// "http://localhost:8888/razorclaw?domain=";

	// private static final String BASE_PATH = "data/full-list/kea-en";

	private static final String OUTPUT_FILE = "data/ucb/24HRDIMES.tk.txt";

	private static final int ITERATION_COUNT = 2;

	// @formatter:off
	private static final String[] DOMAINS = { 
		"24HRDIMES.tk",
	};
	
	private static final double[][] X = {
		{0,
		0,
		0,
		0,
		0,
		68.51351351,
		0,
		0,
		3.142589118,
		0} //first iteration
	};
	
	private static final double[][] knownReward = {
		{0.75		
		}
	};
	// @formatter:on

	// private static final String RAZORCLAW_URL =
	// "http://workers.tkrazorclaw.appspot.com/razorclaw?domain=";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// ---init---
		RealMatrix matrixX = new Array2DRowRealMatrix(X);

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(OUTPUT_FILE));

			for (String domain : DOMAINS) {
				try {
					// ---always output domain---
					out.write(domain + "\t");

					// ------------crawl part-----------------
					Webpage web = new Webpage();
					APIMeta apiMeta = StatsAPI.crawl(domain);
					WebpageMeta webpageMeta = new WebpageMeta();
					web.setWebpageMeta(webpageMeta);
					web.setAPIMeta(apiMeta);

					web = HTMLCrawler.crawl(web);
					// -------------parse part----------------
					web.getWebpageMeta().setLanguage("en");

					web.parse();

					ArrayList<KeyPhraseScore> rankResult = UCBRanker.rank(web,
							matrixX, ITERATION_COUNT, knownReward);

					// -------------sort and output------------
					ArrayList<KeyPhraseScore> sortResult = sort(rankResult);

					// @formatter:off
				double titleScore = 0.0, metaKeywordsScore = 0.0, 
				metaDescriptionScore = 0.0, anchorTextScore = 0.0, 
				userKeywordsScore = 0.0, adminKeywordsScore = 0.0, 
				spiderKeywordsScore = 0.0, h1Score = 0.0, h2Score = 0.0, 
				contentScore = 0.0, lengthScore = 0.0, searchQueryScore = 0.0, 
				POSScore = 0.0, pseudoScore = 0.0;
				// @formatter:on
					String phrase = "";

					for (KeyPhraseScore kps : sortResult) {
						titleScore += kps.getTitleScore();
						metaKeywordsScore += kps.getMetaKeywordsScore();
						metaDescriptionScore += kps.getMetaDescriptionScore();
						anchorTextScore += kps.getAnchorTextScore();
						userKeywordsScore += kps.getUserKeywordsScore();
						adminKeywordsScore += kps.getAdminKeywordsScore();
						spiderKeywordsScore += kps.getSpiderKeywordsScore();
						h1Score += kps.getH1Score();
						h2Score += kps.getH2Score();
						contentScore += kps.getContentScore();
						lengthScore += kps.getLengthScore();
						searchQueryScore += kps.getSearchQueryScore();
						POSScore += kps.getPOSScore();

						pseudoScore += kps.getPseudoScore();
						phrase += kps.getPhrase() + ", ";
					}
					StringBuilder sb = new StringBuilder();
					sb.append(phrase);
					sb.append("\t");
					sb.append(pseudoScore);
					sb.append("\t");
					sb.append(titleScore);
					sb.append("\t");
					sb.append(metaKeywordsScore);
					sb.append("\t");
					sb.append(metaDescriptionScore);
					sb.append("\t");
					sb.append(h1Score);
					sb.append("\t");
					sb.append(h2Score);
					sb.append("\t");
					sb.append(contentScore);
					sb.append("\t");
					sb.append(anchorTextScore);
					sb.append("\t");
					sb.append(spiderKeywordsScore);
					sb.append("\t");
					sb.append(adminKeywordsScore);
					sb.append("\t");
					sb.append(userKeywordsScore);
					sb.append("\t");
					sb.append(lengthScore);
					sb.append("\t");
					sb.append(searchQueryScore);
					sb.append("\t");
					sb.append(POSScore);

					out.write(sb.toString());
				} catch (IOException e) {
					LOG.severe("Crawling stats.tk or webpage failed");
					LOG.severe("Generating keyphrase failed");

					continue;
				} catch (URISyntaxException e) {
					LOG.severe("Wrong URL format");
					LOG.severe("Generating keyphrase failed");

					continue;
				} finally {
					out.newLine();
					out.flush();
				}
			}
		} catch (IOException e) {
			LOG.severe("Reading input file failed");
		}
	}

	/**
	 * sort and generate keyphrases
	 * 
	 * @param phraseMap
	 */
	private static ArrayList<KeyPhraseScore> sort(
			List<KeyPhraseScore> rankResult) {
		Collections.sort(rankResult, new UniversalComparator());

		ArrayList<KeyPhraseScore> ret = new ArrayList<KeyPhraseScore>();

		// get the top 5
		for (int i = 0; i < 5 && i < rankResult.size(); i++) {
			ret.add(rankResult.get(i));
		}

		return ret;
	}
}
