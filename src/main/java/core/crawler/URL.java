package core.crawler;

public class URL {

    String parentUrl;
    String url;
    int depth;

    public URL(String url, int depth, String parentUrl) {
        this.url = url;
        this.depth = depth;
        this.parentUrl = parentUrl;
    }
}
