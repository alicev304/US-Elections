package core.query.handler;

import api.Worker;
import core.nlp.Tokenizer;
import core.query.Document;
import utils.Constants;
import utils.io.FileHandler;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class DocumentHandler {
    private Document[] documents;

    private final String corpusPath;

    public DocumentHandler(String corpusPath) {
        this.corpusPath = corpusPath;
    }

    public DocumentHandler(int cluster, byte cType) {
        this.corpusPath = Constants.TOKENIZED_CORPUS_DIR_PATH;
        List<String> docIds;
        if (cType == 1) {
            docIds = Worker.KMClusteringInv.get(cluster);
        } else {
            docIds = Worker.AggClusteringInv.get(cluster);
        }
        documents = new Document[docIds.size()];
        int counter = 0;
        FileHandler handler = new FileHandler(this.corpusPath);
        for (String docID: docIds) {
            File file = new File(this.corpusPath + docID);
            if (file.isFile()) {
                documents[counter] = toDocument(handler.readFileContent(file), counter);
            }
            counter++;
        }
    }

    public Document[] getDocuments() {
        return documents;
    }

    public String getNameByUrl(String url) {
        for (Document doc: documents) {
            if (doc.getUrl().compareTo(url) == 0) return doc.getName();
        }
        return "DOC NOT FOUND!!!";
    }

    public void loadDocuments() {
        FileHandler fileHandler = new FileHandler(corpusPath);
        Tokenizer tokenizer = new Tokenizer(Tokenizer.LEMMA_TOKENS, Worker.stopwords);
        if(!corpusPath.isEmpty()) {
            File file = new File(corpusPath);
            if(file.isDirectory()) {
                int counter = 0;
                int fileCount = file.listFiles().length;
                documents = new Document[fileCount];
                for(File doc: Objects.requireNonNull(file.listFiles())) {
                    if(counter < fileCount) {
                        if (doc.isFile()) {
                            documents[counter] = toDocument(fileHandler.readFileContent(doc), counter);
                        }
                        counter++;
                    }
                }
            }
            else {
                documents = new Document[1];
                documents[0] = toDocument(fileHandler.readFileContent(file), 0);
            }
        }
    }

    private Document toDocument(List<String> fileTokens, int id) {
        if(fileTokens == null) {
            return null;
        }
        Document document = new Document(id);
        document.setUrl(fileTokens.get(0));
        document.setName(fileTokens.get(1));
        for (int i = 1; i < fileTokens.size(); i++) document.addTerm(fileTokens.get(i));
        fileTokens.forEach(document::addTerm);
        if(Worker.pageRank != null && Worker.pageRank.containsKey(document.getUrl())) {
            document.setPageRank(Worker.pageRank.get(document.getUrl()));
        }
        document.generateVector();
        return document;
    }
}
