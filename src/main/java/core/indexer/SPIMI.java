package core.indexer;

import utils.io.FileHandler;
import utils.io.IndexFileHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class SPIMI {

    private Map<String, LinkedList<Posting>> index = null;
    private byte indexCounter = 0;

    public void createIndex(Map<String, List<String>> termMap, String outFile) {
        int counter = 1;
        for(String docId: termMap.keySet()) {
            try {
                List<String> termList = termMap.get(docId);
                if(index == null) {
                    index = new LinkedHashMap<>();
                }
                for(String term: termList) {
                    if(index.containsKey(term)) {
                        LinkedList<Posting> postingList = index.get(term);
                        Posting postingFileItem = new Posting(counter);
                        int itemIndex = postingList.indexOf(postingFileItem);
                        if(itemIndex > -1) {
                            postingList.get(itemIndex).incrementTermFrequency();
                        }
                        else {
                            postingList.add(postingFileItem);
                        }
                    }
                    else {
                        Posting postingFileItem = new Posting(counter);
                        LinkedList<Posting> postingList = new LinkedList<>();
                        postingList.add(postingFileItem);
                        index.put(term, postingList);
                    }
                }
                counter++;
            }
            catch (OutOfMemoryError e) {
                System.out.println("No more space left for the index. Writing out the index and resuming index creation.");
                writeIndexToFile(outFile);
            }
        }
        writeIndexToFile(outFile);
        mergeIndexes(outFile);
    }

    private void writeIndexToFile(String outFile) {
        if(index.size() == 0) {
            return;
        }
        index = sortIndex(index);
        IndexFileHandler handler = new IndexFileHandler(outFile + "_" + indexCounter);
        handler.write(index);
        indexCounter++;
        index.clear();
    }

    private Map<String, LinkedList<Posting>> sortIndex(Map<String, LinkedList<Posting>> index) {
        return index.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    private void mergeIndexes(String outFilePrefix) {
        try {
            File file = new File(outFilePrefix);
            if (file.isFile()) {
                File newFile = new File(outFilePrefix + "_" + indexCounter);
                Files.move(Paths.get(file.getAbsolutePath()), Paths.get(newFile.getAbsolutePath()));
                indexCounter++;
            }
            else if(indexCounter == 1) {
                File oldFile = new File(outFilePrefix + "_0");
                Files.move(Paths.get(oldFile.getAbsolutePath()), Paths.get(file.getAbsolutePath()));
                indexCounter = 0;
                return;
            }
            String index1FileName = outFilePrefix + "_0";
            String index2FileName = outFilePrefix + "_1";
            mergeIndexes(outFilePrefix, index1FileName, index2FileName);
            FileHandler handler1 = new FileHandler(index1FileName);
            handler1.deleteFile();
            FileHandler handler2 = new FileHandler(index2FileName);
            handler2.deleteFile();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void mergeIndexes(String outFile, String index1FileName, String index2FileName) {
        IndexFileHandler[] randomAccessFileHandlers = new IndexFileHandler[3];
        randomAccessFileHandlers[0] = new IndexFileHandler(outFile);
        randomAccessFileHandlers[1] = new IndexFileHandler(index1FileName);
        randomAccessFileHandlers[1].open();
        randomAccessFileHandlers[2] = new IndexFileHandler(index2FileName);
        randomAccessFileHandlers[2].open();

        Map<String, LinkedList<Posting>> index = new LinkedHashMap<>(1);
        Index indexItem1 = null;
        Index indexItem2 = null;
        while (true) {
            if(indexItem1 == null && randomAccessFileHandlers[1] != null) {
                indexItem1 = randomAccessFileHandlers[1].readOneRecord();
                if(indexItem1 == null) {
                    randomAccessFileHandlers[1].close();
                    randomAccessFileHandlers[1] = null;
                }
            }
            if(indexItem2 == null && randomAccessFileHandlers[2] != null) {
                indexItem2 = randomAccessFileHandlers[2].readOneRecord();
                if(indexItem2 == null) {
                    randomAccessFileHandlers[2].close();
                    randomAccessFileHandlers[2] = null;
                }
            }
            if(indexItem1 != null || indexItem2 != null) {
                int value;
                if(indexItem1 == null && !indexItem2.getTerm().isEmpty()) {
                    value = 1;
                }
                else if(indexItem2 == null && !indexItem1.getTerm().isEmpty()) {
                    value = -1;
                }
                else if(indexItem1 != null && indexItem2 != null) {
                    value = indexItem1.getTerm().compareTo(indexItem2.getTerm());
                }
                else {
                    break;
                }
                if (value == 0) {
                    LinkedList<Posting> postingFileItems = indexItem1.getPostings();
                    postingFileItems.addAll(indexItem2.getPostings());
                    System.out.println("Adding " + indexItem1.getTerm() + " with " + postingFileItems.size() + " items");
                    index.put(indexItem1.getTerm(), postingFileItems);
                    indexItem1 = indexItem2 = null;
                } else if (value < 0) {
                    System.out.println("Adding " + indexItem1.getTerm() + " with " + indexItem1.getPostings().size() + " items");
                    index.put(indexItem1.getTerm(), indexItem1.getPostings());
                    indexItem1 = null;
                } else {
                    System.out.println("Adding " + indexItem2.getTerm() + " with " + indexItem2.getPostings().size() + " items");
                    index.put(indexItem2.getTerm(), indexItem2.getPostings());
                    indexItem2 = null;
                }
            }
            if(randomAccessFileHandlers[1] == null && randomAccessFileHandlers[2] == null) break;
        }
        index = sortIndex(index);
        randomAccessFileHandlers[0].write(index);
    }
}
