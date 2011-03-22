package ucl.GAE.razorclaw.object;

public class Dictionaries {
    public static enum HtmlVersion {
	HTML2,
	HTML3,
	HTML4,
	HTML5,
	XHTML1,
	XHTML2,
	XHTML5,
	UNKNOWN
    }

    public static enum Language {

    }

    /**
     * We only parse-able content-type here.
     * 
     * @author Shuai YUAN
     * 
     */
    public static enum ContentType {
	text_csv,
	text_html,
	text_plain,
	text_xml,
	application_atom_xml,
	application_pdf,
	application_xhtml_xml,
	application_postscript
    }

    public static enum Charset {
	CP37,
	CP930,
	CP1047,
	ISO8859_1,
	ISO8859_2,
	ISO8859_3,
	ISO8859_4,
	ISO8859_5,
	ISO8859_6,
	ISO8859_7,
	ISO8859_8F,
	ISO8859_9,
	ISO8859_10,
	ISO8859_11,
	ISO8859_12,
	ISO8859_13,
	ISO8859_14,
	ISO8859_15,
	ISO8859_16,
	WINDOWS1250,
	WINDOWS1251,
	WINDOWS1252,
	WINDOWS1253,
	WINDOWS1254,
	WINDOWS1255,
	WINDOWS1256,
	WINDOWS1257,
	WINDOWS1258,
	MAC_OS_ROMAN,
	GB2312,
	GBK,
	GB18030,
	BIG5,
	UNICODE,
	KS_X_1001,
	EUC_KR,
	ISO2022KR
    }

    /**
     * describes different stages that a webpage is in during the whole process
     * 
     * @author Shuai YUAN
     * 
     */
    public static enum Status {
	PENDING,
	CRAWLING,
	PARSING,
	EXTRACTING,
	FINISHED
    }
}
