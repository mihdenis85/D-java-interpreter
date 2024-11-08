package src.core;

import src.core.expressionElements.*;
import src.core.literals.*;
import src.core.syntax.Expression;
import src.core.syntax.Identifier;
import src.core.syntax.Variable;
import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.StatementElement;
import src.core.syntax.statements.AssignmentStatement;
import src.core.syntax.statements.FunctionCall;
import src.core.syntax.statements.PrintStatement;

import java.sql.SQLOutput;
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

    public boolean evaluateExpression(ExpressionElement arg1, ExpressionElement arg2, ExpressionElement operator) {
        return switch (operator) {
            case EqualSign equals -> equals.evaluate(arg1, arg2);
            case NotEqualSign notEquals -> notEquals.evaluate(arg1, arg2);
            case LessEqualSign lessEqualSign -> lessEqualSign.evaluate(arg1, arg2);
            case GreaterEqualSign greaterEqualSign -> greaterEqualSign.evaluate(arg1, arg2);
            case LessSign lessSign -> lessSign.evaluate(arg1, arg2);
            case GreaterSign greaterSign -> greaterSign.evaluate(arg1, arg2);
            case LogicalAnd and -> and.evaluate(arg1, arg2);
            case LogicalOr or -> or.evaluate(arg1, arg2);
            case LogicalXor xor -> xor.evaluate(arg1, arg2);
            default -> throw new IllegalStateException("Unexpected value: " + operator);
        };
    }

    public V parseVariableExpression(Expression variable) {
        ArrayList<ExpressionElement> expression = variable.getExpressions();
        SHA sha = new SHA();
        StringBuilder str = new StringBuilder();
        boolean result = false;

        for (int i = 1; i < expression.size() - 1; i++) {
            System.out.println(evaluateExpression(expression.get(i - 1), expression.get(i + 1), expression.get(i)));
            if ((i + 1) == expression.size()) {
                break;
            }
        }

        System.out.println(result);
//        System.out.println(sha.analyze(String.valueOf(str)));

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
            return num.value + " ";
        }

        if (element instanceof RealLiteral num) {
            return num.value + " ";
        }

        if (element instanceof BooleanLiteral bool) {
            return bool.value + " ";
        }

        if (element instanceof Identifier id) {
            return (String) this.variables.get(id.getValue()) + " ";
        }

        if (element instanceof PlusSign) {
            return "+ ";
        }

        if (element instanceof MinusSign) {
            return "- ";
        }

        if (element instanceof MultiplySign) {
            return "* ";
        }

        if (element instanceof DivideSign) {
            return "/ ";
        }

        if (element instanceof LogicalAnd) {
            return "and ";
        }

        if (element instanceof LogicalOr) {
            return "or ";
        }

        if (element instanceof LogicalXor) {
            return "xor ";
        }

        if (element instanceof IsOperator) {
            return "is ";
        }

        if (element instanceof LessEqualSign) {
            return "<= ";
        }

        if (element instanceof GreaterEqualSign) {
            return ">= ";
        }

        if (element instanceof EqualSign) {
            return "= ";
        }

        if (element instanceof NotEqualSign) {
            return "/= ";
        }

        if (element instanceof LessSign) {
            return "< ";
        }

        if (element instanceof GreaterSign) {
            return "> ";
        }

        if (element instanceof IntegerType) {
            return "int ";
        }

        if (element instanceof RealType) {
            return "real ";
        }

        if (element instanceof StringType) {
            return "string ";
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
