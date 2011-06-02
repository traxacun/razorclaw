package razorclaw.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

/**
 * @author Shuai Yuan
 *
 */
/**
 * @author Shuai Yuan
 * 
 */
public class TextUtils {
	private static final Logger LOG = Logger.getLogger(TextUtils.class
			.getName());

	public static final String replacePattern = "'s[.|,| ]|'[.|,| ]|[ |,]'|\"|^-|^&|^£|^$|,|:|;";
	private static Pattern _pattern = Pattern.compile(replacePattern);

	public static String removePunctuation(String text) {
		return _pattern.matcher(text).replaceAll(" ");
	}

	public static String removeNonAlphabeticChars(String text) {
		StringBuilder textBuilder = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c < 65)
				c = ' ';
			else if (c > 172)
				c = ' ';
			else if (c >= 91 && c <= 96)
				c = ' ';
			textBuilder.append(c);
		}
		return textBuilder.toString();
	}

	public static boolean hasNonAlphabeticChars(String text) {
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c == 32)
				continue;
			if (c < 65)
				return true;
			else if (c > 172)
				return true;
			else if (c >= 91 && c <= 96)
				return true;
		}
		return false;
	}

	public static boolean hasNonAsciiChars(String text) {
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c > 127)
				return true;
		}
		return false;
	}

	public static String replaceMSWordCharacters(String text) {
		text = text.replace((char) 96, '\'');
		text = text.replace((char) 8217, '\'');
		text = text.replace((char) 8220, '\"');
		text = text.replace((char) 8221, '\"');
		return text;
	}

	/**
	 * detect the language of _webpage.getText();
	 * 
	 * @throws LangDetectException
	 * 
	 */
	public static String detectLanguage(String text) {
		try {
			DetectorFactory.loadProfile("lang-profiles/");
		} catch (Exception e) {
			// profiles already loaded
		}
		try {
			Detector detector = DetectorFactory.create();
			detector.append(text);

			String lang = detector.detect();

			return lang;
		} catch (LangDetectException e) {
			LOG.warning("Un-recongnized language. Using English as default.");

			return "en";
		}
	}

	/**
	 * check if a string is URL
	 * 
	 * @param text
	 * @return
	 */
	public static boolean isURL(String text) {
		if (text.contains("http://") || text.contains("https://")) {
			return true;
		} else {
			return false;
		}
	}
}
