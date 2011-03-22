package ucl.GAE.razorclaw.parse;

public interface ITokenizer 
{
	/**
	 * break a paragraph into sentences.
	 * @param bodyText
	 * @return sentences
	 */
	String[] tokenizeToSentence(String bodyText);
	/**
	 * break a sentence into phrases.
	 * @param sentenceText
	 * @return phrases
	 */
	String[] tokenizeToWords(String sentenceText);
}
