package razorclaw.linguistic.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PolecatStopwordFilter implements IStopwordFilter, Serializable
{
	private static final long serialVersionUID = 1L;
	private static final String STOP_FILE = "com/polecat/linguistics/models/polecat-stopwords.txt";
	private static final String ARTICLE_REGEX = "the|a|an";
	private static List<String> _stopWords;
	private static Pattern _pattern = Pattern.compile(ARTICLE_REGEX);
	static
	{
		_stopWords = new ArrayList<String>();
		InputStream iStream = Thread.currentThread().getContextClassLoader().
			getResourceAsStream(STOP_FILE);
		BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
		String fileLine;
		try
		{
			while ((fileLine = reader.readLine()) != null)
			{
				_stopWords.add(fileLine);
			}
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	@Override
	public void addStopWord(String word)
	{
		_stopWords.add(word.toLowerCase());
	}
	
	@Override
	public boolean isStopWord(String word) 
	{
		return _stopWords.contains(word.toLowerCase());
	}
	
	@Override
	public boolean isArticle(String word)
	{
		return _pattern.matcher(word).matches();
	}

	@Override
	public boolean filter(String word) 
	{
		return isStopWord(word);
	}
}
