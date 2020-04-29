package api.controller;

import api.Worker;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import core.query.Document;
import core.query.QueryExpansion;
import core.query.Roccio;
import core.query.handler.DocumentHandler;
import core.query.handler.QueryHandler;
import spark.Request;
import utils.Constants;
import utils.Utils;

import java.util.*;

public class QueryController implements IController {

    private static final int RPC = 3; // results per cluster

    @Override
    public String handleRequest(Request request) {
        String queryString = request.queryParams("query");
        if(queryString == null) {
            return toGson(StandardResponse.getFailureResponse(Constants.INVALID_PARAMETERS));
        }
        String rModelType = request.queryParams("relevance");
        if(rModelType == null) {
            return toGson(StandardResponse.getFailureResponse(Constants.INVALID_PARAMETERS));
        }
        String cAlgorithmType = request.queryParams("clustering");
        if(cAlgorithmType == null) {
            return toGson(StandardResponse.getFailureResponse(Constants.INVALID_PARAMETERS));
        }
        String qeAlgorithmType = request.queryParams("qe");
        if(qeAlgorithmType == null) {
            return toGson(StandardResponse.getFailureResponse(Constants.INVALID_PARAMETERS));
        }

        core.query.handler.QueryHandler queryHandler = new core.query.handler.QueryHandler(queryString);
        queryHandler.populateQueryObject();
        queryHandler.populateQueryVector(Worker.indexHeaders);

        DocumentHandler documentHandler = new DocumentHandler(Constants.TOKENIZED_CORPUS_DIR_PATH);
        documentHandler.loadDocuments();

        byte modelType = Constants.SIMPLE_COSINE;
        try {
            modelType = Byte.parseByte(rModelType);
        }
        catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }

        byte cType = -1;
        try {
            cType = Byte.parseByte(cAlgorithmType);
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

        byte qeType = -1;
        try {
            qeType = Byte.parseByte(qeAlgorithmType);
        }
        catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }

        JsonObject jsonElement = new JsonObject();

        Map<String, Double> results;

        if(qeType != -1) {
            int K = 20;
            if(qeType == Constants.QE_SCALAR || qeType == Constants.QE_METRIC) {
                K = 2;
            }
            if(qeType == Constants.QE_ROCCHIO) {
                Document[] dp = new Document[5];
                Document[] dn = new Document[5];
                queryHandler.getRNR(documentHandler.getDocuments(), dp, dn);
                String exp_query = Roccio.return_best(queryHandler.getQuery(), dp, dn, queryString);
                QueryHandler newQueryHandler = new QueryHandler(exp_query);
                newQueryHandler.populateQueryObject();
                newQueryHandler.populateQueryVector(Worker.indexHeaders);
                results = newQueryHandler.getTopKDocuments(documentHandler.getDocuments(), modelType, K);
                jsonElement.addProperty("exp_query", exp_query);
            }
            else {
                results = queryHandler.getTopKDocuments(documentHandler.getDocuments(), modelType, K);
                try {
                    String newQuery = QueryExpansion.expander(queryString, results.keySet(), qeType);
                    core.query.handler.QueryHandler newQueryHandler = new core.query.handler.QueryHandler(newQuery);
                    newQueryHandler.populateQueryObject();
                    newQueryHandler.populateQueryVector(Worker.indexHeaders);
                    results = newQueryHandler.getTopKDocuments(documentHandler.getDocuments(), modelType, 20);
                    jsonElement.addProperty("exp_query", newQuery);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        else {
            results = queryHandler.getTopKDocuments(documentHandler.getDocuments(), modelType,20);
        }

        if(cType != -1) {
            int k = 20;
            Map<String, Double> cResults;
            Map<String, Double> ctResults = new LinkedHashMap<>();
            Set<Integer> visitedClusters = new HashSet<>();
            for (String key : results.keySet()) {
                if (ctResults.size() >= k) break;
                int cluster;
                if (cType == Constants.K_MEANS) {
                    cluster = Worker.KMClustering.get(key);
                }
                else {
                    cluster = Worker.AggClustering.get(key);
                }
                if (visitedClusters.contains(cluster)) continue;
                visitedClusters.add(cluster);
                DocumentHandler dh = new DocumentHandler(cluster, cType);
                cResults = queryHandler.getTopKDocuments(dh.getDocuments(), modelType, RPC);
                for (String cKey: cResults.keySet()) {
                    ctResults.put(cKey, cResults.get(cKey));
                    if (ctResults.size() >= k) break;
                }
            }
            results = ctResults;
        }

        int counter = 1;
        JsonArray jsonArray = new JsonArray();
        for (String key : results.keySet()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("title", documentHandler.getNameByUrl(key.replace("__hub__", "").replace("__auth__", "")));
            jsonObject.addProperty("rank", counter++);
            jsonObject.addProperty("url", key.replace("__hub__", "").replace("__auth__", ""));
            jsonObject.addProperty("score", results.get(key));
            jsonArray.add(jsonObject);
        }
        System.out.println(results);
        jsonElement.add("results", jsonArray);
        jsonElement.addProperty("query", queryString);
        return toGson(StandardResponse.getSuccessResponse(jsonElement));
    }

    private String toGson(StandardResponse standardResponse) {
        return new Gson().toJson(standardResponse);
    }
}
