package api.handler;

import spark.Request;

public interface IHandler {
    String handleRequest(Request request);
}
