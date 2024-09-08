package src.core;

public class Span {
    private final long lineNum;
    private final int posBegin, posEnd;

    public Span(long lineNum, int posBegin, int posEnd) {
        this.lineNum = lineNum;
        this.posBegin = posBegin;
        this.posEnd = posEnd;
    }

    public long getLineNum() {
        return this.lineNum;
    }

    public int getPosBegin() {
        return this.posBegin;
    }

    public int getPosEnd() {
        return this.posEnd;
    }
}