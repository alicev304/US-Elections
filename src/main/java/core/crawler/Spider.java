package core.crawler;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class Spider {
    private static final int MAX_DEPTH = 6;
    private static final Pattern SUFFIX_FILTER = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp4|zip|gz|pdf))$");
    private static final Pattern PREFIX_FILTER = Pattern.compile("mailto:.*$");

    public void search(String url, Map<String, Set<String>> pagesVisited, Set<String> totalPagesVisited) {
        this.searchUtil(url, url, pagesVisited, totalPagesVisited, 0);
    }

    public void searchUtil(String rootUrl, String currUrl, Map<String, Set<String>> pagesVisited, Set<String> totalPagesVisited, int depth) {
        if (depth >= MAX_DEPTH) return;
        SpiderLeg leg = new SpiderLeg();
        leg.crawl(currUrl);
        for (String url: leg.getLinks()) {
            if (!totalPagesVisited.contains(url) && shouldVisit(url)) {
                totalPagesVisited.add(url);
                pagesVisited.get(rootUrl).add(url);
                this.searchUtil(rootUrl, url, pagesVisited, totalPagesVisited, depth + 1);
            }
        }
    }

    private boolean shouldVisit(String url) {
        return !SUFFIX_FILTER.matcher(url).matches() && !PREFIX_FILTER.matcher(url).matches();
    }
}