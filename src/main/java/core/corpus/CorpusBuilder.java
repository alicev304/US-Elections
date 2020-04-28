package core.corpus;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import utils.Constants;
import utils.Utils;
import utils.io.FileHandler;

import java.util.ArrayList;
import java.util.List;

public class CorpusBuilder {
    private final List<String> listOfUrls;

    public CorpusBuilder(String pathToLinks) {
        listOfUrls = new ArrayList<>();
        FileHandler handler = new FileHandler(pathToLinks);
        handler.readLinks(listOfUrls);
    }

    public void build(Tokenizer tokenizer, boolean format) {
        int docID = 0;
        for (String url: listOfUrls) {
            try {
                Document doc = this.hitAndFetch(url);
                if (doc != null){
                    String title = doc.title();
                    String text = doc.body().text();
                    if(format) {
                        text = Utils.formatInput(text);
                    }
                    text.trim();
                    String tokenizedText = tokenizer.tokenize(title, text);
                    FileHandler handler = new FileHandler(Constants.TOKENIZED_CORPUS_DIR_PATH + docID);
                    handler.writeCorpusToFiles(title, tokenizedText, url);
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
