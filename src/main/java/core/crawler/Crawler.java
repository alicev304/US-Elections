package core.crawler;

import utils.io.FileHandler;

import java.util.*;

public class Crawler {

    List<String> urlStack = new ArrayList<String>(Arrays.asList(
            "https://www.usa.gov/",
            "https://www.fec.gov/",
            "https://www.eac.gov/",
            "https://ballotpedia.org/Elections/",
            "https://www.whitehouse.gov/about-the-white-house/elections-voting/",
            "https://www.nytimes.com/news-event/2020-election/",
            "https://en.wikipedia.org/wiki/United_States_presidential_election/",
            "https://en.wikipedia.org/wiki/Elections_in_the_United_States/",
            "https://history.house.gov/Institution/Election-Statistics/Election-Statistics/",
            "https://www.britannica.com/topic/United-States-Presidential-Election-Results-1788863/",
            "https://www.history.com/this-day-in-history/first-u-s-presidential-election/",
            "https://www.scholastic.com/teachers/articles/teaching-content/origins-and-functions-political-parties/",
            "http://dmoztools.net/Regional/North_America/United_States/Government/Elections/President/",
            "http://dmoztools.net/Regional/North_America/United_States/Society_and_Culture/Politics/Candidates_and_Campaigns/",
            "https://www.vote411.org/"));

    Map<String, Set<String>> pagesVisited;
    Set<String> totalPagesVisited;

    public Crawler() {
        pagesVisited = new HashMap<>();
        totalPagesVisited = new HashSet<>();
    }

    public void crawl() {
        Spider spider = new Spider();
        this.urlStack.forEach((url) -> {
            pagesVisited.put(url, new HashSet<>());
            pagesVisited.get(url).add(url);
            spider.search(url, pagesVisited, totalPagesVisited);
            System.out.println("\n**Done** Visited " + this.pagesVisited.get(url).size() + " web page(s)");
        });
        FileHandler handler = new FileHandler("output/links.txt");
        handler.writeLinksToFile(pagesVisited);
    }
}
