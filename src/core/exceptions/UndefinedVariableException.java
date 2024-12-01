package src.core.exceptions;

import src.core.token.Span;

public class UndefinedVariableException extends RuntimeException {
    public UndefinedVariableException(String variableName, Span span) {
        super("UndefinedVariableException raised: Variable " + variableName + " is not defined. Span: " + span.lineNum + ":" + span.posBegin + "-" + span.posEnd);
    }
}
