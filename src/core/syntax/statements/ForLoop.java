package src.core.syntax.statements;

import src.core.syntax.Identifier;
import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.StatementElement;
import src.core.syntax.interfaces.SyntaxElement;

import java.util.ArrayList;

public class ForLoop implements StatementElement {
    private final Identifier identifier;
    private final ExpressionElement expression;
    private final ArrayList<SyntaxElement> body;

    public ForLoop(Identifier identifier, ExpressionElement expression, ArrayList<SyntaxElement> body) {
        this.identifier = identifier;
        this.expression = expression;
        this.body = body;
    }

    public Identifier getIdentifier() {
        return identifier;
    }
    public ExpressionElement getExpression() {
        return expression;
    }
    public ArrayList<SyntaxElement> getBody() {
        return body;
    }
}
