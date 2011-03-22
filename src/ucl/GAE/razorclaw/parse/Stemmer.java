package ucl.GAE.razorclaw.parse;

import ucl.GAE.razorclaw.object.Phrase;

/**
 * @author Shuai YUAN
 * 
 */
public class Stemmer implements IStemmer {
    private char[] b;
    private int i, j, k, k0;
    private boolean dirty = false;
    private static final int INC = 50;
    private static final int EXTRA = 1;

    public Stemmer() {
	b = new char[INC];
	i = 0;
    }

    /**
     * reset() resets the stemmer so it can stem another word. If you invoke the
     * stemmer by calling add(char) and then stem(), you must call reset()
     * before starting another word.
     */
    public void reset() {
	i = 0;
	dirty = false;
    }

    /**
     * Add a character to the word being stemmed. When you are finished adding
     * characters, you can call stem(void) to process the word.
     */
    public void addCharToStem(char ch) {
	if (b.length <= i + EXTRA) {
	    char[] new_b = new char[b.length + INC];
	    for (int c = 0; c < b.length; c++) {
		new_b[c] = b[c];
	    }
	    b = new_b;
	}
	b[i++] = ch;
    }

    /**
     * After a word has been stemmed, it can be retrieved by toString(), or a
     * reference to the internal buffer can be retrieved by getResultBuffer and
     * getResultLength (which is generally more efficient.)
     */
    public String toString() {
	return new String(b, 0, i);
    }

    private final boolean isConsonant(int i) {
	switch (b[i]) {
	case 'a':
	case 'e':
	case 'i':
	case 'o':
	case 'u':
	    return false;
	case 'y':
	    return (i == k0) ? true : !isConsonant(i - 1);
	default:
	    return true;
	}
    }

    /**
     * Measures the number of consonant sequences between k0 and j. if c is a
     * consonant sequence and v a vowel sequence, and <..> indicates arbitrary
     * presence <c><v> gives 0 <c>vc<v> gives 1 <c>vcvc<v> gives 2 <c>vcvcvc<v>
     * gives 3
     */
    private final int getConsonantSequenceNumber() {
	int n = 0;
	int i = k0;
	while (true) {
	    if (i > j)
		return n;
	    if (!isConsonant(i))
		break;
	    i++;
	}
	i++;
	while (true) {
	    while (true) {
		if (i > j)
		    return n;
		if (isConsonant(i))
		    break;
		i++;
	    }
	    i++;
	    n++;
	    while (true) {
		if (i > j)
		    return n;
		if (!isConsonant(i))
		    break;
		i++;
	    }
	    i++;
	}
    }

    private final boolean isVowelInStem() {
	int i;
	for (i = k0; i <= j; i++) {
	    if (!isConsonant(i)) {
		return true;
	    }
	}
	return false;
    }

    private final boolean isDoubleConsonant(int j) {
	if (j < k0 + 1)
	    return false;
	if (b[j] != b[j - 1])
	    return false;
	return isConsonant(j);
    }

    /**
     * Is true <=> i-2,i-1,i has the form consonant - vowel - consonant and also
     * if the second c is not w,x or y. this is used when trying to restore an e
     * at the end of a short word. e.g. cav(e), lov(e), hop(e), crim(e), but
     * snow, box, tray.
     */
    private final boolean isConsonantVowelConsonant(int i) {
	if (i < k0 + 2 || !isConsonant(i) || isConsonant(i - 1)
		|| !isConsonant(i - 2)) {
	    return false;
	} else {
	    int ch = b[i];
	    if (ch == 'w' || ch == 'x' || ch == 'y')
		return false;
	}
	return true;
    }

    private final boolean isEnd(String s) {
	int l = s.length();
	int o = k - l + 1;
	if (o < k0)
	    return false;
	for (int i = 0; i < l; i++) {
	    if (b[o + i] != s.charAt(i)) {
		return false;
	    }
	}
	j = k - l;
	return true;
    }

    private void addWordEnding(String s) {
	int l = s.length();
	int o = j + 1;
	for (int i = 0; i < l; i++) {
	    b[o + i] = s.charAt(i);
	}
	k = j + l;
	dirty = true;
    }

    void addCheckedWordEnding(String s) {
	if (getConsonantSequenceNumber() > 0)
	    addWordEnding(s);
    }

    /**
     * step1() gets rid of plurals and -ed or -ing. e.g. caresses -> caress
     * ponies -> poni ties -> ti caress -> caress cats -> cat feed -> feed
     * agreed -> agree disabled -> disable matting -> mat mating -> mate meeting
     * -> meet milling -> mill
     */
    private final void step1() {
	if (b[k] == 's') {
	    if (isEnd("sses"))
		k -= 2;
	    else if (isEnd("ies"))
		addWordEnding("i");
	    else if (b[k - 1] != 's')
		k--;
	}
	if (isEnd("eed")) {
	    if (getConsonantSequenceNumber() > 0)
		k--;
	} else if ((isEnd("ed") || isEnd("ing")) && isVowelInStem()) {
	    k = j;
	    if (isEnd("at"))
		addWordEnding("ate");
	    else if (isEnd("bl"))
		addWordEnding("ble");
	    else if (isEnd("iz"))
		addWordEnding("ize");
	    else if (isDoubleConsonant(k)) {
		int ch = b[k--];
		if (ch == 'l' || ch == 's' || ch == 'z')
		    k++;
	    } else if (getConsonantSequenceNumber() == 1
		    && isConsonantVowelConsonant(k)) {
		addWordEnding("e");
	    }
	}
    }

    /**
     * turns terminal y to i when there is another vowel in the stem.
     */
    private final void step2() {
	if (isEnd("y") && isVowelInStem()) {
	    b[k] = 'i';
	    dirty = true;
	}
    }

    /**
     * step3() maps double suffices to single ones. so -ization ( = -ize plus
     * -ation) maps to -ize etc. note that the string before the suffix must
     * give getConsonantSequenceNumber() > 0.
     */
    private final void step3() {
	if (k == k0)
	    return;
	switch (b[k - 1]) {
	case 'a':
	    if (isEnd("ational")) {
		addCheckedWordEnding("ate");
		break;
	    }
	    if (isEnd("tional")) {
		addCheckedWordEnding("tion");
		break;
	    }
	    break;
	case 'c':
	    if (isEnd("enci")) {
		addCheckedWordEnding("ence");
		break;
	    }
	    if (isEnd("anci")) {
		addCheckedWordEnding("ance");
		break;
	    }
	    break;
	case 'e':
	    if (isEnd("izer")) {
		addCheckedWordEnding("ize");
		break;
	    }
	    break;
	case 'l':
	    if (isEnd("bli")) {
		addCheckedWordEnding("ble");
		break;
	    }
	    if (isEnd("alli")) {
		addCheckedWordEnding("al");
		break;
	    }
	    if (isEnd("entli")) {
		addCheckedWordEnding("ent");
		break;
	    }
	    if (isEnd("eli")) {
		addCheckedWordEnding("e");
		break;
	    }
	    if (isEnd("ousli")) {
		addCheckedWordEnding("ous");
		break;
	    }
	    break;
	case 'o':
	    if (isEnd("ization")) {
		addCheckedWordEnding("ize");
		break;
	    }
	    if (isEnd("ation")) {
		addCheckedWordEnding("ate");
		break;
	    }
	    if (isEnd("ator")) {
		addCheckedWordEnding("ate");
		break;
	    }
	    break;
	case 's':
	    if (isEnd("alism")) {
		addCheckedWordEnding("al");
		break;
	    }
	    if (isEnd("iveness")) {
		addCheckedWordEnding("ive");
		break;
	    }
	    if (isEnd("fulness")) {
		addCheckedWordEnding("ful");
		break;
	    }
	    if (isEnd("ousness")) {
		addCheckedWordEnding("ous");
		break;
	    }
	    break;
	case 't':
	    if (isEnd("aliti")) {
		addCheckedWordEnding("al");
		break;
	    }
	    if (isEnd("iviti")) {
		addCheckedWordEnding("ive");
		break;
	    }
	    if (isEnd("biliti")) {
		addCheckedWordEnding("ble");
		break;
	    }
	    break;
	case 'g':
	    if (isEnd("logi")) {
		addCheckedWordEnding("log");
		break;
	    }
	}
    }

    /**
     * step4() deals with -ic-, -full, -ness etc. similar strategy to step3.
     */
    private final void step4() {
	switch (b[k]) {
	case 'e':
	    if (isEnd("icate")) {
		addCheckedWordEnding("ic");
		break;
	    }
	    if (isEnd("ative")) {
		addCheckedWordEnding("");
		break;
	    }
	    if (isEnd("alize")) {
		addCheckedWordEnding("al");
		break;
	    }
	    break;
	case 'i':
	    if (isEnd("iciti")) {
		addCheckedWordEnding("ic");
		break;
	    }
	    break;
	case 'l':
	    if (isEnd("ical")) {
		addCheckedWordEnding("ic");
		break;
	    }
	    if (isEnd("ful")) {
		addCheckedWordEnding("");
		break;
	    }
	    break;
	case 's':
	    if (isEnd("ness")) {
		addCheckedWordEnding("");
		break;
	    }
	    break;
	}
    }

    /**
     * step5() takes off -ant, -ence etc., in context <c>vcvc<v>.
     */
    private final void step5() {
	if (k == k0)
	    return;
	switch (b[k - 1]) {
	case 'a':
	    if (isEnd("al"))
		break;
	    return;
	case 'c':
	    if (isEnd("ance"))
		break;
	    if (isEnd("ence"))
		break;
	    return;
	case 'e':
	    if (isEnd("er"))
		break;
	    return;
	case 'i':
	    if (isEnd("ic"))
		break;
	    return;
	case 'l':
	    if (isEnd("able"))
		break;
	    if (isEnd("ible"))
		break;
	    return;
	case 'n':
	    if (isEnd("ant"))
		break;
	    if (isEnd("ement"))
		break;
	    if (isEnd("ment"))
		break;
	    if (isEnd("ent"))
		break;
	    return;
	case 'o':
	    if (isEnd("ion") && j >= 0 && (b[j] == 's' || b[j] == 't'))
		break;
	    if (isEnd("ou"))
		break;
	    return;
	case 's':
	    if (isEnd("ism"))
		break;
	    return;
	case 't':
	    if (isEnd("ate"))
		break;
	    if (isEnd("iti"))
		break;
	    return;
	case 'u':
	    if (isEnd("ous"))
		break;
	    return;
	case 'v':
	    if (isEnd("ive"))
		break;
	    return;
	case 'z':
	    if (isEnd("ize"))
		break;
	    return;
	default:
	    return;
	}
	if (getConsonantSequenceNumber() > 1)
	    k = j;
    }

    /**
     * step6() removes a final -e if getConsonantSequenceNumber() > 1.
     */
    private final void step6() {
	j = k;
	if (b[k] == 'e') {
	    int a = getConsonantSequenceNumber();
	    if (a > 1 || a == 1 && !isConsonantVowelConsonant(k - 1))
		k--;
	}
	if (b[k] == 'l' && isDoubleConsonant(k)
		&& getConsonantSequenceNumber() > 1)
	    k--;
    }

    /**
     * Stem a word provided as a String. Returns the result as a String.
     */
    public String stem(String s) {
	if (stem(s.toCharArray(), s.length()))
	    return toString();
	else
	    return s;
    }

    /**
     * Stem a word contained in a char[]. Returns true if the stemming process
     * resulted in a word different from the input. You can retrieve the result
     * with getResultLength()/getResultBuffer() or toString().
     */
    public boolean stem(char[] word) {
	return stem(word, word.length);
    }

    /**
     * Stem a word contained in a portion of a char[] array. Returns true if the
     * stemming process resulted in a word different from the input. You can
     * retrieve the result with getResultLength()/getResultBuffer() or
     * toString().
     */
    public boolean stem(char[] wordBuffer, int offset, int wordLen) {
	reset();
	if (b.length < wordLen) {
	    char[] new_b = new char[wordLen + EXTRA];
	    b = new_b;
	}
	for (int j = 0; j < wordLen; j++) {
	    b[j] = wordBuffer[offset + j];
	}
	i = wordLen;
	return stem(0);
    }

    /**
     * Stem a word contained in a leading portion of a char[] array. Returns
     * true if the stemming process resulted in a word different from the input.
     * You can retrieve the result with getResultLength()/getResultBuffer() or
     * toString().
     */
    public boolean stem(char[] word, int wordLen) {
	return stem(word, 0, wordLen);
    }

    /**
     * Stem the word placed into the Stemmer buffer through calls to add().
     * Returns true if the stemming process resulted in a word different from
     * the input. You can retrieve the result with
     * getResultLength()/getResultBuffer() or toString().
     */
    public boolean stem() {
	return stem(0);
    }

    public boolean stem(int i0) {
	k = i - 1;
	k0 = i0;
	if (k > k0 + 1) {
	    step1();
	    step2();
	    step3();
	    step4();
	    step5();
	    step6();
	}
	if (i != k + 1)
	    dirty = true;
	i = k + 1;
	return dirty;
    }

    /**
     * stem the phrase and keep the text statistics
     * 
     * @param p
     * @return
     */
    public Phrase stem(Phrase p) {
	p.setPhrase(this.stem(p.getPhrase()));

	return p;
    }
}
