package razorclaw.linguistic.filter;

import razorclaw.util.TextUtils;

public class AlphabeticFilter implements IFilter
{
	@Override
	public boolean filter(String word) 
	{
		return TextUtils.hasNonAlphabeticChars(word);
	}

}
