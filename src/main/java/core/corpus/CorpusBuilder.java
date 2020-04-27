package core.corpus;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import utils.Constants;
import utils.filter.HTMLFilter;
import utils.io.FileHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CorpusBuilder {
    private final List<String> listOfUrls;

    public CorpusBuilder(String pathToLinks) {
        listOfUrls = new ArrayList<>();
        FileHandler handler = new FileHandler(pathToLinks);
        handler.readLinks(listOfUrls);
    }

    public void build(Tokenizer tokenizer) {
        int docID = 0;
        for (String url: listOfUrls) {
            try {
                Document doc = this.hitAndFetch(url);
                if (doc != null){
                    String title = doc.title();
                    String text = doc.body().text();
                    tokenizer.tokenize("output/corpus", new HTMLFilter(), true);
                    FileHandler handler = new FileHandler(Constants.TOKENIZED_CORPUS_DIR_PATH + docID + ".txt");
                    handler.writeCorpusToFiles(title, text);
                    docID++;
                }
            } catch (Exception e) {
                System.out.println("**Ignored** " + e.getMessage());
            }
        }
    }

    private Document hitAndFetch(String url) {
        try {
            Connection connection = Jsoup.connect(url).userAgent(Constants.USER_AGENT);
            try {
                return connection.get();
            } catch (Exception e) {
                System.out.println("**Ignored** " + e.getMessage());
                return null;
            }
        } catch(Exception e) {
            // We were not successful in our HTTP request
            System.out.println("**Ignored** " + e.getMessage());
            return null;
        }
    }
}
