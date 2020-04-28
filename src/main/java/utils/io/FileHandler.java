package utils.io;

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

    public void writeCorpusToFiles(String title, String text, String url) {
        File file = new File(filePath);
        if(!file.isDirectory()) {
            try {
                FileWriter writer = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.write(url + '\n');
                bufferedWriter.write(title + '\n');
                bufferedWriter.write(text);
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

    public void readLinks(List<String> listOfUrls) {
        File file = new File(filePath);
        if(!file.isDirectory()) {
            try {
                FileReader reader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.split(" ").length > 1) {
                        listOfUrls.add(line.split(" ")[1]);
                    }
                }
                bufferedReader.close();
                reader.close();
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public List<String> readFileContent() {
        File file = new File(filePath);
        return readFileContent(file);
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

    public List<String> reagPageRanks() {
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

    public List<String> readUrls(File file) {
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

    public void deleteFile() {
        File file = new File(filePath);
        if(file.exists())
            file.delete();
    }
}
