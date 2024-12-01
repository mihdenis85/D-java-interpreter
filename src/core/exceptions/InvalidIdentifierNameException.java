package src.core.exceptions;

import src.core.token.Span;

public class InvalidIdentifierNameException extends Exception {
    public InvalidIdentifierNameException(Span span) {
        super("InvalidIdentifierException raised: Invalid identifier name in line " + span.lineNum + ":" + span.posBegin + "-" + span.posEnd);
    }
}
