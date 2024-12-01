package src.core.interpreter;

import src.core.utils.SHA;
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
    private Map<String, Integer> currIndex;
    private Map<String, FunctionStatement> functions;
    private ArrayList<FunctionStatement> functionList;
    private Object returnValue;
    private String lastVariableType;

    public Interpreter(ArrayList<StatementElement> tree) {
        this.tree = tree;
        this.returnValue = null;
        this.variables = new HashMap<>();
        this.currIndex = new HashMap<>();
        this.functions = new HashMap<>();
        this.functionList = new ArrayList<>();
    }

    public void interpret() {
        parseBody(tree);
    }

    public void parseBody(ArrayList<StatementElement> body) {
        parseBody(body, null);
    }

    public void parseBody(ArrayList<StatementElement> body, Map<String, Object> variablesInParentScope) {
        for (StatementElement element : body) {
            switch (element) {
                case PrintStatement printStatement:
                    printInterpretation(printStatement.getExpressions());
                    break;
                case ReturnStatement returnStatement:
                    if (returnValue != null) {
                        break;
                    }
                    returnValue = parseVariableExpression(returnStatement.getExpression());
                    break;
                case Variable variable:
                    Identifier identifier = variable.getIdentifier();
                    this.variables.put(identifier.getValue(), parseVariableExpression(variable.getExpression()));
                    String variablesKey = this.variables.get(identifier.getValue()).toString();
                    if (variablesKey.equals("func")) {
                        for (ExpressionElement el : variable.getExpression().getExpressions()) {
                            if (el instanceof FunctionStatement functionStatement) {
                                if (variablesInParentScope != null) {
                                    for (Map.Entry<String, Object> entry : variablesInParentScope.entrySet()) {
                                        if (!functionStatement.variablesInScope.containsKey(entry.getKey())) {
                                            functionStatement.variablesInScope.put(entry.getKey(), entry.getValue());
                                        }
                                    }
                                }
                                this.functions.put(identifier.getValue(), functionStatement);
                                this.variables.remove(identifier.getValue());
                            }
                        }
                    }
                    if (functions.containsKey(variablesKey)){
                        FunctionStatement functionStatementValue = functions.get(variablesKey);
                        for (ExpressionElement el : variable.getExpression().getExpressions()) {
                            if (el instanceof FunctionCall functionCall) {
                                this.functions.put(identifier.getValue(), functionStatementValue);
                                this.variables.remove(identifier.getValue());
                            }
                        }
                    }
                    break;
                case AssignmentStatement assignmentStatement:
                    if (assignmentStatement.getIdentifier() instanceof Identifier id) {
                        this.variables.put(id.getValue(), parseVariableExpression(assignmentStatement.getExpression()));
                    }

                    if (assignmentStatement.getIdentifier() instanceof ArrayIndex arrayIndex) {
                        Object value = parseVariableExpression(assignmentStatement.getExpression());
                        Object array = this.variables.get(arrayIndex.identifier.getValue());
                        ArrayList<Object> updatedArray = setElementToArray(array, getIndex(arrayIndex.expression.getExpressions()), value);
                        this.variables.put(arrayIndex.identifier.getValue(), updatedArray);
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

    public ArrayList<Object> setElementToArray(Object array, int index, Object value) {
        String[] obj = array.toString().substring(1, array.toString().length() - 1).split(",");

        for (int i = 0; i < obj.length; i++) {
            obj[i] = obj[i].trim();
        }

        ArrayList<Object> newArray = new ArrayList<>(Arrays.asList(obj));

        if (index > newArray.size()) {
            while (newArray.size() < index) {
                newArray.add(null);
            }
        }
        newArray.set(index - 1, value);

        return newArray;
    }

    public String[] split(String expression) {
        Stack<Character> stack = new Stack<>();
        ArrayList<String> result = new ArrayList<>();
        StringBuilder str = new StringBuilder();
        char[] letters = expression.toCharArray();
        for (int i = 0; i < letters.length; i++) {
            char character = expression.charAt(i);
            str.append(character);

            if (character == '{') stack.push(character);
            if (character == '}') stack.pop();

            if ((character == ',' && stack.isEmpty()) || i == letters.length - 1) {
                int length = str.length();
                if (str.charAt(length - 1) == ',') {
                    result.add(str.substring(0, length - 1).trim());
                } else {
                    result.add(str.toString().trim());
                }
                str = new StringBuilder();
            }
        }

        return result.toArray(new String[0]);
    }

    public Object getValueFromArray(int index, Object array) {
        String[] strings = split(array.toString().substring(1, array.toString().length() - 1));
        ArrayList<Object> newArray = new ArrayList<>(Arrays.asList(strings));

        return newArray.get(index);
    }

    public int getIndex(ArrayList<ExpressionElement> elements) {
        SHA sha = new SHA();
        return Integer.parseInt(sha.evaluate(SHA.toRPN(interpretExpression(elements).toString())).toString(), 10);
    }

    public Map<Object, Object> getTuple(String tuple) {
        Map<Object, Object> map = new HashMap<>();
        String[] tupleElements = tuple.substring(1, tuple.length() - 1).split(",");
        for (String elem : tupleElements) {
            String[] values = elem.split("=");
            map.put(values[0].trim(), values[1].trim());
        }

        return map;
    }

    public Object interpretExpression(ArrayList<ExpressionElement> elements) {
        StringBuilder str = new StringBuilder();
        ArrayList<String> result = new ArrayList<>();

        for (ExpressionElement element : elements) {
            switch (element) {
                case ArrayIndex arrayIndex -> {
                    return parseElement(arrayIndex);
                }
                case DotNotation dotNotation -> {
                    return parseElement(dotNotation);
                }
                case TupleLiteral tupleLiteral -> {
                    this.lastVariableType = "tuple";
                    Map<Object, Object> map = new HashMap<>();
                    String tupleStr = parseElement(tupleLiteral);
                    String[] newTuple = tupleStr.substring(1, tupleStr.length() - 1).split(",");
                    for (String elem : newTuple) {
                        SHA sha = new SHA();
                        String[] values = elem.split(" := ");
                        try {
                            map.put(values[0].trim(), sha.evaluate(SHA.toRPN(values[1].trim())));
                        } catch (Exception e) {
                            map.put(map.size() + 1, sha.evaluate(SHA.toRPN(values[0].trim())));
                        }
                    }
                    return map;
                }
                case ArrayLiteral arrayLiteral -> {
                    this.lastVariableType = "array";
                    String array = parseElement(arrayLiteral);
                    String[] newArray = array.substring(1, array.length() - 1).split(",");
                    for (String elem : newArray) {
                        SHA sha = new SHA();
                        Object value = sha.evaluate(SHA.toRPN(elem));
                        result.add(value == null ? null : value.toString());
                    }
                    return result;
                }
                case StringLiteral stringLiteral -> {
                    this.lastVariableType = "string";
                    str.append(parseElement(stringLiteral));
                }
                case FunctionStatement functionStatement -> {
                    this.lastVariableType = "functionStatement";
                    str.append(parseElement(functionStatement));
                }
                default -> {
                    str.append(parseElement(element));
                }
            }
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
        if (functions.containsKey(strExpression.toString())) {
            return strExpression;
        }

        String rpn = SHA.toRPN(strExpression.toString());
        return sha.evaluate(rpn);
    }

    public void printInterpretation(ArrayList<Expression> expressions) {
        for (Expression expression : expressions) {
            System.out.println(parseVariableExpression(expression).toString().replace("'", ""));
        }
    }

    public String parseElement(ExpressionElement element) {
        return switch (element) {
            case StringLiteral str -> "'" + str.value.substring(1, str.value.length() - 1) + "'";
            case IntegerLiteral integerLiteral -> integerLiteral.value;
            case BooleanLiteral bool -> bool.value;
            case RealLiteral realLiteral -> realLiteral.value;
            case Identifier id -> {
                for (FunctionStatement functionStatement : functionList) {
                    Object value = functionStatement.variablesInScope.get(id.getValue());
                    if (value != null) {
                        yield value.toString();
                    }
                }

                Object func = this.functions.get(id.getValue());
                if (func != null) {
                    this.lastVariableType = "functionStatement";
                    yield id.getValue();
                }

                Object result = this.variables.get(id.getValue());
                if (result == null) {
                    yield id.getValue();
                }

                char start = result.toString().charAt(0);
                char end = result.toString().charAt(result.toString().length() - 1);
                if (start == '{' && end == '}') {
                    this.lastVariableType = "tuple";
                }

                if (start == '[' && end == ']') {
                    this.lastVariableType = "array";
                }

                yield result.toString();
            }
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
            case FunctionStatement functionStatement -> "func";
            case ArrayIndex ai -> {
                String id = ai.identifier.getValue();
                int index = getIndex(ai.expression.getExpressions());
                yield getValueFromArray(index - 1, this.variables.get(id)).toString().trim();
            }
            case TupleIndex tupleIndex -> String.valueOf(tupleIndex.getValue());
            case DotNotation dotNotation -> {
                String id = dotNotation.getIdentifier().getValue();
                String attribute = parseElement(dotNotation.getAttribute());
                String tuple = this.variables.get(id).toString();
                Map<Object, Object> map = getTuple(tuple);

                yield map.get(attribute).toString();
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
            case TupleLiteral tupleLiteral -> {
                StringBuilder tuple = new StringBuilder();
                tuple.append("{");
                ArrayList<TupleElement> elements = tupleLiteral.getElements();
                for (int i = 0; i < elements.size(); i++) {
                    String id = Objects.equals(elements.get(i).getIdentifier().getValue(), "") ? null : elements.get(i).getIdentifier().getValue();
                    if (id == null) {
                        tuple.append(interpretExpression(elements.get(i).getExpression().getExpressions()));
                        if (i != elements.size() - 1) {
                            tuple.append(",");
                        }
                        continue;
                    }

                    tuple.append(id);
                    tuple.append(" := ");
                    tuple.append(interpretExpression(elements.get(i).getExpression().getExpressions()));

                    if (i != elements.size() - 1) {
                        tuple.append(",");
                    }
                }
                tuple.append("}");
                yield tuple.toString();
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
                            yield "'" + reader.next() + "'";
                        }
                        default -> {
                            FunctionStatement func = this.functions.get(id.getValue());
                            this.functionList.add(func);
                            ArrayList<Identifier> ids = func.getArguments();
                            ArrayList<Expression> values = call.getArguments();

                            for (int i = 0; i < ids.size(); i++) {
                                Object interpretedExpression = parseVariableExpression(values.get(i));
                                func.variablesInScope.put(ids.get(i).getValue(), interpretedExpression);
                            }
                            returnValue = null;
                            parseBody(func.getBody(), func.getVariablesInScope());

                            func.variablesInScope.clear();
                            this.functionList.removeLast();

                            yield returnValue.toString();
                        }
                    }
                }
                yield null;
            }
            default -> throw new IllegalStateException("Unexpected value: " + element);
        };

    }
}
