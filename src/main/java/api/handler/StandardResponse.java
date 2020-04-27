package api.handler;

import com.google.gson.JsonElement;
import utils.Constants;

public class StandardResponse {
    private String status;

    private String message;

    private JsonElement data;

    public StandardResponse(String status) {
        this.status = status;
    }

    public StandardResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public StandardResponse(String status, JsonElement jsonElement) {
        this.status = status;
        this.data = jsonElement;
    }

    public static StandardResponse getSuccessResponse() {
        return new StandardResponse(Constants.SUCCESS_STATUS);
    }

    public static StandardResponse getFailureResponse(String message) {
        return new StandardResponse(Constants.FAILURE_STATUS, message);
    }

    public static StandardResponse getSuccessResponse(JsonElement jsonElement) {
        return new StandardResponse(Constants.SUCCESS_STATUS, jsonElement);
    }
}
