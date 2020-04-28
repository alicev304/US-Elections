package api;

import api.handler.QHandler;
import core.corpus.CorpusBuilder;
import core.corpus.Tokenizer;
import core.crawler.Crawler;
import core.indexer.SPIMI;
import core.ranker.Graph;
import core.ranker.GraphBuilder;
import core.ranker.PageRank;
import spark.ModelAndView;
import spark.Spark;
import spark.template.velocity.VelocityTemplateEngine;
import utils.Constants;
import utils.io.FileHandler;
import utils.io.IndexFileHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static spark.Spark.*;

public class Worker {
    public static Map<String, List<String>> tokenMap = null;

    public static Map<String, Short> indexHeaders = null;

    public static Map<String, Double> pageRank = null;

    public static Graph mainGraph = null;

    public static HashSet<String> stopwords = null;

    public static void main(String[] args) {

        Spark.staticFileLocation("/");
        loadStopwords(Constants.STOPWORDS);
        fetchLinks();
        buildCorpus();
        createIndex();
        loadGraph();
        loadIndexHeaders();
        loadPageRankScores();

        port(8080);
        //GET REST APIS
        get("/hello", (req, res) -> "Current time is " + new Date(System.currentTimeMillis()).toString());

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            return new VelocityTemplateEngine().render(
                    new ModelAndView(model, "templates/home.vm")
            );
        });

        get("/compare", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("query", req.queryParams("query"));
            model.put("relevance", req.queryParams("relevance"));
            model.put("clustering", req.queryParams("clustering"));
            model.put("qe", req.queryParams("qe"));
            return new VelocityTemplateEngine().render(
                    new ModelAndView(model, "templates/compare.vm")
            );
        });

        get("/results", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("query", req.queryParams("query"));
            model.put("expandedQuery", req.queryParams("expquery"));
            model.put("relevance", req.queryParams("relevance"));
            model.put("clustering", req.queryParams("clustering"));
            model.put("qe", req.queryParams("qe"));
            return new VelocityTemplateEngine().render(
                    new ModelAndView(model, "templates/results.vm")
            );
        });

        //POST REST APIS
        post("/search", (request, response) -> {
            QHandler queryController = new QHandler();
            return queryController.handleRequest(request);
        });
    }

    private static void fetchLinks() {
        File linkFile = new File(Constants.LIST_OF_URLS);
        if (!linkFile.exists()) {
            Crawler crawler = new Crawler();
            crawler.crawl();
        }
    }

    private static void buildCorpus() {
        File dir = new File(Constants.TOKENIZED_CORPUS_DIR_PATH);
        if (dir.isDirectory() && Objects.requireNonNull(dir.list()).length == 0) {
            Tokenizer tokenizer = new Tokenizer(Tokenizer.LEMMA_TOKENS, stopwords);
            CorpusBuilder builder = new CorpusBuilder(Constants.LIST_OF_URLS);
            builder.build(tokenizer, true);
            tokenMap = tokenizer.getTokenMap();
        }
    }

    private static void createIndex() {
        File file = new File(Constants.INDEX_FILE);
        if (!file.exists()) {
            SPIMI spimi = new SPIMI();
            spimi.createIndex(tokenMap, Constants.INDEX_FILE);
        }
    }

    private static void loadIndexHeaders() {
        IndexFileHandler handler = new IndexFileHandler(Constants.INDEX_FILE);
        indexHeaders = handler.readIndexHeaders();
    }

    private static void loadPageRankScores() {
        FileHandler fileHandler = new FileHandler(Constants.PAGE_RANKS);
        List<String> content = fileHandler.reagPageRanks();
        if(content != null && content.size() > 0) {
            if (pageRank == null) {
                pageRank = new HashMap<>();
            }
            content.forEach((item) -> {
                String[] contentSplit = item.split(" ");
                if (contentSplit.length == 2) {
                    pageRank.put(contentSplit[0], Double.parseDouble(contentSplit[1]));
                }
            });
        }
    }

    public static void loadGraph() {
        GraphBuilder builder = new GraphBuilder();
        builder.constructGraph();
        mainGraph = builder.getGraph();
        GraphBuilder.generateGraphStatistics(builder.getGraph());
        PageRank pageRank = new PageRank(builder.getGraph());
        pageRank.computeGraphPageRank();
        pageRank.writeResults(Constants.PAGE_RANKS);
    }

    public static void loadStopwords(String stopWordsFilePath) {
        stopwords = new HashSet<>(1);
        try {
            File file = new File(stopWordsFilePath);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                stopwords.add(line.toLowerCase().trim());
            }
            reader.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

//    private static void loadHITSScores() {
//        FileHandler fileHandler = new FileHandler(Constants.HITS);
//        List<String> content = fileHandler.readFileContent();
//        if(content != null && content.size() > 0) {
//            if (authorityScores == null) {
//                authorityScores = new HashMap<>();
//            }
//            if (hubScores == null) {
//                hubScores = new HashMap<>();
//            }
//            content.forEach((item) -> {
//                String[] contentSplit = item.split(" ");
//                if (contentSplit.length == 3) {
//                    double hubScore = Double.parseDouble(contentSplit[1]);
//                    double authScore = Double.parseDouble(contentSplit[2]);
//                    if (hubScore == 0.0) {
//                        authorityScores.put(contentSplit[0], authScore);
//                    } else if (authScore == 0.0) {
//                        authorityScores.put(contentSplit[0], hubScore);
//                    }
//                }
//            });
//        }
//    }
}
