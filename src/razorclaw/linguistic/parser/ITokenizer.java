package razorclaw.linguistic.parser;

import java.util.ArrayList;

public interface ITokenizer {
    public ArrayList<String> tokenize(String text, String lang);
}
