package ucl.GAE.razorclaw.linguistic.tagger;

import java.util.List;

import opennlp.model.AbstractModel;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;

public class OpenNLPSpeechTagger implements IPartOfSpeechTagger
{
	private static final String POS_TAG_FILE = "com/polecat/linguistics/models/opennlp-tag.gz";
	private POSTagger tagger;
	
	@SuppressWarnings("deprecation")
	public OpenNLPSpeechTagger()
	{
//		tagger = new POSTaggerME((AbstractModel) getModel(POS_TAG_FILE), new Dictionary());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getWordTags(List<String> words) 
	{
		return tagger.tag(words);
	}

	@Override
	public String getWordTag(String word)
	{
		return tagger.tag(word);
	}
}
