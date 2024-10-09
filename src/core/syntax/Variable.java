package src.core.syntax;

import src.core.syntax.interfaces.SyntaxElement;

public class Variable implements SyntaxElement {
    private final Keyword keyword;
    private final Identifier identifier;

    public Variable(Keyword keyword, Identifier identifier) {
        this.keyword = keyword;
        this.identifier = identifier;
    }

    public Keyword getKeyword() {
        return keyword;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

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
