package razorclaw.evaluation.razorclaw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import razorclaw.crawler.HTMLCrawler;
import razorclaw.object.APIMeta;
import razorclaw.object.KeyPhraseScore;
import razorclaw.object.Webpage;
import razorclaw.object.WebpageMeta;
import razorclaw.ranker.BM25F;
import razorclaw.ranker.UniversalComparator;
import razorclaw.tk.StatsAPI;

/**
 * use this class to output raw score of fields in BM25F algorithm
 * 
 * @author Shuai Yuan
 * 
 */
public class RawScoreWritter {

	private static final Logger LOG = Logger.getLogger(RawScoreWritter.class
			.getName());

	private static final String INPUT_FILE = "data/1st/en_reward.txt";

	private static final String OUTPUT_FILE = "data/1st/en_raw.txt";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// ---init---
		BufferedWriter out = null;
		BufferedReader in = null;
		try {
			out = new BufferedWriter(new FileWriter(OUTPUT_FILE));
			in = new BufferedReader(new FileReader(INPUT_FILE));
		} catch (IOException e) {
			e.printStackTrace();
			LOG.severe("Open output file failed");

			return;
		}
		String line = "";
		try {
			while ((line = in.readLine()) != null) {
				try {
					String[] arr = line.split("\t");
					String domain = arr[0];

					// ---always output domain---
					out.write(line + "\t");

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

					ArrayList<KeyPhraseScore> rankResult = BM25F.rank(
							web.getWebpageMeta(), web.getAPIMeta(),
							web.getPhraseMap());

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
					sb.append(phrase.substring(0, phrase.length() - 2));
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
