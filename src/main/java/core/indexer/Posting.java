package core.indexer;

import java.util.BitSet;

public class Posting {
    private Integer docId;

    private int termFrequency;

    private BitSet code;

    public Posting() {
        this.docId = -1;
        this.termFrequency = 0;
    }

    public Posting(int docId) {
        this.docId = docId;
        this.termFrequency = 1;
    }

    Posting(int docId, int termFrequency) {
        this.docId = docId;
        this.termFrequency = termFrequency;
    }

    public Integer getDocId() {
        return this.docId;
    }

    public int getTermFrequency() {
        return this.termFrequency;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public void setTermFrequency(int termFrequency) {
        this.termFrequency = termFrequency;
    }

    public void incrementTermFrequency() {
        this.termFrequency++;
    }

    public void computeGap(int previousDocId) {
        this.docId -= previousDocId;
    }

    public void setCode(boolean[] booleanCode) {
        code = new BitSet(booleanCode.length);
        for(int counter = 0; counter < booleanCode.length; counter++) {
            code.set(counter, booleanCode[counter]);
        }
    }

    public BitSet getCode() {
        return this.code;
    }

    @Override
    public int hashCode() {
        return this.docId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }

        if(!(obj instanceof Posting)) {
            return false;
        }

        Posting item = (Posting) obj;

        return this.docId.equals(item.docId);
    }
}
