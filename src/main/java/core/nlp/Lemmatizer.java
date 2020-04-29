package core.nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class Lemmatizer {

    private StanfordCoreNLP pipeline;

    public Lemmatizer() {
        Properties properties = new Properties();
        properties.setProperty("annotators", "tokenize, ssplit, pos, lemma");
        pipeline = new StanfordCoreNLP(properties);
    }

    public String[] lemmatize(String text) {

        List<String> lemmas = new LinkedList<>();
        Annotation document = new Annotation(text);
        pipeline.annotate(document);
        for(CoreMap sentence: document.get(CoreAnnotations.SentencesAnnotation.class)) {
            for(CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String tokenString = token.get(CoreAnnotations.LemmaAnnotation.class);
                lemmas.add(tokenString.toLowerCase());
            }
        }

        return lemmas.toArray(new String[0]);
    }
}
