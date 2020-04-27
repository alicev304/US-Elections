package utils.io;

import core.indexer.Index;
import core.indexer.Posting;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class IndexFileHandler {
    private String filePath;

    private RandomAccessFile file;

    public IndexFileHandler(String filePath) {
        this.filePath = filePath;
    }

    public Map<String, LinkedList<Posting>> read() {
        if(filePath.equals("")) {
            return null;
        }
        return readIndex();
    }

    public boolean open() {
        try {
            file = new RandomAccessFile(filePath, "r");
            return true;
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean close() {
        try {
            file.close();
            return true;
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private Map<String, LinkedList<Posting>> readIndex() {
        Map<String, LinkedList<Posting>> content = new HashMap<>();
        try {
            file = new RandomAccessFile(filePath, "r");
            int byteCounter = 0;
            while(byteCounter <= file.length()) {
                Index indexItem = readOneRecord(file);
                if(indexItem != null) {
                    content.put(indexItem.getTerm(), indexItem.getPostings());
                    byteCounter += indexItem.getSize();
                }
            }
            file.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public Map<String, Short> readIndexHeaders() {
        Map<String, Short> contentHeaders = new HashMap<>();
        try {
            file = new RandomAccessFile(filePath, "r");
            int byteCounter = 0;
            while(byteCounter < file.length()) {
                Index indexItem = readOneRecordHeader(file);
                if(indexItem != null) {
                    contentHeaders.put(indexItem.getTerm(), indexItem.getDf());
                    byteCounter += indexItem.getSize();
                }
            }
            file.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return contentHeaders;
    }

    public Index readOneRecord() {
        try {
            if (file != null) {
                return readOneRecord(file);
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return new Index();
    }

    private Index readOneRecord(RandomAccessFile file) throws IOException {
        if(file.getFilePointer() < file.length() - Short.BYTES) {
            byte termByte;
            StringBuilder term = new StringBuilder();
            while ((termByte = file.readByte()) != ' ') {
                term.append((char)termByte);
            }
            short df = file.readShort();
            Index indexItem = new Index(term.toString(), df);
            indexItem.incrementSize(1);
            for (int counter = 0; counter < df; counter++) {
                indexItem.addPostingEntry(file.readInt(), file.readInt());
            }
            return indexItem;
        }
        return null;
    }

    private Index readOneRecordHeader(RandomAccessFile file) throws IOException {
        if(file.getFilePointer() < file.length()) {
            byte termByte;
            StringBuilder term = new StringBuilder();
            while ((termByte = file.readByte()) != ' ') {
                term.append((char)termByte);
            }
            short df = file.readShort();
            Index indexItem = new Index(term.toString(), df);
            indexItem.incrementSize(1);
            int delta = Integer.BYTES * 2 * df;
            indexItem.incrementSize(delta);
            file.seek(file.getFilePointer() + delta);
            return indexItem;
        }
        return null;
    }

    public void write(Map<String, LinkedList<Posting>> index) {
        try {
            file = new RandomAccessFile(filePath, "rw");
            if (index != null && index.size() > 0) {
                index.forEach(this::write);
            }
            file.close();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public boolean write(String key, LinkedList<Posting> value) {
        writeTermAndPostingToRandomAccessFile(file, key, value);
        return false;
    }

    private void writeTermAndPostingToRandomAccessFile(RandomAccessFile randomAccessFile, String term, LinkedList<Posting> postingList) {
        try {
            randomAccessFile.write(term.getBytes());
            randomAccessFile.write(' ');
            randomAccessFile.writeShort(postingList.size());
            for(Posting postingFileItem: postingList) {
                randomAccessFile.writeInt(postingFileItem.getDocId());
                randomAccessFile.writeInt(postingFileItem.getTermFrequency());
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
