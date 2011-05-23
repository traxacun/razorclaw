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
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.bind.DatatypeConverter;

import org.apache.hadoop.util.UTF8ByteArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mortbay.util.Utf8StringBuffer;
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
		Unicode.UTF8Test();
	}

	public static void UTF8Test() {
		try {
			String s = "\u795e\u7ecf\u7f51\u7edc";

			System.out.println(s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static byte[] fromHexString(final String encoded) {
		if ((encoded.length() % 2) != 0)
			throw new IllegalArgumentException(
					"Input string must contain an even number of characters");

		final byte result[] = new byte[encoded.length() / 2];
		final char enc[] = encoded.toCharArray();
		for (int i = 0; i < enc.length; i += 2) {
			StringBuilder curr = new StringBuilder(2);
			curr.append(enc[i]).append(enc[i + 1]);
			result[i / 2] = (byte) Integer.parseInt(curr.toString(), 16);
		}
		return result;
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static void charsetTest() {
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
