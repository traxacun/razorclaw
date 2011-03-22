package ucl.GAE.razorclaw.linguistic.pos;

public enum PartOfSpeech 
{
	ADJECTIVE,
	ADVERB,
	DETERMINER,
	FOREIGNWORD,
	NOUN,
	NUMBER,
	PREPOSITION,
	PRONOUN,
	PROPERNOUN,
	QUALIFIER,
	STOPWORD,
	SYMBOL,
	UNSPECIFIED,
	VERB;
	
	public static PartOfSpeech load(String tag)
	{
		try
		{
			return (PartOfSpeech) Enum.valueOf(PartOfSpeech.class, tag.toUpperCase());
		}
		catch (IllegalArgumentException iae)
		{
			return PartOfSpeech.UNSPECIFIED;
		}
	}
}
