package razorclaw.filter;

import java.util.List;

import razorclaw.linguistic.pos.BrownPOSTag;
import razorclaw.parser.OpenNLPPOSTagger;



public class PolecatPOSFilter 
{
	/*private static IPartOfSpeechTagger _posTagger = new OpenNLPPOSTagger();
	private List<PartOfSpeech> _posFilters;
	
	public PolecatPOSFilter(List<PartOfSpeech> posFilters)
	{
		_posFilters = posFilters;
	}

	@Override
	public boolean filter(String word) 
	{
		String cleanTag = _posTagger.getWordTag(word).replace(word + "/", "").
			replaceAll("-.+", "").toLowerCase();
		PartOfSpeech pos = BrownPOSTag.load(cleanTag).getPartOfSpeech();
		return ! _posFilters.contains(pos);
	}*/
}
