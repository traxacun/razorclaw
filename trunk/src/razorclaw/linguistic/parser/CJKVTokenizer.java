package razorclaw.linguistic.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import org.wltea.analyzer.IKSegmentation;
import org.wltea.analyzer.Lexeme;

public class CJKVTokenizer {
	private static final String CHINESE_DICT = "IKAnalyzer/chinese.u8";
	private static final String JAPANESE_DICT = "IKAnalyzer/japanese.u8";
	private static final String KOREAN_DICT = "IKAnalyzer/korean.u8";
	private static final String VIETNAM_DICT = "IKAnalyzer/vietnam.u8";

	private static IKSegmentation _seg;

	private static boolean _initialzied = false;

	private CJKVTokenizer() {
	}

	public static String next() throws IOException {
		Lexeme next = _seg.next();
		if (next == null) {
			return null;
		} else {
			return next.getLexemeText();
		}
	}

	public static void feed(Reader in) throws IOException {
		if (_initialzied == false) {
			init();
		}
		_seg = new IKSegmentation(in, true); // try maximize split length
	}

	private static void init() throws IOException {
		ArrayList<String> extWords = new ArrayList<String>();
		BufferedReader wordReader = new BufferedReader(new FileReader(
				CHINESE_DICT));
		for (String s = wordReader.readLine(); s != null; s = wordReader
				.readLine()) {
			extWords.add(s);
		}
		org.wltea.analyzer.dic.Dictionary.loadExtendWords(extWords);
		extWords.clear();

		wordReader = new BufferedReader(new FileReader(JAPANESE_DICT));
		for (String s = wordReader.readLine(); s != null; s = wordReader
				.readLine()) {
			extWords.add(s);
		}
		org.wltea.analyzer.dic.Dictionary.loadExtendWords(extWords);
		extWords.clear();

		wordReader = new BufferedReader(new FileReader(KOREAN_DICT));
		for (String s = wordReader.readLine(); s != null; s = wordReader
				.readLine()) {
			extWords.add(s);
		}
		org.wltea.analyzer.dic.Dictionary.loadExtendWords(extWords);
		extWords.clear();

		wordReader = new BufferedReader(new FileReader(VIETNAM_DICT));
		for (String s = wordReader.readLine(); s != null; s = wordReader
				.readLine()) {
			extWords.add(s);
		}
		org.wltea.analyzer.dic.Dictionary.loadExtendWords(extWords);
		extWords.clear();

		_initialzied = true;
	}
}
