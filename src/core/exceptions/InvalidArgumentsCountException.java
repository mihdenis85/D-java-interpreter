package src.core.exceptions;

import src.core.Span;

public class InvalidArgumentsCountException extends RuntimeException {
    public InvalidArgumentsCountException(String functionName, Span span) {
        super("InvalidArgumentsCountException raised: Incorrect number of arguments in function " + functionName + ". Span: " + span.lineNum + ":" + span.posBegin + "-" + span.posEnd);
    }
}
