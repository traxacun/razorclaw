package razorclaw.parser;

import java.util.logging.Logger;

import razorclaw.util.TextUtils;

public class BasicTokenizer {
	private static final Logger LOG = Logger.getLogger(BasicTokenizer.class
			.getName());

	public static String[] tokenize(String text, String lang) {
		return text.split(TextUtils.replacePattern);
	}
}
