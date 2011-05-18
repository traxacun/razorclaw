package razorclaw.test;

import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.wltea.analyzer.lucene.IKQueryParser;
import org.wltea.analyzer.lucene.IKSimilarity;
import java.lang.*;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mozilla.intl.chardet.HtmlCharsetDetector;
import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

public class Unicode {

	private static final String URL_STRING = "http://c1520.icr38.net";
	private static String charSet = "";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			URL url = new URL(URL_STRING.toLowerCase());
			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(),
					url.getQuery(), null);

			URLConnection conn = (new URL(uri.toString())).openConnection();
			conn.setConnectTimeout(0);
			conn.setReadTimeout(0);

			byte[] byteChunk = new byte[4096];
			ByteArrayOutputStream webpageContent = new ByteArrayOutputStream();

			InputStream in = conn.getInputStream();
			while (in.read(byteChunk) > 0) {
				webpageContent.write(byteChunk);
			}
			// nsDetector det = new nsDetector(nsDetector.ALL);
			//
			// det.Init(new nsICharsetDetectionObserver() {
			// public void Notify(String result) {
			// HtmlCharsetDetector.found = true;
			// // System.out.println("CHARSET = " + charset);
			// charSet = result;
			// }
			// });
			//
			// det.DoIt(webpageContent.toByteArray(), webpageContent.size(),
			// false);
			// det.DataEnd();
			Document doc = Jsoup.parse(webpageContent.toString()); // parse
																	// using
																	// default
																	// charset

			Elements meta = doc.getElementsByTag("meta");
			for (Element e : meta) {
				if (e.hasAttr("http-equiv") && e.hasAttr("content")
						&& e.attr("http-equiv").equals("Content-Type")) {
					String s = e.attr("content");
					charSet = s.substring(s.indexOf("charset=") + 8);

					break;
				}
			}

			String content = new String(webpageContent.toString(charSet));
			// System.out.println(content);

			doc = Jsoup.parse(content); // parse using given charset
			// System.out.println(doc.body().text());

			// BufferedReader in = new BufferedReader(new InputStreamReader(
			// conn.getInputStream()));
			// String s = "", content = "";
			// while ((s = in.readLine()) != null) {
			// content += s;
			// }
			//
			// System.out.println(content);
			//
			// Document doc = Jsoup.parse(content);
			//
			// System.out.println(doc.body().text());

			// System.out.println("中文汉字 mixed");

			ArrayList<String> extWords = new ArrayList<String>();
			BufferedReader wordReader = new BufferedReader(
					new FileReader(
							"D:/my projects/razorclaw/war/IKAnalyzer/cedict_simplified.u8"));
			for (String s = wordReader.readLine(); s != null; s = wordReader
					.readLine()) {
				extWords.add(s);
			}
			org.wltea.analyzer.dic.Dictionary.loadExtendWords(extWords);

			IKSegmentation seg = new IKSegmentation(new StringReader(doc.body()
					.text()), true); // try maximize split length
			for (Lexeme token = seg.next(); token != null; token = seg.next()) {
				System.out.println(token);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
