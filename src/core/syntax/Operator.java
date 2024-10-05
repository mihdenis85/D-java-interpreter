package src.core.syntax;

public class Operator {
    private final Keyword keyword;
    private final Identifier identifier;
    private final Expression expressions;

    public Operator(Keyword keyword, Identifier identifier, Expression expressions) {
        this.keyword = keyword;
        this.identifier = identifier;
        this.expressions = expressions;
    }

    public Keyword getKeyword() {
        return keyword;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Expression getExpressions() {
        return expressions;
    }
}
