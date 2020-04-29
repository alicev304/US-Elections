package core.corpus;

import utils.Constants;
import utils.io.FileHandler;

import java.util.*;

public class Tokenizer {
    public static final byte LEMMA_TOKENS = 0;
    public static final byte STEM_TOKENS = 1;

    private final Map<String, List<String>> tokenMap;
    private final byte mode;
    private Lemmatizer lemmatizer;
    private Stemmer stemmer;
    private final HashSet<String> stopWords;
    private int progress;

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
        this.progress = 0;
    }

    public void tokenize(FileHandler handler) {
        Map<String, String> content = handler.readFiles();
        content.forEach(this::tokenize);
    }

    public String tokenize(String docId, String text) {
        progress += 1;
        if (progress % 100 == 0) {
            System.out.println(progress);
        }
        String[] contentSplit;
        text = text.replaceAll(Constants.SPECIAL_CHARACTER_REGEX, "");
        if(mode == LEMMA_TOKENS) {
            contentSplit = lemmatizer.lemmatize(text);
        }
        else if(mode == STEM_TOKENS) {
            contentSplit = stemmer.stem(text);
        }
        else {
            contentSplit = text.split(" ");
        }
        StringBuilder builder = new StringBuilder();
        tokenMap.put(docId, new ArrayList<>());
        for (String item : contentSplit) {
            item = item.trim();
            if(!item.equals("") && !stopWords.contains(item)) {
                tokenMap.get(docId).add(item);
                builder.append(item).append(" ");
            }
        }
        return builder.toString().trim();
    }

    public Map<String, List<String>> getTokenMap() {
        return this.tokenMap;
    }
}
