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

import java.util.*;

public class Interpreter<V> {
    private ArrayList<StatementElement> tree;
    private Map<String, Object> variables;
    private String lastVariableType;
    private Map<String, Integer> currIndex;

    public Interpreter(ArrayList<StatementElement> tree) {
        this.tree = tree;
        this.variables = new HashMap<>();
        this.currIndex = new HashMap<>();
    }

    public void interpret() {
        parseBody(tree);
    }

    public void parseBody(ArrayList<StatementElement> body) {
        for (StatementElement element : body) {
            switch (element) {
                case PrintStatement printStatement:
                    printInterpretation(printStatement.getExpressions());
                    break;
                case Variable variable:
                    Identifier identifier = variable.getIdentifier();
                    this.variables.put(identifier.getValue(), parseVariableExpression(variable.getExpression()));
                    break;
                case AssignmentStatement assignmentStatement:
                    if (assignmentStatement.getIdentifier() instanceof Identifier id) {
                        this.variables.put(id.getValue(), parseVariableExpression(assignmentStatement.getExpression()));
                    }
                    break;
                case IfStatement ifStatement:
                    Object ifRes = parseVariableExpression(ifStatement.getCondition());
                    if (Boolean.parseBoolean(ifRes.toString())) {
                        parseBody(ifStatement.getBody());
                    } else {
                        parseBody(ifStatement.getElseBody());
                    }
                    break;
                case WhileLoop whileLoop:
                    Object whileRes = parseVariableExpression(whileLoop.getCondition());
                    while (Boolean.parseBoolean(whileRes.toString())) {
                        parseBody(whileLoop.getBody());
                        whileRes = parseVariableExpression(whileLoop.getCondition());
                    }
                    break;
                case ForLoop forLoop:
                    String id = forLoop.getIdentifier().getValue();
                    String array = forLoop.getExpression().getValue();
                    this.currIndex.put(id, 0);
                    this.variables.put(id, getValueFromArray(0, this.variables.get(array)));

                    while (this.currIndex.get(id) < getArrayLength(this.variables.get(array))) {
                        this.variables.put(id, getValueFromArray(this.currIndex.get(id), this.variables.get(array)));
                        parseBody(forLoop.getBody());
                        this.currIndex.put(id, this.currIndex.get(id) + 1);
                    }
                default:
                    break;
            }
        }
    }

    public int getArrayLength(Object array) {
        String[] obj = array.toString().substring(1, array.toString().length() - 1).split(",");
        ArrayList<Object> newArray = new ArrayList<>(Arrays.asList(obj));

        return newArray.size();
    }

    public Object getValueFromArray(int index, Object array) {
        String[] obj = array.toString().substring(1, array.toString().length() - 1).split(",");
        ArrayList<Object> newArray = new ArrayList<>(Arrays.asList(obj));

        return newArray.get(index);
    }

    public Object interpretExpression(ArrayList<ExpressionElement> elements) {
        StringBuilder str = new StringBuilder();
        ArrayList<String> result = new ArrayList<>();

        for (ExpressionElement element : elements) {
            if (element instanceof ArrayIndex) {
                return parseElement(element);
            }

            if (element instanceof ArrayLiteral) {
                this.lastVariableType = "array";
                String array = parseElement(element);
                String[] newArray = array.substring(1, array.length() - 1).split(",");
                for (String elem : newArray) {
                    SHA sha = new SHA();
                    result.add(sha.evaluate(SHA.toRPN(elem)).toString());
                }

                return result;
            }
            str.append(parseElement(element));
        }

        return str.toString();
    }

    public Object parseVariableExpression(Expression variable) {
        ArrayList<ExpressionElement> expression = variable.getExpressions();

        SHA sha = new SHA();
        Object strExpression = interpretExpression(expression);
        if (this.lastVariableType != null) {
            this.lastVariableType = null;
            return strExpression;
        }

        String rpn = SHA.toRPN(strExpression.toString());
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
            case EmptyLiteral emptyLiteral -> "'" + emptyLiteral.getValue() + "'";
            case PlusSign ps -> ps.value;
            case UnaryNot not -> not.value;
            case UnaryPlus up -> up.value;
            case UnaryMinus um -> um.value;
            case MinusSign ms -> ms.value;
            case MultiplySign ms -> ms.value;
            case DivideSign dv -> dv.value;
            case LogicalAnd and -> and.value;
            case LogicalOr or -> or.value;
            case LogicalXor xor -> xor.value;
            case FunctionStatement fs -> "func";
            case ArrayIndex ai -> {
                String id = ai.identifier.getValue();
                int index = Integer.parseInt(interpretExpression(ai.expression.getExpressions()).toString(), 10);
                yield getValueFromArray(index - 1, this.variables.get(id)).toString();
            }
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
            case ArrayLiteral al -> {
                StringBuilder array = new StringBuilder();
                array.append("[");
                ArrayList<Expression> elements = al.getElements();
                for (int i = 0; i < elements.size(); i++) {
                    array.append(interpretExpression(elements.get(i).getExpressions()));
                    if (i != elements.size() - 1) {
                        array.append(",");
                    }
                }
                array.append("]");
                yield array.toString();
            }
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
