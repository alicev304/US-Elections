package utils.io;

import core.nlp.Tokenizer;
import core.ranker.Graph;
import core.ranker.Node;

import java.io.*;
import java.util.*;

public class FileHandler {
    private final String filePath;

    public FileHandler(String filePath) {
        this.filePath = filePath;
    }

    public void writeLinksToFile(Map<String, Set<String>> pagesVisited) {
        File file = new File(filePath);
        if(!file.isDirectory()) {
            try {
                FileWriter writer = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                for (String rootUrl: pagesVisited.keySet()) {
                    for (String url: pagesVisited.get(rootUrl)) {
                        bufferedWriter.write(rootUrl + " " + url + '\n');
                    }
                }
                bufferedWriter.close();
                writer.close();
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void writeCorpusToFile(String title, String text, String url, Tokenizer tokenizer) {
        File file = new File(filePath);
        if(!file.isDirectory()) {
            try {
                FileWriter writer = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.write(url + '\n');
                bufferedWriter.write(title + '\n');
                bufferedWriter.write(String.join(" ", tokenizer.tokenize(file.getName(), text)));
                bufferedWriter.close();
                writer.close();
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void writeGraphToFile(Graph graph, boolean doSimpleWrite) {
        if(graph != null && graph.getNodes() != null) {
            Map<String, Node> nodes = graph.getNodes();
            File file = new File(filePath);
            if(!file.isDirectory()) {
                try {
                    FileWriter writer = new FileWriter(file);
                    BufferedWriter bufferedWriter = new BufferedWriter(writer);
                    for(String key: nodes.keySet()) {
                        Node node = nodes.get(key);
                        if(doSimpleWrite) {
                            bufferedWriter.write(key + " " + node.getWeight() + "\n");
                        }
                        else if(node.getType() == node.TYPE_HUB) {
                            bufferedWriter.write(key + " " + node.getWeight() + " 0\n");
                        }
                        else {
                            bufferedWriter.write(key + " 0 " + node.getWeight() + "\n");
                        }
                    }
                    bufferedWriter.close();
                    writer.close();
                }
                catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public List<String> readFileContent(File file) {
        List<String> content = null;
        if(file.isFile()) {
            try {
                content = new ArrayList<>();
                FileReader reader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line = bufferedReader.readLine();
                content.add(line);
                line = bufferedReader.readLine();
                content.add(line);
                while ((line = bufferedReader.readLine()) != null) {
                    content.addAll(Arrays.asList(line.split(" ")));
                }
                bufferedReader.close();
                reader.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return content;
    }

    public Map<String, String> readFiles() {
        Map<String, String> content = new HashMap<>();
        File dir = new File(filePath);
        if (dir.isDirectory() && Objects.requireNonNull(dir.listFiles()).length > 0) {
            for (File file: Objects.requireNonNull(dir.listFiles())) {
                try{
                    FileReader reader = new FileReader(file);
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String line = bufferedReader.readLine();
                    String title = bufferedReader.readLine();
                    String docID = file.getName();
                    StringBuilder text = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        text.append(line.trim()).append(" ");
                    }
                    content.put(docID, text.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return content;
    }

    public List<String> readFileContents() {
        File file = new File(filePath);
        List<String> content = null;
        if(file.isFile()) {
            try {
                content = new ArrayList<>();
                FileReader reader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    content.add(line);
                }
                bufferedReader.close();
                reader.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return content;
    }

    public void readClusterFile(Map<String, Integer> cluster, Map<Integer, List<String>> clusterInv) {
        File file = new File(filePath);
        if (file.isFile()) {
            try {
                FileReader reader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] content = line.split(" ");
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < content.length-2; i++) {
                        builder.append(content[i]).append(" ");
                    }
                    cluster.put(builder.toString().trim(), Integer.parseInt(content[content.length-2]));
                    if (!clusterInv.containsKey(Integer.parseInt(content[content.length-2]))) {
                        clusterInv.put(Integer.parseInt(content[content.length-2]), new ArrayList<>());
                    }
                    clusterInv.get(Integer.parseInt(content[content.length-2])).add(content[content.length-1]);
                }
                bufferedReader.close();
                reader.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void deleteFile() {
        File file = new File(filePath);
        if(file.exists())
            file.delete();
    }

    public static int getDocID(String dirPath) {
        File dir = new File(dirPath);
        if (dir.isDirectory()) {
            return Objects.requireNonNull(dir.listFiles()).length;
        }
        return -1;
    }
}
