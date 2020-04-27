package core.ranker;

import utils.io.FileHandler;

import java.util.HashMap;
import java.util.Map;

public class PageRank {

    private Graph graph;

    private double dampingFactor = 0.8;

    public PageRank(Graph graph) {
        this.graph = graph;
    }

    public PageRank(Graph graph, double dampingFactor) {
        this.graph = graph;
        this.dampingFactor = dampingFactor;
    }

    public Graph getGraph() {
        return this.graph;
    }

    public void computeGraphPageRank() {
        graph.initializeGraphWeights(1.0 / graph.getNodes().size());
        Map<String, Node> nodes = graph.getNodes();
        Map<String, Double> nodeScore = new HashMap<>();
        double startingWeight = (1.0 - dampingFactor);
        int counter = 1;
        while (true) {
            boolean doBreak = true;
            for(String key: nodes.keySet()) {
                Node node = nodes.get(key);
                double weight = startingWeight;
                if(node.getSourceNodes() != null) {
                    for (Node sourceNode : node.getSourceNodes()) {
                        weight += (dampingFactor * (sourceNode.getWeight() / sourceNode.getNodeCapacity()));
                    }
                }
                nodeScore.put(key, weight);
            }
            for(Node node: nodes.values()) {
                double weight = nodeScore.get(node.getName());
                if(Math.abs(node.getWeight() - weight) > 0.00001) {
                    doBreak = false;
                    node.setWeight(weight);
                }
            }
            if(doBreak) {
                break;
            }
        }
    }

    public void writeResults(String filePath) {
        FileHandler handler = new FileHandler(filePath);
        handler.writeGraphToFile(graph, true);
    }
}

