package core.ranker;

import utils.io.FileHandler;

import java.util.HashMap;
import java.util.Map;

public class PageRank {

    private final Graph graph;

    public PageRank(Graph graph) {
        this.graph = graph;
    }

    public Graph getGraph() {
        return this.graph;
    }

    public void computeGraphPageRank() {
        graph.initializeGraphWeights(1.0 / graph.getNodes().size());
        Map<String, Node> nodes = graph.getNodes();
        Map<String, Double> nodeScore = new HashMap<>();
        double dampingFactor = 0.8;
        double startingWeight = (1.0 - dampingFactor);
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

