package ucl.GAE.razorclaw.filter;

import java.util.List;

import ucl.GAE.razorclaw.linguistic.pos.BrownPOSTag;
import ucl.GAE.razorclaw.linguistic.pos.PartOfSpeech;
import ucl.GAE.razorclaw.linguistic.tagger.IPartOfSpeechTagger;
import ucl.GAE.razorclaw.linguistic.tagger.OpenNLPSpeechTagger;


public class PolecatPOSFilter implements IFilter
{
	private static IPartOfSpeechTagger _posTagger = new OpenNLPSpeechTagger();
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
	}
}
