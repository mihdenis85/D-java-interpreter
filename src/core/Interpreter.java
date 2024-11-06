package src.core;

import src.core.literals.*;
import src.core.syntax.Expression;
import src.core.syntax.Identifier;
import src.core.syntax.Variable;
import src.core.syntax.interfaces.AssignmentIdentifier;
import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.StatementElement;
import src.core.syntax.statements.AssignmentStatement;
import src.core.syntax.statements.FunctionCall;
import src.core.syntax.statements.PrintStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Interpreter<V> {
    private ArrayList<StatementElement> tree;
    private Map<String, V> variables;

    public Interpreter(ArrayList<StatementElement> tree) {
        this.tree = tree;
        this.variables = new HashMap<>();
    }

    public void interpret() {
        for (StatementElement element : tree) {
            if (element instanceof PrintStatement printStatement) {
                printInterpretation(printStatement.getExpressions());
            }

            if (element instanceof Variable variable) {
                Identifier identifier = variable.getIdentifier();
                this.variables.put(identifier.getValue(), parseVariableExpression(variable.getExpression()));
            }

            if (element instanceof AssignmentStatement assignmentStatement) {
                if (assignmentStatement.getIdentifier() instanceof Identifier id) {
                    this.variables.put(id.getValue(), parseVariableExpression(assignmentStatement.getExpression()));
                }
            }
        }

        System.out.println(this.variables);
    }

    public V parseVariableExpression(Expression variable) {
        ArrayList<ExpressionElement> expression = variable.getExpressions();
        for (ExpressionElement expressionElement : expression) {
            return (V) parseElement(expressionElement);
        }

        return null;
    }

    public void printInterpretation(ArrayList<Expression> expressions) {
        for (Expression expression : expressions) {
            ArrayList<ExpressionElement> elements = expression.getExpressions();
            for (ExpressionElement element : elements) {
                System.out.println(parseElement(element));
            }
        }
    }

    public String parseElement(ExpressionElement element) {
        if (element instanceof StringLiteral str) {
            return str.value.substring(1, str.value.length() - 1);
        }

        if (element instanceof IntegerLiteral num) {
            return num.value;
        }

        if (element instanceof RealLiteral num) {
            return num.value;
        }

        if (element instanceof BooleanLiteral bool) {
            return bool.value;
        }

        if (element instanceof Identifier id) {
            return (String) this.variables.get(id.getValue());
        }

        if (element instanceof FunctionCall call) {
            if (call.getIdentifier() instanceof Identifier id) {
                Scanner reader = new Scanner(System.in);
                switch (id.getValue()) {
                    case "readInt" -> {
                        int value = reader.nextInt();
                        return String.valueOf(value);
                    }
                    case "readReal" -> {
                        float value = reader.nextFloat();
                        return String.valueOf(value);
                    }
                    case "readString" -> {
                        return reader.next();
                    }
                    default -> {
                        // TODO: Perform all other function calls
                    }
                }
            }
        }

        return null;
    }
}
