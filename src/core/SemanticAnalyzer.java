package src.core;

import src.core.expressionElements.TupleElement;
import src.core.literals.ArrayLiteral;
import src.core.literals.TupleLiteral;
import src.core.syntax.Expression;
import src.core.syntax.Identifier;
import src.core.syntax.Program;
import src.core.syntax.Variable;
import src.core.syntax.interfaces.AssignmentIdentifier;
import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.StatementElement;
import src.core.syntax.interfaces.SyntaxElement;
import src.core.syntax.statements.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SemanticAnalyzer {
    private final ArrayList<SyntaxElement> tree;
    private final Map<String, Integer> definedVariables;

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
            definedVariables.put(identifier, 0);
            Expression expression = variable.getExpression();

            if (expression.getExpressions().getFirst() instanceof FunctionStatement functionStatement) {
                definedVariables.put(identifier, functionStatement.getArguments().size());
            }

            if (expression.getExpressions().getFirst() instanceof FunctionCall functionCall) {
                if (functionCall.getIdentifier() instanceof Identifier id) {
                    if (!Objects.equals(id.getValue(), "readInt") && !Objects.equals(id.getValue(), "readString") && !Objects.equals(id.getValue(), "readReal")) {
                        if (!definedVariables.containsKey(id.getValue())) {
                            throw new Error("Function '" + id.getValue() + "' is not defined");
                        }
                    }
                }
            }

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
            ArrayList<Identifier> arguments = functionStatement.getArguments();
            for (Identifier argument : arguments) {
                definedVariables.put(argument.getValue(), 0);
            }

            parseBody(functionStatement.getBody());
        }

        if (syntaxElement instanceof FunctionCall functionCall) {
            System.out.println(definedVariables);
            AssignmentIdentifier identifier = functionCall.getIdentifier();
            ArrayList<ExpressionElement> arguments = functionCall.getArguments();
            if (identifier instanceof Identifier id) {
                if (definedVariables.get(id.getValue()) == null) {
                    throw new Error("Variable '" + id.getValue() + "' is not defined");
                }

                if (definedVariables.get(id.getValue()) != arguments.size()) {
                    throw new Error("Function '" + id.getValue() + "' has different number of arguments");
                }
            }

            analyzeExpression(arguments);
        }

        if (syntaxElement instanceof AssignmentStatement assignmentStatement) {
            Expression expression = assignmentStatement.getExpression();

            analyzeExpression(expression.getExpressions());
        }
    }

    public void analyzeExpression(ArrayList<ExpressionElement> expressions) {
        for (ExpressionElement expressionElement : expressions) {
            if (expressionElement instanceof Identifier identifier) {
                if (definedVariables.get(identifier.getValue()) == null) {
                    throw new Error("Variable '" + identifier.getValue() + "' is not defined");
                }
            };

            if (expressionElement instanceof Expression expression) {
                analyzeExpression(expression.getExpressions());
            }

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
