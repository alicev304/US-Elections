package core.query.handler;

import api.Worker;
import core.corpus.Tokenizer;
import core.query.Document;
import core.query.Query;
import core.ranker.Graph;
import core.ranker.GraphBuilder;
import core.ranker.HITS;
import org.apache.commons.codec.binary.Base64;
import utils.Constants;
import utils.Utils;
import utils.filter.IFilter;
import utils.filter.QueryFilter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class QueryHandler {
    private final String queryStr;

    private Query query = null;

    public QueryHandler(String queryStr) {
        this.queryStr = queryStr;
    }

    public Query getQuery() {
        return query;
    }

    public void populateQueryObject() {
        if(!queryStr.isEmpty()) {
            query = new Query((short) 0);

            Tokenizer tokenizer = new Tokenizer(Tokenizer.LEMMA_TOKENS, Worker.stopwords);
            String fQueryStr = Utils.formatInput(queryStr);
            IFilter filter = new QueryFilter();
            filter.construct();
            fQueryStr = filter.filter(fQueryStr.trim());
            tokenizer.tokenize("query", fQueryStr);
            tokenizer.getTokenMap()
                    .get("query")
                    .forEach(query::addTerm);
        }
    }

    public void populateQueryVector(Map<String, Short> indexHeaders) {
        if(query != null && query.getTermMap().size() > 0 && indexHeaders != null) {
            query.generateVector(indexHeaders);
        }
    }

    public Map<String, Double> getTopKDocuments(Document[] documents, byte method, int K) {
        if(K > documents.length) {
            K = documents.length;
        }
        Map<String, Double> csResults = getTopKDocumentsWithCosineSim(documents, K);
        if(method == Constants.SIMPLE_COSINE) {
            return csResults;
        }
        if(method == Constants.SIMPLE_COSINE_W_PR) {
            return reorderDocumentsWPR(csResults);
        }
        if (method == Constants.SIMPLE_COSINE_W_HITS) {
            Graph subGraph = GraphBuilder.extractSubGraph(Worker.mainGraph, csResults.keySet());
            HITS hits = new HITS(subGraph);
            hits.computeHITSScores();
            return reorderDocumentsWHITS(hits, csResults.keySet());
        }
        return null;
    }

    private Map<String, Double> getTopKDocumentsWithCosineSim(Document[] documents, int K) {
        Map<String, Double> queryDocumentCosineScores = new LinkedHashMap<>();
        for (Document document : documents) {
            if(document != null) {
                double score = Utils.dotProduct(query.getVector(), document.getVector());
                queryDocumentCosineScores.put(document.getUrl(), score);
            }
        }
        queryDocumentCosineScores = Utils.sortMap(queryDocumentCosineScores);
        Map<String, Double> result = new LinkedHashMap<>();
        for(String key: queryDocumentCosineScores.keySet()) {
            result.put(key, queryDocumentCosineScores.get(key));
            if (--K == 0) {
                break;
            }
        }
        return result;
    }

    private Map<String, Double> reorderDocumentsWPR(Map<String, Double> csResults) {
        Map<String, Double> prResults = new LinkedHashMap<>(csResults);
        if(Worker.pageRank != null) {
            for(String key: csResults.keySet()) {
                prResults.put(key, Worker.pageRank.getOrDefault(key, 0.0));
            }
            return Utils.sortMap(prResults);
        }
        return prResults;
    }

    private Map<String, Double> reorderDocumentsWHITS(HITS hits, Set<String> csResultsKeys) {
        Map<String, Double> hitsResults = new LinkedHashMap<>();
        Map<String, Double> hubScores = new LinkedHashMap<>();
        Map<String, Double> authorityScores = new LinkedHashMap<>();
        Map<String, Double[]> actualHubScores= hits.getHubScores();
        Map<String, Double[]> actualAuthScores= hits.getAuthorityScores();
        for(String key: actualAuthScores.keySet()) {
            if(actualHubScores.containsKey(key)) {
                hubScores.put(key, actualHubScores.get(key)[1]);
            }
            if(actualAuthScores.containsKey(key)) {
                authorityScores.put(key, actualAuthScores.get(key)[1]);
            }
        }
        hubScores = Utils.sortMap(hubScores);
        authorityScores = Utils.sortMap(authorityScores);
        byte counter = 0;
        for(String key: hubScores.keySet()) {
            if(counter < 10)
                hitsResults.put(key + "__hub__", hubScores.get(key));
            else
                break;
            counter++;
        }
        counter = 10;
        for(String key: authorityScores.keySet()) {
            if(counter > 0)
                hitsResults.put(key + "__auth__", authorityScores.get(key));
            else
                break;
            counter--;
        }
        return hitsResults;
    }

    public void getRNR(Document[] documents, Document[] dp, Document[] dn) {
        Map<Document, Double> rnr = new LinkedHashMap<>();
        for (Document document : documents) {
            if(document != null) {
                double score = Utils.dotProduct(query.getVector(), document.getVector());
                rnr.put(document, score);
            }
        }
        rnr = rnr.entrySet()
                .stream()
                .sorted(Map.Entry.<Document, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
        int counter = 5;
        for (Document doc: rnr.keySet()) {
            dp[--counter] = doc;
            if (counter == 0) {
                break;
            }
        }
        rnr = rnr.entrySet()
                .stream()
                .sorted(Map.Entry.<Document, Double>comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
        counter = 5;
        for (Document doc: rnr.keySet()) {
            dn[--counter] = doc;
            if (counter == 0) {
                break;
            }
        }
        System.out.println("Rocchio done!!!");
    }
}
