package utils.io;

import core.corpus.Tokenizer;
import core.ranker.Graph;
import core.ranker.Node;
import utils.Constants;
import utils.filter.IFilter;
import utils.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;;
import java.util.Map;

public class FileHandler {
    private String filePath;
    private IFilter filter;
    private boolean format = false;

    public FileHandler(String filePath) {
        this.filePath = filePath;
    }

    public FileHandler(String filePath, IFilter filter) {
        this.filePath = filePath;
        this.filter = filter;
    }

    public FileHandler(String filePath, IFilter filter, boolean format) {
        this.filePath = filePath;
        this.filter = filter;
        this.format = format;
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

    public void writeCorpusToFiles(String title, String text) {
        File file = new File(filePath);
        if(!file.isDirectory()) {
            try {
                FileWriter writer = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
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

    public List<String> readBrokenFile(File file, Tokenizer tokenizer) {
        List<String> content = null;
        if(file.isFile()) {
            try {
                content = new ArrayList<>();
                FileReader reader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    List<String> tokens = tokenizer.
                }
                bufferedReader.close();
                reader.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return content;
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

    public Map<String, String> readCorpus() {
        Map<String, String> content = new HashMap<String, String>();
        try {
            File dir = new File(filePath);
            for(File file: dir.listFiles()) {
                if(file.isFile()) {
                    FileReader reader = new FileReader(file);
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String title = bufferedReader.readLine();
                    StringBuilder text = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        if(format) {
                            line = Utils.formatInput(line);
                        }
                        text.append(line.trim()).append(" ");
                    }
                    String textString = text.toString();
                    if (filter != null) {
                        textString = filter.filter(textString);
                    }
                    content.put(title, textString);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public void deleteFile() {
        File file = new File(filePath);
        if(file.exists())
            file.delete();
    }
}
