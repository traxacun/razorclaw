package ucl.GAE.razorclaw.linguistic.tagger;

import java.util.List;

public interface IPartOfSentenceTagger 
{
	List<String> getWordTags(List<String> words, List<String> partsOfSpeech);
}
