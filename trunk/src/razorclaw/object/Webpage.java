package razorclaw.object;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * represents a webpage crawled from a dot.tk domain. NOTE: there could be
 * multiple dot.tk domains pointing to the same webpage
 * 
 * @author Shuai YUAN
 * 
 */
public class Webpage implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -3083386200240092000L;

    /**
     * content between <html> and </html>
     */
    private String _html;

    /**
     * content between <body> and </body> with HTML tag removed. used to detect
     * language
     */
    private String _text;

    private static final Logger LOG = Logger.getLogger(Webpage.class.getName());

    public Webpage() {

    }

    // -----------------------getters and setters---------------------

    public void setHtml(String _html) {
	this._html = _html;
    }

    public String getHtml() {
	return _html;
    }

    public void setText(String _text) {
	this._text = _text;
    }

    public String getText() {
	return _text;
    }

}
