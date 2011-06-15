package razorclaw.ranker;

import java.util.Comparator;
import java.util.Map.Entry;

import razorclaw.object.KeyPhraseScore;
import razorclaw.object.PhraseProperty;

public class UniversalComparator implements Comparator<KeyPhraseScore> {

	@Override
	public int compare(KeyPhraseScore arg0, KeyPhraseScore arg1) {
		// first BM25F score
		if (arg0.getBM25FScore() - arg1.getBM25FScore() > 0) {
			return -1; // descending order
		} else if (arg0.getBM25FScore() - arg1.getBM25FScore() < 0) {
			return 1;
		} else {
			// then TF in Content
			if (arg0.getContentScore() > arg1.getContentScore()) {
				return -1;
			} else if (arg0.getContentScore() < arg1.getContentScore()) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}
