package api.handler;

import api.Worker;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import core.query.QueryExpansion;
import core.query.handler.DocumentHandler;
import spark.Request;
import utils.Constants;

import java.util.Map;

public class QHandler implements IHandler {

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

        byte qeType = -1;
        try {
            qeType = Byte.parseByte(qeAlgorithmType);
        }
        catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }

        JsonObject jsonElement = new JsonObject();

        Map<String, Double> results;
        System.out.println(qeType + " qeType");

        if(qeType != -1) {
            int K = 20;
            if(qeType == Constants.QE_SCALAR || qeType == Constants.QE_METRIC) {
                K = 2;
            }
            results = queryHandler.getTopKDocuments(documentHandler.getDocuments(), (byte)(modelType % 3), K, false);
            try {
                String newQuery = QueryExpansion.expander(queryString, results.keySet(), qeType);
                core.query.handler.QueryHandler newQueryHandler = new core.query.handler.QueryHandler(newQuery);
                newQueryHandler.populateQueryObject();
                newQueryHandler.populateQueryVector(Worker.indexHeaders);
                results = newQueryHandler.getTopKDocuments(documentHandler.getDocuments(), modelType,20, true);
                jsonElement.addProperty("exp_query", newQuery);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        else {
            results = queryHandler.getTopKDocuments(documentHandler.getDocuments(), modelType,20, true);
        }

        int counter = 1;
        JsonArray jsonArray = new JsonArray();
        if(modelType == Constants.SIMPLE_COSINE_W_HITS) {
            for (String key : results.keySet()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("rank", counter++);
                jsonObject.addProperty("url", key.replace("__hub__", "").replace("__auth__", ""));
                jsonObject.addProperty("score", results.get(key));
                jsonArray.add(jsonObject);
            }
        }
        else {
            for (String key : results.keySet()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("rank", counter++);
                jsonObject.addProperty("url", key);
                jsonObject.addProperty("score", results.get(key));
                jsonArray.add(jsonObject);
            }
        }
        jsonElement.add("results", jsonArray);
        jsonElement.addProperty("query", queryString);
        return toGson(StandardResponse.getSuccessResponse(jsonElement));
    }

    private String toGson(StandardResponse standardResponse) {
        return new Gson().toJson(standardResponse);
    }
}
