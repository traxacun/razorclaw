package ucl.GAE.razorclaw.linguistic.tagger;

import java.util.List;
import opennlp.tools.chunker.Chunker;
import opennlp.tools.chunker.ChunkerME;


public class OpenNLPSentenceTagger implements IPartOfSentenceTagger
{
	private static final String POS_TAG_FILE = "com/polecat/linguistics/models/opennlp-chunker.gz";
	private Chunker _chunker;
	
	public OpenNLPSentenceTagger()
	{
//		_chunker = new ChunkerME(getModel(POS_TAG_FILE));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getWordTags(List<String> words, List<String> partsOfSpeech) 
	{
		return _chunker.chunk(words, partsOfSpeech);
	}
}
