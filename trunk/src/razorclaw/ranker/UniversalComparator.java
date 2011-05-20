package razorclaw.ranker;

import java.util.Comparator;
import java.util.Map.Entry;

import razorclaw.object.PhraseProperty;

public class UniversalComparator implements
		Comparator<Entry<String, PhraseProperty>> {
	@Override
	public int compare(Entry<String, PhraseProperty> arg0,
			Entry<String, PhraseProperty> arg1) {
		// first BM25F score
		if (arg0.getValue().getBM25FScore() - arg1.getValue().getBM25FScore() > 0) {
			return -1; // descending order
		} else if (arg0.getValue().getBM25FScore()
				- arg1.getValue().getBM25FScore() < 0) {
			return 1;
		} else {
			// then TF in Content
			if (arg0.getValue().getTFContent() > arg1.getValue().getTFContent()) {
				return -1;
			} else if (arg0.getValue().getTFContent() < arg1.getValue()
					.getTFContent()) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}
