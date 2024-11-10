package src.core;

import src.core.exceptions.InvalidArgumentsCountException;
import src.core.exceptions.UndefinedFunctionException;
import src.core.exceptions.UndefinedVariableException;
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
    private ArrayList<StatementElement> tree;
    private final Map<String, Integer> definedVariables;
    private final ArrayList<String> usedVariables;
    private boolean isVariableChecking;
    private boolean isEmptyCasesChecking;
    private ArrayList<StatementElement> bodyToCheck;
    private ArrayList<StatementElement> parentBody;
    private ExpressionElement currentExpression;
    private Variable functionVariable;
    private boolean isTuple;
    private boolean isDeleted;

    public SemanticAnalyzer(ArrayList<StatementElement> tree) {
        this.tree = tree;
        this.definedVariables = new HashMap<>();
        this.usedVariables = new ArrayList<>();
        this.isVariableChecking = false;
        this.isEmptyCasesChecking = false;
        this.isDeleted = false;
        this.isTuple = false;
    }

    public Program analyze() {
        for (SyntaxElement syntaxElement : tree) {
            parseStatement(syntaxElement);
        }

        this.isVariableChecking = true;
        int counter = 0;

        while (counter < tree.size()) {
            this.bodyToCheck = tree;
            this.isDeleted = false;
            parseStatement(tree.get(counter));
            if (!this.isDeleted) {
                counter++;
            }
        }

        this.isEmptyCasesChecking = true;

        counter = 0;
        while (counter < tree.size()) {
            this.parentBody = tree;
            this.isDeleted = false;
            parseStatement(tree.get(counter));
            if (!this.isDeleted) {
                counter++;
            }
        }


        return new Program(tree);
    }

    public void parseBody(ArrayList<StatementElement> body) {
        this.bodyToCheck = body;
        int counter = 0;
        while (counter < body.size()) {
            this.parentBody = body;
            this.isDeleted = false;
            parseStatement(body.get(counter));
            if (!this.isDeleted) {
                counter++;
            }
        }
    }

    public void parseStatement(SyntaxElement syntaxElement) {
        if (syntaxElement instanceof Variable variable) {
            String identifier = variable.getIdentifier().getValue();
            if (!usedVariables.contains(identifier) && isVariableChecking) {
                if (this.bodyToCheck.contains(variable)) {
                    this.bodyToCheck.remove(variable);
                    this.isDeleted = true;
                    this.definedVariables.remove(identifier);
                } else if (parentBody.contains(variable)) {
                    this.parentBody.remove(variable);
                    this.isDeleted = true;
                    this.definedVariables.remove(identifier);
                }
            }

            definedVariables.put(identifier, 0);
            Expression expression = variable.getExpression();

            if (expression.getExpressions().getFirst() instanceof FunctionStatement functionStatement) {
                this.functionVariable = variable;
                definedVariables.put(identifier, functionStatement.getArguments().size());
            }

            if (expression.getExpressions().getFirst() instanceof FunctionCall functionCall) {
                if (functionCall.getIdentifier() instanceof Identifier id) {
                    if (!Objects.equals(id.getValue(), "readInt") && !Objects.equals(id.getValue(), "readString") && !Objects.equals(id.getValue(), "readReal")) {
                        if (!definedVariables.containsKey(id.getValue()) && !(currentExpression instanceof TupleLiteral)) {
                            throw new UndefinedFunctionException(id.getValue(), id.getSpan());
                        }
                    }
                }
            }

            if (expression.getExpressions().getFirst() instanceof DotNotation dotNotation) {
                if (dotNotation.getIdentifier() instanceof Identifier id) {
                    if (!Objects.equals(id.getValue(), "readInt") && !Objects.equals(id.getValue(), "readString") && !Objects.equals(id.getValue(), "readReal")) {
                        if (!definedVariables.containsKey(id.getValue()) && !(currentExpression instanceof TupleLiteral)) {
                            throw new UndefinedVariableException(id.getValue(), id.getSpan());
                        }
                    }
                }
            }

            analyzeExpression(expression.getExpressions());
        }

        if (syntaxElement instanceof IfStatement ifStatement) {
            Expression condition = ifStatement.getCondition();

            analyzeExpression(condition.getExpressions());

            ArrayList<StatementElement> body = ifStatement.getBody();

            parseBody(ifStatement.getBody());

            bodyToCheck = ifStatement.getElseBody();
            if (body.isEmpty() && bodyToCheck.isEmpty() && isEmptyCasesChecking) {
                parentBody.remove(ifStatement);
                return;
            }

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

            bodyToCheck = whileLoop.getBody();

            if (bodyToCheck.isEmpty() && isEmptyCasesChecking) {
                this.parentBody.remove(whileLoop);
            }
        }

        if (syntaxElement instanceof ForLoop forLoop) {
            this.definedVariables.put(forLoop.getIdentifier().getValue(), 0);

            ArrayList<ExpressionElement> expressions = new ArrayList<>();
            expressions.add(forLoop.getExpression());

            analyzeExpression(expressions);

            parseBody(forLoop.getBody());

            bodyToCheck = forLoop.getBody();

            if (bodyToCheck.isEmpty() && isEmptyCasesChecking) {
                this.parentBody.remove(forLoop);
            }
        }

        if (syntaxElement instanceof FunctionStatement functionStatement) {
            ArrayList<Identifier> arguments = functionStatement.getArguments();
            for (Identifier argument : arguments) {
                definedVariables.put(argument.getValue(), 0);
            }

            parseBody(functionStatement.getBody());

            bodyToCheck = functionStatement.getBody();

            if (bodyToCheck.isEmpty() && isEmptyCasesChecking) {
                this.parentBody.remove(functionVariable);
                this.isDeleted = true;
                this.definedVariables.remove(functionVariable.getIdentifier().getValue());
            }
        }

        if (syntaxElement instanceof FunctionCall functionCall) {
            AssignmentIdentifier identifier = functionCall.getIdentifier();
            ArrayList<ExpressionElement> arguments = functionCall.getArguments();
            if (identifier instanceof Identifier id) {
                if (!Objects.equals(id.getValue(), "readInt") && !Objects.equals(id.getValue(), "readString") && !Objects.equals(id.getValue(), "readReal")) {
                    if (definedVariables.get(id.getValue()) == null && isVariableChecking) {
                        this.parentBody.remove(functionCall);
                        return;
                    }
                }

                if (!Objects.equals(id.getValue(), "readInt") && !Objects.equals(id.getValue(), "readString") && !Objects.equals(id.getValue(), "readReal")) {
                    if (definedVariables.get(id.getValue()) == null) {
                        throw new UndefinedFunctionException(id.getValue(), id.getSpan());
                    } else {
                        usedVariables.add(id.getValue());
                    }

                    if (definedVariables.get(id.getValue()) != arguments.size()) {
                        throw new InvalidArgumentsCountException(id.getValue(), id.getSpan());
                    }
                }
            }

            analyzeExpression(arguments);
        }

        if (syntaxElement instanceof DotNotation dotNotation) {
            AssignmentIdentifier identifier = dotNotation.getIdentifier();
            if (identifier instanceof Identifier id) {
                if (definedVariables.get(id.getValue()) == null && isVariableChecking) {
                    this.parentBody.remove(dotNotation);
                    return;
                }

                if (definedVariables.get(id.getValue()) == null) {
                    throw new UndefinedVariableException(id.getValue(), id.getSpan());
                } else {
                    usedVariables.add(id.getValue());
                }
            }
        }

        if (syntaxElement instanceof AssignmentStatement assignmentStatement) {
            AssignmentIdentifier identifier = assignmentStatement.getIdentifier();
            if (identifier instanceof Identifier id) {
                if (definedVariables.get(id.getValue()) == null) {
                    throw new UndefinedVariableException(id.getValue(), id.getSpan());
                }
            }

            Expression expression = assignmentStatement.getExpression();

            analyzeExpression(expression.getExpressions());
        }
    }

    public void analyzeExpression(ArrayList<ExpressionElement> expressions) {
        for (ExpressionElement expressionElement : expressions) {
            currentExpression = expressionElement;
            if (expressionElement instanceof Identifier identifier) {
                if (definedVariables.get(identifier.getValue()) == null && !isTuple) {
                    throw new UndefinedVariableException(identifier.getValue(), identifier.getSpan());
                } else {
                    usedVariables.add(identifier.getValue());
                }
            };

            if (expressionElement instanceof Expression expression) {
                analyzeExpression(expression.getExpressions());
            }

            if (expressionElement instanceof TupleLiteral tupleLiteral) {
                this.isTuple = true;
                for (TupleElement tupleElement : tupleLiteral.getElements()) {
                    analyzeExpression(tupleElement.getExpression().getExpressions());
                }
                this.isTuple = false;
            }

            if (expressionElement instanceof ArrayLiteral arrayLiteral) {
                for (Expression expression : arrayLiteral.getElements()) {
                    analyzeExpression(expression.getExpressions());
                }
            }

            if (expressionElement instanceof FunctionStatement function) {
                parseStatement(function);
            }

            if (expressionElement instanceof FunctionCall functionCall) {
                parseStatement(functionCall);
            }

            if (expressionElement instanceof DotNotation dotNotation) {
                parseStatement(dotNotation);
            }
        }
    }
}
