package utils;

import core.query.Document;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {

    public static String formatInput(String input) {
        return input.toLowerCase()
                .replaceAll(Constants.HYPHEN_REGEX, " ");
    }

    public static double dotProduct(Map<String, Double> vector1, Map<String, Double> vector2) {
        double dotProduct = 0.0;
        for(String key: vector1.keySet()) {
            if(vector2.containsKey(key)) {
                dotProduct += (vector1.get(key) * vector2.get(key));
            }
        }
        return dotProduct;
    }

    public static Map<String, Double> sortMap(Map<String, Double> documents) {
        return documents.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }
}
