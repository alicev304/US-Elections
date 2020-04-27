package core.indexer;

import java.util.LinkedList;

public class Index {
    private String term;

    private short df;

    private LinkedList<Posting> postings;

    private int size;

    public Index() {
        this.term = "";
        postings = null;
        postings = new LinkedList<>();
    }

    public Index(String term, short df) {
        this.term = term;
        this.df = df;
        this.size = term.length() + Short.BYTES;
    }

    public String getTerm() {
        return term;
    }

    public short getDf() {
        return df;
    }

    public int getSize() {
        return size;
    }

    public LinkedList<Posting> getPostings() {
        return postings;
    }

    public void setPostings(LinkedList<Posting> postings) {
        this.postings = postings;
    }

    public void addPostingEntry(int docId, int frequency) {
        if(postings == null) {
            postings = new LinkedList<>();
        }
        postings.add(new Posting(docId, frequency));
        size += (Integer.BYTES * 2);
    }

    public void incrementSize(int delta) {
        this.size += delta;
    }
}
