package src.core.exceptions;

import src.core.Span;
import src.core.enums.Code;

public class UnexpectedTokenException extends Exception {
    public UnexpectedTokenException(Span span, Code givenToken, Code expectedToken) {
        super("UnexpectedTokenException raised: " + "expected token - " + expectedToken + ", given token - " + givenToken + " at line " + span.lineNum + ":" + span.posBegin + "-" + span.posEnd);
    }
}
