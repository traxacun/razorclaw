package ucl.GAE.razorclaw.linguistic.pos;

import razorclaw.object.Dictionaries.PartOfSpeech;

public enum BrownPOSTag {
    JJ(PartOfSpeech.ADJECTIVE),
    JJT(PartOfSpeech.ADJECTIVE),
    JJS(PartOfSpeech.ADJECTIVE),
    JJR(PartOfSpeech.ADJECTIVE),
    RB(PartOfSpeech.ADVERB),
    RBR(PartOfSpeech.ADVERB),
    RBS(PartOfSpeech.ADVERB),
    RBT(PartOfSpeech.ADVERB),
    RP(PartOfSpeech.ADVERB),
    RN(PartOfSpeech.ADVERB),
    DT(PartOfSpeech.DETERMINER),
    PDT(PartOfSpeech.DETERMINER),
    FW(PartOfSpeech.FOREIGNWORD),
    NNS$(PartOfSpeech.NOUN),
    NN$(PartOfSpeech.NOUN),
    NR(PartOfSpeech.NOUN),
    NP$(PartOfSpeech.NOUN),
    NNS(PartOfSpeech.NOUN),
    NPS$(PartOfSpeech.NOUN),
    NN(PartOfSpeech.NOUN),
    OD(PartOfSpeech.NUMBER),
    CD(PartOfSpeech.NUMBER),
    IN(PartOfSpeech.PREPOSITION),
    PPSS(PartOfSpeech.PRONOUN),
    PRP$(PartOfSpeech.PRONOUN),
    PRP(PartOfSpeech.PRONOUN),
    PPS(PartOfSpeech.PRONOUN),
    PPO(PartOfSpeech.PRONOUN),
    PPLS(PartOfSpeech.PRONOUN),
    PPL(PartOfSpeech.PRONOUN),
    PP$(PartOfSpeech.PRONOUN),
    PN$(PartOfSpeech.PRONOUN),
    PN(PartOfSpeech.PRONOUN),
    POS(PartOfSpeech.PRONOUN),
    PP$$(PartOfSpeech.PRONOUN),
    NNPS(PartOfSpeech.PROPERNOUN),
    NNP(PartOfSpeech.PROPERNOUN),
    NP(PartOfSpeech.PROPERNOUN),
    NPS(PartOfSpeech.PROPERNOUN),
    ABL(PartOfSpeech.QUALIFIER),
    ABN(PartOfSpeech.QUALIFIER),
    ABX(PartOfSpeech.QUALIFIER),
    AP(PartOfSpeech.QUALIFIER),
    QL(PartOfSpeech.QUALIFIER),
    QLP(PartOfSpeech.QUALIFIER),
    AT(PartOfSpeech.STOPWORD),
    WRB(PartOfSpeech.STOPWORD),
    BED(PartOfSpeech.STOPWORD),
    BEDZ(PartOfSpeech.STOPWORD),
    BEG(PartOfSpeech.STOPWORD),
    BEM(PartOfSpeech.STOPWORD),
    BEN(PartOfSpeech.STOPWORD),
    BER(PartOfSpeech.STOPWORD),
    BEZ(PartOfSpeech.STOPWORD),
    CC(PartOfSpeech.STOPWORD),
    CS(PartOfSpeech.STOPWORD),
    DO(PartOfSpeech.STOPWORD),
    DOD(PartOfSpeech.STOPWORD),
    DOZ(PartOfSpeech.STOPWORD),
    DTI(PartOfSpeech.STOPWORD),
    DTS(PartOfSpeech.STOPWORD),
    DTX(PartOfSpeech.STOPWORD),
    EX(PartOfSpeech.STOPWORD),
    HV(PartOfSpeech.STOPWORD),
    HVD(PartOfSpeech.STOPWORD),
    HVG(PartOfSpeech.STOPWORD),
    HVN(PartOfSpeech.STOPWORD),
    LS(PartOfSpeech.STOPWORD),
    MD(PartOfSpeech.STOPWORD),
    BE(PartOfSpeech.STOPWORD),
    WQL(PartOfSpeech.STOPWORD),
    WPS(PartOfSpeech.STOPWORD),
    WPO(PartOfSpeech.STOPWORD),
    WP(PartOfSpeech.STOPWORD),
    WP$(PartOfSpeech.STOPWORD),
    WDT(PartOfSpeech.STOPWORD),
    TO(PartOfSpeech.STOPWORD),
    UH(PartOfSpeech.STOPWORD),
    SYM(PartOfSpeech.SYMBOL),
    VBN(PartOfSpeech.VERB),
    VBG(PartOfSpeech.VERB),
    VBD(PartOfSpeech.VERB),
    VB(PartOfSpeech.VERB),
    VBZ(PartOfSpeech.VERB),
    VBP(PartOfSpeech.VERB),
    UNSP(PartOfSpeech.UNSPECIFIED);

    private PartOfSpeech _pos;

    private BrownPOSTag(PartOfSpeech pos) {
	_pos = pos;
    }

    public PartOfSpeech getPartOfSpeech() {
	return _pos;
    }

    public static BrownPOSTag load(String tag) {
	try {
	    return (BrownPOSTag) Enum.valueOf(BrownPOSTag.class,
		    tag.toUpperCase());
	} catch (IllegalArgumentException iae) {
	    return BrownPOSTag.UNSP;
	}
    }
}
