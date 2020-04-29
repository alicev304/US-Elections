package core.crawler;

import core.corpus.Tokenizer;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.Constants;
import utils.io.FileHandler;

import java.util.LinkedList;
import java.util.List;

public class Spider
{
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;

    public boolean search(URL url, Tokenizer tokenizer) {
        try {
            Connection connection = Jsoup.connect(url.url).userAgent(Constants.USER_AGENT);
            Document htmlDocument;
            try {
                htmlDocument = connection.get();
            } catch (Exception e) {
                System.out.println("**Ignored** " + e.getMessage());
                return false;
            }
            this.htmlDocument = htmlDocument;
            if(connection.response().statusCode() == 200) {
                System.out.println("\n**Visiting** Received web page at " + url.url);
            }
            if(!connection.response().contentType().contains("text/html") && htmlDocument.body().text().trim().length() > 0) {
                System.out.println("**Ignored** Retrieved something other than HTML/text");
                return false;
            }
            int docID = FileHandler.getDocID(Constants.TOKENIZED_CORPUS_DIR_PATH);
            FileHandler handler = new FileHandler(Constants.TOKENIZED_CORPUS_DIR_PATH + docID);
            String title = htmlDocument.title();
            String text = htmlDocument.body().text();
            handler.writeCorpusToFile(title, text, url.url, tokenizer);
            Elements linksOnPage = htmlDocument.select("a[href]");
            System.out.println("Found (" + linksOnPage.size() + ") links");
            for(Element link : linksOnPage) {
                this.links.add(link.absUrl("href"));
            }
            return true;
        } catch(Exception e) {
            // We were not successful in our HTTP request
            System.out.println("**Ignored** " + e.getMessage());
            return false;
        }
    }

    public List<String> getLinks()
    {
        return this.links;
    }
}