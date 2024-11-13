package src.core;

import org.w3c.dom.ls.LSOutput;
import src.core.expressionElements.*;
import src.core.literals.*;
import src.core.syntax.Expression;
import src.core.syntax.Identifier;
import src.core.syntax.Variable;
import src.core.syntax.interfaces.ExpressionElement;
import src.core.syntax.interfaces.StatementElement;
import src.core.syntax.statements.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Interpreter<V> {
    private ArrayList<StatementElement> tree;
    private Map<String, Object> variables;
    private String parent;

    public Interpreter(ArrayList<StatementElement> tree) {
        this.tree = tree;
        this.variables = new HashMap<>();
    }

    public void interpret() {
        parseBody(tree);
    }

    public void parseBody(ArrayList<StatementElement> body) {
        for (StatementElement element : body) {
            switch (element) {
                case PrintStatement printStatement:
                    this.parent = "print";
                    printInterpretation(printStatement.getExpressions());
                    break;
                case Variable variable:
                    this.parent = "variable";
                    Identifier identifier = variable.getIdentifier();
                    this.variables.put(identifier.getValue(), parseVariableExpression(variable.getExpression()));
                    break;
                case AssignmentStatement assignmentStatement:
                    this.parent = "assignment";
                    if (assignmentStatement.getIdentifier() instanceof Identifier id) {
                        this.variables.put(id.getValue(), parseVariableExpression(assignmentStatement.getExpression()));
                    }
                    break;
                case IfStatement ifStatement:
                    this.parent = "if";
                    Object ifRes = parseVariableExpression(ifStatement.getCondition());
                    if (Boolean.parseBoolean(ifRes.toString())) {
                        parseBody(ifStatement.getBody());
                    } else {
                        parseBody(ifStatement.getElseBody());
                    }
                    break;
                case WhileLoop whileLoop:
                    this.parent = "while";
                    Object whileRes = parseVariableExpression(whileLoop.getCondition());
                    while (Boolean.parseBoolean(whileRes.toString())) {
                        parseBody(whileLoop.getBody());
                        whileRes = parseVariableExpression(whileLoop.getCondition());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public String interpretExpression(ArrayList<ExpressionElement> elements) {
        StringBuilder str = new StringBuilder();

        for (ExpressionElement element : elements) {
            str.append(parseElement(element));
        }

        return str.toString();
    }

    public Object parseVariableExpression(Expression variable) {
        ArrayList<ExpressionElement> expression = variable.getExpressions();

        SHA sha = new SHA();
        String strExpression = interpretExpression(expression);
        String rpn = SHA.toRPN(strExpression);
        return sha.evaluate(rpn);
    }

    public void printInterpretation(ArrayList<Expression> expressions) {
        for (Expression expression : expressions) {
            System.out.println(parseVariableExpression(expression));
        }
    }

    public String parseElement(ExpressionElement element) {
        return switch (element) {
            case StringLiteral str -> "'" + str.value.substring(1, str.value.length() - 1) + "'";
            case IntegerLiteral integerLiteral -> integerLiteral.value;
            case BooleanLiteral bool -> bool.value;
            case RealLiteral realLiteral -> realLiteral.value;
            case Identifier id -> this.variables.get(id.getValue()).toString();
            case PlusSign ps -> ps.value;
            case UnaryNot not -> not.value;
            case MinusSign ms -> ms.value;
            case MultiplySign ms -> ms.value;
            case DivideSign dv -> dv.value;
            case LogicalAnd and -> and.value;
            case LogicalOr or -> or.value;
            case LogicalXor xor -> xor.value;
            case IsOperator isOperator -> isOperator.value;
            case LessEqualSign lessEqualSign -> lessEqualSign.value;
            case GreaterEqualSign greaterEqualSign -> greaterEqualSign.value;
            case LessSign lessSign -> lessSign.value;
            case GreaterSign greaterSign -> greaterSign.value;
            case EqualSign equalSign -> equalSign.value;
            case NotEqualSign notEqualSign -> notEqualSign.value;
            case IntegerType integer -> integer.value;
            case RealType realType -> realType.value;
            case StringType string -> string.value;
            case FunctionCall call -> {
                if (call.getIdentifier() instanceof Identifier id) {
                    Scanner reader = new Scanner(System.in);
                    switch (id.getValue()) {
                        case "readInt" -> {
                            int value = reader.nextInt();
                            yield String.valueOf(value);
                        }
                        case "readReal" -> {
                            float value = reader.nextFloat();
                            yield String.valueOf(value);
                        }
                        case "readString" -> {
                            yield reader.next();
                        }
                        default -> {
                            // TODO: Perform all other function calls
                        }
                    }
                }
                yield null;
            }
            default -> throw new IllegalStateException("Unexpected value: " + element);
        };

    }
}
