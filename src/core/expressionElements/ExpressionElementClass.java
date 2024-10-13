package src.core.expressionElements;
import src.core.Span;

public class ExpressionElementClass {
    public Span span;
    public String value;

    public ExpressionElementClass(String value, Span span) {
        this.value = value;
        this.span = span;
    }
}
