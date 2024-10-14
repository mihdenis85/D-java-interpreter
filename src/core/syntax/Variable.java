package src.core.syntax;

import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.StatementElement;

public class Variable implements StatementElement {
    private final Keyword keyword;
    private final Identifier identifier;
    private final Expression expression;

    public Variable(Keyword keyword, Identifier identifier, Expression expression) {
        this.keyword = keyword;
        this.identifier = identifier;
        this.expression = expression;
    }

    public Keyword getKeyword() {
        return keyword;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Expression getExpression() { return expression; }

    public String outputConsole() {
        return "{\n\t'Keyword': { \n" +
        "\t\t'value': " + keyword.getValue() +
        "\n\t\t'span': " + keyword.getSpan().lineNum + ":" + keyword.getSpan().posBegin + "-" + keyword.getSpan().posEnd +
        "\n\t}, " +
        "\n\t'Identifier': { \n" +
        "\t\t'value': " + keyword.getValue() +
        "\n\t\t'span': " + keyword.getSpan().lineNum + ":" + keyword.getSpan().posBegin + "-" + keyword.getSpan().posEnd +
        "\n\t}\n}";
    }
}
