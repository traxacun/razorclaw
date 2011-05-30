package razorclaw.linguistic.filter;

import java.util.List;

import razorclaw.linguistic.parser.OpenNLPPOSTagger;
import razorclaw.linguistic.pos.BrownPOSTag;



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
