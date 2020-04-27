package core.ranker;

import java.util.HashMap;
import java.util.Map;

public class Graph {

    Map<String, Node> nodes = null;

    public Graph() {
        this.nodes = new HashMap<>(1);
    }

    public Map<String, Node> getNodes() {
        return nodes;
    }

    public void addNode(String name) {
        if(!nodes.containsKey(name)) {
            Node node = new Node(name);
            nodes.put(name, node);
        }
    }

    public void addEdge(String node1Name, String node2Name) {
        Node node1 = nodes.get(node1Name);
        Node node2 = nodes.get(node2Name);
        if(node1 != null && node2 != null) {
            node1.addTargetNode(node2);
            node2.addSourceNode(node1);
        }
    }

    public void initializeGraphWeights(double initialWeight) {
        nodes.values().forEach((node) -> node.setWeight(initialWeight));
    }
}