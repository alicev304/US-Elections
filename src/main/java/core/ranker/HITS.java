package core.ranker;

import java.util.HashMap;
import java.util.Map;

public class HITS {

    private final Graph webGraph;

    private double initialWeights = 1.0;

    Map<String, Double[]> hubScores;
    Map<String, Double[]> authorityScores;

    public HITS(Graph graph) {
        this.webGraph = graph;
    }

    public HITS(Graph graph, double initialWeights) {
        this.webGraph = graph;
        this.initialWeights = initialWeights;
    }

    public void computeHITSScores() {
        Map<String, Node> nodes = webGraph.getNodes();
        hubScores = new HashMap<>();
        authorityScores = new HashMap<>();
        for (String key: nodes.keySet()) {
            hubScores.put(key, new Double[] {initialWeights, initialWeights});
            authorityScores.put(key, new Double[] {initialWeights, initialWeights});
        }
        while (true) {
            double totalHubScore = 0.0;
            double totalAuthorityScore = 0.0;
            boolean doBreak = true;
            for(String key: nodes.keySet()) {
                Node node = nodes.get(key);
                double hubWeight = 0.0;
                double authWeight = 0.0;
                if(node.getSourceNodes() != null) {
                    for (Node sourceNode : node.getSourceNodes()) {
                        hubWeight += hubScores.get(sourceNode.getName())[1];
                    }
                    totalAuthorityScore += hubWeight;
                }
                if(node.getTargetNodes() != null) {
                    for (Node targetNode : node.getTargetNodes()) {
                        authWeight += authorityScores.get(targetNode.getName())[1];
                    }
                    totalHubScore += authWeight;
                }
                Double[] hubWeights = authorityScores.get(key);
                hubWeights[1] = hubWeight;
                authorityScores.put(key, hubWeights);
                Double[] authWeights = hubScores.get(key);
                authWeights[1] = authWeight;
                hubScores.put(key, authWeights);
            }
            for(String key: hubScores.keySet()) {
                Double[] hubWeights = hubScores.get(key);
                hubWeights[1] = hubWeights[1] / totalHubScore;
                if(Math.abs(hubWeights[0] - hubWeights[1]) > 0.0001) {
                    hubWeights[0] = hubWeights[1];
                    hubScores.put(key, hubWeights);
                    doBreak = false;
                }
                Double[] authorityWeights = authorityScores.get(key);
                authorityWeights[1] = authorityWeights[1] / totalAuthorityScore;
                if(Math.abs(authorityWeights[0] - authorityWeights[1]) > 0.0001) {
                    authorityWeights[0] = authorityWeights[1];
                    authorityScores.put(key, authorityWeights);
                    doBreak = false;
                }
            }
            if(doBreak) {
                break;
            }
        }

        for (Node node: webGraph.getNodes().values()) {
            double hubScore = hubScores.get(node.getName())[0];
            double authScore = authorityScores.get(node.getName())[0];
            if(hubScore >= authScore) {
                node.setType(node.TYPE_HUB);
                node.setWeight(hubScore);
            }
            else {
                node.setType(node.TYPE_AUTHORITY);
                node.setWeight(authScore);
            }
        }
    }

    public Map<String, Double[]> getHubScores() {
        return hubScores;
    }

    public Map<String, Double[]> getAuthorityScores() {
        return authorityScores;
    }
}
