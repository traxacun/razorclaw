package razorclaw.parse;

import java.util.regex.Pattern;

public class TextUtils {
    private static final String replacePattern = "'s[.|,| ]|'[.|,| ]|[ |,]'|\"|^-|^&|^£|^$|[^[0-9],[0-9]|^a-zA-Z0-9]|,|:|;";
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
}
