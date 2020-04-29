package core.ranker;

import utils.Constants;
import utils.io.FileHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraphBuilder {

    private final String filePath;

    private final Graph graph;

    public GraphBuilder() {
        this.filePath = Constants.LIST_OF_URLS;
        graph = new Graph();
    }

    public void constructGraph() {
        FileHandler fileHandler = new FileHandler(filePath);
        List<String> content = null;
        if(!filePath.isEmpty()) {
            content = fileHandler.readFileContents();
        }
        if(content != null) {
            content.forEach((item) -> {
                String[] contentSplit = item.trim().split(" ");
                graph.addNode(contentSplit[0]);
                if(contentSplit.length == 2) {
                    graph.addNode(contentSplit[1]);
                    graph.addEdge(contentSplit[0], contentSplit[1]);
                }
            });
        }
    }

    public static Graph extractSubGraph(Graph graph, Set<String> rootSetURL) {
        Graph subGraph = new Graph();
        Map<String, Node> nodes = graph.getNodes();

        for(String rootUrl: rootSetURL) {
            subGraph.addNode(rootUrl);
            if(nodes.containsKey(rootUrl)) {
                Node node = nodes.get(rootUrl);
                if (node.getTargetNodes() != null) {
                    for (Node tagetNode : node.getTargetNodes()) {
                        subGraph.addNode(tagetNode.getName());
                        subGraph.addEdge(rootUrl, tagetNode.getName());
                    }
                }
                if (node.getSourceNodes() != null) {
                    for (Node sourceNode : node.getSourceNodes()) {
                        subGraph.addNode(sourceNode.getName());
                        subGraph.addEdge(sourceNode.getName(), rootUrl);
                    }
                }
            }
            else {
                System.out.println(rootUrl + " is not present in the graph");
            }
        }

        return subGraph;
    }

    public Graph getGraph() {
        return graph;
    }

    public static void generateGraphStatistics(Graph graph) {
        if(graph != null && graph.getNodes() != null) {
            List<Integer> inLinks = new ArrayList<>();
            List<Integer> outLinks = new ArrayList<>();
            int totalLinks = 0;
            System.out.println("Number of nodes in the graph: " + graph.getNodes().size());
            for(Node node: graph.getNodes().values()) {
                if(node.getSourceNodes() != null) {
                    inLinks.add(node.getSourceNodes().size());
                    totalLinks += node.getSourceNodes().size();
                }
                if(node.getTargetNodes() != null) {
                    outLinks.add(node.getTargetNodes().size());
                }
            }
            System.out.println("Total number of links in the graph: " + totalLinks);
            inLinks.sort((o1, o2) -> -o1.compareTo(o2));
            System.out.println("Largest number of incoming links: " + inLinks.get(0));
            outLinks.sort((o1, o2) -> -o1.compareTo(o2));
            System.out.println("Largest number of outgoing links: " + outLinks.get(0));
        }
    }
}