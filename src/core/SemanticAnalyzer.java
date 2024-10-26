package src.core;

import src.core.expressionElements.TupleElement;
import src.core.literals.ArrayLiteral;
import src.core.literals.TupleLiteral;
import src.core.syntax.Expression;
import src.core.syntax.Identifier;
import src.core.syntax.Program;
import src.core.syntax.Variable;
import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.StatementElement;
import src.core.syntax.interfaces.SyntaxElement;
import src.core.syntax.statements.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SemanticAnalyzer {
    private final ArrayList<SyntaxElement> tree;
    private final Map<String, Boolean> definedVariables;

    public SemanticAnalyzer(ArrayList<SyntaxElement> tree) {
        this.tree = tree;
        this.definedVariables = new HashMap<>();
    }

    public Program analyze() {
        for (SyntaxElement syntaxElement : tree) {
            parseStatement(syntaxElement);
        }

        return new Program(tree);
    }

    public void parseBody(ArrayList<StatementElement> body) {
        for (SyntaxElement syntaxElement : body) {
            parseStatement(syntaxElement);
        }
    }

    public void parseStatement(SyntaxElement syntaxElement) {
        if (syntaxElement instanceof Variable variable) {
            String identifier = variable.getIdentifier().getValue();
            definedVariables.put(identifier, true);
            Expression expression = variable.getExpression();

            analyzeExpression(expression.getExpressions());
        }

        if (syntaxElement instanceof IfStatement ifStatement) {
            Expression condition = ifStatement.getCondition();

            analyzeExpression(condition.getExpressions());

            parseBody(ifStatement.getBody());

            parseBody(ifStatement.getElseBody());
        }

        if (syntaxElement instanceof PrintStatement printStatement) {
            for (Expression expression : printStatement.getExpressions()) {
                analyzeExpression(expression.getExpressions());
            }
        }

        if (syntaxElement instanceof ReturnStatement returnStatement) {
            Expression expression = returnStatement.getExpression();

            analyzeExpression(expression.getExpressions());
        }

        if (syntaxElement instanceof WhileLoop whileLoop) {
            Expression condition = whileLoop.getCondition();

            analyzeExpression(condition.getExpressions());

            parseBody(whileLoop.getBody());
        }

        if (syntaxElement instanceof ForLoop forLoop) {
            ArrayList<ExpressionElement> expressions = new ArrayList<>();
            expressions.add(forLoop.getExpression());

            analyzeExpression(expressions);

            parseBody(forLoop.getBody());
        }

        if (syntaxElement instanceof FunctionStatement functionStatement) {
            parseBody(functionStatement.getBody());
        }

        if (syntaxElement instanceof AssignmentStatement assignmentStatement) {
            Expression expression = assignmentStatement.getExpression();

            analyzeExpression(expression.getExpressions());
        }
    }

    public void analyzeExpression(ArrayList<ExpressionElement> expressions) {
        for (ExpressionElement expressionElement : expressions) {
            System.out.println(expressionElement);
            if (expressionElement instanceof Identifier identifier) {
                if (definedVariables.get(identifier.getValue()) == null) {
                    throw new Error("Variable '" + identifier.getValue() + "' is not defined");
                }
            };

            if (expressionElement instanceof TupleLiteral tupleLiteral) {
                for (TupleElement tupleElement : tupleLiteral.getElements()) {
                    analyzeExpression(tupleElement.getExpression().getExpressions());
                }
            }

            if (expressionElement instanceof ArrayLiteral arrayLiteral) {
                for (Expression expression : arrayLiteral.getElements()) {
                    analyzeExpression(expression.getExpressions());
                }
            }

            if (expressionElement instanceof FunctionStatement function) {
                parseStatement(function);
            }
        }
    }
}
