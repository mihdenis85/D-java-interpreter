package src.core.token;

public class Span {
    public long lineNum;
    public int posBegin, posEnd;

    public Span(long lineNum, int posBegin, int posEnd) {
        this.lineNum = lineNum;
        this.posBegin = posBegin;
        this.posEnd = posEnd;
    }

    @Override
    public String toString() {
        return lineNum +
                ":" +
                posBegin +
                "-" +
                posEnd;
    }
}