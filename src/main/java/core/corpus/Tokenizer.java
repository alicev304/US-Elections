package core.corpus;

import utils.filter.IFilter;
import utils.io.FileHandler;

import java.util.*;

public class Tokenizer {
    public static final byte LEMMA_TOKENS = 0;
    public static final byte STEM_TOKENS = 1;

    private Map<String, List<String>> tokenMap;
    private byte mode;
    private Lemmatizer lemmatizer;
    private Stemmer stemmer;
    private HashSet<String> stopWords;

    public Tokenizer(byte mode, HashSet<String> stopWords) {
        this.tokenMap = new LinkedHashMap<>();
        this.mode = mode;
        if(this.mode == LEMMA_TOKENS) {
            lemmatizer = new Lemmatizer();
        }
        else if(this.mode == STEM_TOKENS) {
            stemmer = new Stemmer();
        }
        this.stopWords = stopWords;
    }

    public String tokenize(String title, String text) {
        String[] contentSplit;
        if(mode == LEMMA_TOKENS) {
            contentSplit = lemmatizer.lemmatize(text);
        }
        else if(mode == STEM_TOKENS) {
            contentSplit = stemmer.stem(text);
        }
        else {
            contentSplit = text.split(" ");
        }
        tokenMap.put(title, new ArrayList<>());
        StringBuilder builder = new StringBuilder();
        for (String item : contentSplit) {
            item = item.trim();
            if(!item.equals("") && !stopWords.contains(item)) {
                tokenMap.get(title).add(item);
                builder.append(item).append(" ");
            }
        }
        return builder.toString();
    }

    public Map<String, List<String>> getTokenMap() {
        return this.tokenMap;
    }
}
