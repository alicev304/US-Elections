package core.query;

import java.util.HashMap;
import java.util.Map;

public class Document {
    public final boolean TYPE_HUB = true;
    public final boolean TYPE_AUTHORITY = false;

    private int id;

    private String name;

    private Map<String, Short> termMap;

    private boolean type;

    private double pageRank;

    private double weight;

    private short maxTF;

    private short length;

    private Map<String, Double> vector;

    public Document(int id) {
        this.id = id;
        termMap = new HashMap<>(1);
        type = TYPE_AUTHORITY;
    }

    public void addTerm(String term) {
        if(termMap.containsKey(term)) {
            short frequency = termMap.get(term);
            if(maxTF < frequency + 1) {
                maxTF = (short)(frequency + 1);
            }
            termMap.put(term, (short)(frequency + 1));
        }
        else {
            termMap.put(term, (short) 1);
            if(maxTF < 1) {
                maxTF = (short) 1;
            }
        }
        length++;
    }

    public short getMaxTF() {
        return maxTF;
    }

    public short getLength() {
        return length;
    }

    public Map<String, Short> getTermMap() {
        return termMap;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Double> getVector() {
        return vector;
    }

    public boolean getType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getPageRank() {
        return pageRank;
    }

    public void setPageRank(double pageRank) {
        this.pageRank = pageRank;
    }

    public void generateVector() {
        vector = new HashMap<>();
        double normalizationValue = 0;
        for(String term: termMap.keySet()) {
            int tf = termMap.containsKey(term) ? termMap.get(term) : 0;
            double tfwt = tf == 0 ? 0 : (1 + Math.log10(tf));
            normalizationValue = normalizationValue + (tfwt * tfwt);
            vector.put(term, tfwt);
        }
        normalizationValue = Math.sqrt(normalizationValue);
        for(String term: termMap.keySet()) {
            vector.put(term, vector.get(term) / normalizationValue);
        }
    }
}
