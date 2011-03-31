package razorclaw.filter;

import razorclaw.parser.TextUtils;

public class AlphabeticFilter implements IFilter
{
	@Override
	public boolean filter(String word) 
	{
		return TextUtils.hasNonAlphabeticChars(word);
	}

}
