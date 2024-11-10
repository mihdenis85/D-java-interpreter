package src.core;

import src.core.expressionElements.LogicalAnd;
import src.core.expressionElements.LogicalOr;
import src.core.expressionElements.LogicalXor;
import src.core.expressionElements.UnaryNot;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.function.Predicate;

public class SHA {
    public String lastOperator;

    public static String toRPN(String expression) throws IllegalArgumentException {
        Map<String, Integer> precedence = new HashMap<>();
        precedence.put("not", 6);
        precedence.put("/", 5);
        precedence.put("*", 5);
        precedence.put("+", 4);
        precedence.put("-", 4);
        precedence.put(">", 3);
        precedence.put("<", 3);
        precedence.put(">=", 3);
        precedence.put("<=", 3);
        precedence.put("=", 3);
        precedence.put("/=", 3);
        precedence.put("and", 2);
        precedence.put("xor", 2);
        precedence.put("or", 1);

        Stack<String> operatorStack = new Stack<>();
        List<String> output = new ArrayList<>();

        Predicate<String> isNumber = token -> {
            try {
                Double.parseDouble(token);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        };

        List<String> tokens = tokenize(expression, precedence.keySet());

        for (String token : tokens) {
            if (isNumber.test(token)) {
                output.add(token);
            } else if (precedence.containsKey(token)) {
                while (!operatorStack.isEmpty() && precedence.containsKey(operatorStack.peek()) &&
                        precedence.get(operatorStack.peek()) >= precedence.get(token)) {
                    output.add(operatorStack.pop());
                }
                operatorStack.push(token);
            } else {
                throw new IllegalArgumentException("Недопустимый токен: " + token);
            }
        }

        while (!operatorStack.isEmpty()) {
            output.add(operatorStack.pop());
        }

        return String.join(",", output);
    }

    private static List<String> tokenize(String expression, Set<String> operators) {
        List<String> tokens = new ArrayList<>();
        String regex = "\\s*(>=|<=|/=|>|<|=|xor|and|or|not|\\+|\\-|\\*|/|\\d*\\.?\\d+)\\s*";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(expression);

        int pos = 0;
        while (matcher.find()) {
            if (matcher.start() != pos) {
                throw new IllegalArgumentException("Недопустимый символ в выражении на позиции " + pos);
            }
            String token = matcher.group(1);
            tokens.add(token.toLowerCase());
            pos = matcher.end();
        }
        if (pos != expression.length()) {
            throw new IllegalArgumentException("Недопустимый символ в выражении на позиции " + pos);
        }
        return tokens;
    }

    public double evaluate(String rpnExpression) {
        if (rpnExpression == null || rpnExpression.isEmpty()) {
            return 0.0;
        }

        String[] tokens = rpnExpression.split(",");
        Stack<Double> stack = new Stack<>();

        for (String token : tokens) {
            switch (token) {
//                case "not":
//                    if (!stack.isEmpty()) {
//                        this.lastOperator = "not";
//                        stack.push(UnaryNot.evaluate(stack.pop()));
//                    }
//                    break;

                case "and":
                    this.lastOperator = "and";
                    if (stack.size() >= 2) {
                        stack.push(LogicalAnd.evaluate(stack.pop(), stack.pop()) ? 1.0 : 0.0);
                    }
                    break;

                case "xor":
                    this.lastOperator = "xor";
                    if (stack.size() >= 2) {
                        stack.push(LogicalXor.evaluate(stack.pop(), stack.pop()) ? 1.0 : 0.0);
                    }
                    break;

                case "or":
                    this.lastOperator = "or";
                    if (stack.size() >= 2) {
                        stack.push(LogicalOr.evaluate(stack.pop(), stack.pop()) ? 1.0 : 0.0);
                    }
                    break;

                case "+":
                    this.lastOperator = "+";
                    if (stack.size() >= 2) {
                        stack.push(stack.pop() + stack.pop());
                    }
                    break;

                case "-":
                    this.lastOperator = "-";
                    if (stack.size() >= 2) {
                        double subtrahend = stack.pop();
                        double minuend = stack.pop();
                        stack.push(minuend - subtrahend);
                    }
                    break;

                case "*":
                    this.lastOperator = "*";
                    if (stack.size() >= 2) {
                        stack.push(stack.pop() * stack.pop());
                    }
                    break;

                case "/":
                    this.lastOperator = "/";
                    if (stack.size() >= 2) {
                        double divisor = stack.pop();
                        double dividend = stack.pop();
                        if (divisor != 0.0) {
                            stack.push(dividend / divisor);
                        }
                    }
                    break;

                case ">":
                    this.lastOperator = ">";
                    if (stack.size() >= 2) {
                        double rightGreater = stack.pop();
                        double leftGreater = stack.pop();
                        stack.push(leftGreater > rightGreater ? 1.0 : 0.0);
                    }
                    break;

                case "<":
                    this.lastOperator = "<";
                    if (stack.size() >= 2) {
                        double rightLess = stack.pop();
                        double leftLess = stack.pop();
                        stack.push(leftLess < rightLess ? 1.0 : 0.0);
                    }
                    break;

                case ">=":
                    this.lastOperator = ">=";
                    if (stack.size() >= 2) {
                        double rightGreaterEq = stack.pop();
                        double leftGreaterEq = stack.pop();
                        stack.push(leftGreaterEq >= rightGreaterEq ? 1.0 : 0.0);
                    }
                    break;

                case "<=":
                    this.lastOperator = "<=";
                    if (stack.size() >= 2) {
                        double rightLessEq = stack.pop();
                        double leftLessEq = stack.pop();
                        stack.push(leftLessEq <= rightLessEq ? 1.0 : 0.0);
                    }
                    break;

                case "=":
                    this.lastOperator = "=";
                    if (stack.size() >= 2) {
                        double rightEq = stack.pop();
                        double leftEq = stack.pop();
                        stack.push(Double.compare(leftEq, rightEq) == 0 ? 1.0 : 0.0);
                    }
                    break;

                case "/=":
                    this.lastOperator = "/=";
                    if (stack.size() >= 2) {
                        double rightNeq = stack.pop();
                        double leftNeq = stack.pop();
                        stack.push(Double.compare(leftNeq, rightNeq) != 0 ? 1.0 : 0.0);
                    }
                    break;

                default:
                    try {
                        stack.push(Double.parseDouble(token));
                    } catch (NumberFormatException e) {
                        // Ignore invalid tokens
                    }
                    break;
            }
        }

        return stack.isEmpty() ? 0.0 : stack.pop();
    }

}
