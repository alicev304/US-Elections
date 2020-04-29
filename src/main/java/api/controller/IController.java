package api.controller;

import spark.Request;

public interface IController {
    String handleRequest(Request request);
}
