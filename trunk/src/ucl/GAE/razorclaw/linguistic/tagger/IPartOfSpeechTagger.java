package ucl.GAE.razorclaw.linguistic.tagger;

import java.util.List;

public interface IPartOfSpeechTagger 
{
	String getWordTag(String word);
	List<String> getWordTags(List<String> words);
}
