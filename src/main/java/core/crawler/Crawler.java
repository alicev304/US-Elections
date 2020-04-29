package core.crawler;

import api.Worker;
import core.corpus.Tokenizer;
import utils.Constants;
import utils.io.FileHandler;

import java.util.*;
import java.util.regex.Pattern;

public class Crawler {

    private static final int MAX_DEPTH = 3;
    private static final int MAX_FILES = 5000;
    private static final Pattern SUFFIX_FILTER = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp4|zip|gz|pdf))$");
    private static final Pattern PREFIX_FILTER = Pattern.compile("mailto:.*$");
    private static final String SEARCH_FILTER = "search";

    private static final String[] urlList = {
            "https://en.wikipedia.org/wiki/List_of_presidents_of_the_United_States",
            "https://www.cnn.com/election/2020/primaries-and-caucuses",
            "https://www.whitehouse.gov/about-the-white-house/elections-voting",
            "https://www.nytimes.com/news-event/2020-election",
            "https://en.wikipedia.org/wiki/United_States_presidential_election",
            "https://history.house.gov/Institution/Election-Statistics/Election-Statistics",
            "https://www.britannica.com/topic/United-States-Presidential-Election-Results-1788863",
            "https://www.history.com/this-day-in-history/first-u-s-presidential-election",
            "http://dmoztools.net/Regional/North_America/United_States/Government/Elections/President",
            "https://www.vote411.org/"};

    Map<String, Set<String>> pagesVisited;
    Queue<URL> URLQueue;
    public Tokenizer tokenizer;

    public Crawler() {
        tokenizer = new Tokenizer(Tokenizer.LEMMA_TOKENS, Worker.stopwords);
        pagesVisited = new HashMap<>();
        URLQueue = new LinkedList<>();
        for (String url: urlList) {
            URLQueue.add(new URL(url, 0, ""));
        }
    }

    public void crawl() {
        Set<String> totalPagesVisited = new HashSet<>();
        int totalFiles = 0;
        while (!URLQueue.isEmpty()) {
            URL url = URLQueue.poll();
            if (url.depth >= MAX_DEPTH) continue;
            if (!totalPagesVisited.contains(url.url)) {
                totalPagesVisited.add(url.url);
                Spider spider = new Spider();
                if (spider.search(url, tokenizer)) {
                    totalFiles++;
                    if(url.parentUrl.length() > 0) {
                        pagesVisited.get(url.parentUrl).add(url.url);
                    }
                    if(!pagesVisited.containsKey(url.url)) {
                        pagesVisited.put(url.url, new HashSet<>());
                    }
                    if(totalFiles >= MAX_FILES) break;
                    for (String newUrl: spider.getLinks()) {
                        if (shouldVisit(newUrl)) {
                            URLQueue.add(new URL(newUrl, url.depth+1, url.url));
                        }
                    }
                }
            }
        }
        FileHandler handler = new FileHandler(Constants.LIST_OF_URLS);
        handler.writeLinksToFile(pagesVisited);
    }

    private boolean shouldVisit(String url) {
        return !SUFFIX_FILTER.matcher(url).matches() && !PREFIX_FILTER.matcher(url).matches() && !url.contains(SEARCH_FILTER);
    }
}
