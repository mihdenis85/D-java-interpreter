package src.core.exceptions;

import src.core.Span;

public class UndefinedFunctionException extends RuntimeException {
  public UndefinedFunctionException(String variableName, Span span) {
    super("UndefinedFunctionException raised: Function " + variableName + " is not defined. Span: " + span.lineNum + ":" + span.posBegin + "-" + span.posEnd);
  }
}
