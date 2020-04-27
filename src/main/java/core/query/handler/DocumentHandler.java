package core.query.handler;

import api.Worker;
import core.query.Document;
import utils.Constants;
import utils.io.FileHandler;

import java.io.File;
import java.util.List;

public class DocumentHandler {
    private Document[] documents;

    private String corpusPath;

    private FileHandler fileHandler;

    private int fileCount = Constants.MAX_BLOCK_FILES_COUNT;

    public DocumentHandler(String corpusPath) {
        this.corpusPath = corpusPath;
    }

    public DocumentHandler(String corpusPath, int fileCount) {
        this.corpusPath = corpusPath;
        this.fileCount = fileCount;
    }

    public Document[] getDocuments() {
        return documents;
    }

    public void loadDocuments() {
        fileHandler = new FileHandler(corpusPath);
        if(!corpusPath.isEmpty()) {
            File file = new File(corpusPath);
            if(file.isDirectory()) {
                int counter = 0;
                documents = new Document[fileCount];
                for(File file1: file.listFiles()) {
                    if(counter < fileCount) {
                        if (file1.isFile()) {
                            documents[counter] = toDocument(fileHandler.readFileContent(file1), counter, file1.getName().replace(".txt", ""));
                        }
                        counter++;
                    }
                }
            }
            else {
                documents = new Document[1];
                documents[0] = toDocument(fileHandler.readFileContent(file), 0, file.getName());
            }
        }
    }

    private Document toDocument(List<String> fileTokens, int id, String name) {
        if(fileTokens == null) {
            return null;
        }
        Document document = new Document(id);
        document.setName(name);
        fileTokens.forEach(document::addTerm);
        if(Worker.pageRank != null && Worker.pageRank.containsKey(name)) {
            document.setPageRank(Worker.pageRank.get(name));
        }
        document.generateVector();
        return document;
    }
}
