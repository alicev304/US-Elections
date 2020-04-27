package core.crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.Constants;

import java.util.LinkedList;
import java.util.List;

public class SpiderLeg
{
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;

    public boolean crawl(String url) {
        try {
            Connection connection = Jsoup.connect(url).userAgent(Constants.USER_AGENT);
            Document htmlDocument;
            try {
                htmlDocument = connection.get();
            } catch (Exception e) {
                System.out.println("**Ignored** " + e.getMessage());
                return true;
            }
            this.htmlDocument = htmlDocument;
            if(connection.response().statusCode() == 200) {
                System.out.println("\n**Visiting** Received web page at " + url);
            }
            if(!connection.response().contentType().contains("text/html")) {
                System.out.println("**Ignored** Retrieved something other than HTML");
                return true;
            }
            Elements linksOnPage = htmlDocument.select("a[href]");
            System.out.println("Found (" + linksOnPage.size() + ") links");
            for(Element link : linksOnPage) {
                this.links.add(link.absUrl("href"));
            }
            return true;
        } catch(Exception e) {
            // We were not successful in our HTTP request
            System.out.println("**Ignored** " + e.getMessage());
            return true;
        }
    }

    public List<String> getLinks()
    {
        return this.links;
    }
}