package src.core;

import src.core.expressionElements.*;

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
        precedence.put("is", 0);

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

        List<String> tokens = tokenize(expression);

        for (String token : tokens) {
            if (isNumber.test(token)) {
                output.add(token);
            } else if (token.equals("int") || token.equals("real") || token.equals("string") || token.equals("true") || token.equals("false")) {
                output.add(token);
            } else if (token.charAt(0) == '\'' && token.charAt(token.length() - 1) == '\'') {
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

    private static List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        String regex = "\\s*(>=|<=|/=|>|<|=|xor|and|or|not|is|int|real|string|true|false|\\+|\\-|\\*|/|\\d*\\.?\\d+|\"[^\"]+\"|'[^']+')\\s*";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(expression);

        int pos = 0;
        while (matcher.find()) {
            if (matcher.start() != pos) {
                throw new IllegalArgumentException("Недопустимый символ в выражении на позиции " + pos);
            }
            String token = matcher.group(1);
            tokens.add(token);
            pos = matcher.end();
        }

        if (pos != expression.length()) {
            throw new IllegalArgumentException("Недопустимый символ в выражении на позиции " + pos);
        }
        return tokens;
    }

    public Object evaluate(String rpnExpression) {
        if (rpnExpression == null || rpnExpression.isEmpty()) {
            return 0.0;
        }

        String[] tokens = rpnExpression.split(",");
        Stack<Object> stack = new Stack<>();

        for (String token : tokens) {
            switch (token) {
                case "not":
                    if (!stack.isEmpty()) {
                        stack.push(UnaryNot.evaluate(stack.pop()));
                    }
                    break;

                case "is":
                    if (stack.size() >= 2) {
                        stack.push(IsOperator.evaluate(stack.pop(), stack.pop()));
                    }
                    break;

                case "and":
                    if (stack.size() >= 2) {
                        stack.push(LogicalAnd.evaluate(stack.pop(), stack.pop()));
                    }
                    break;

                case "xor":
                    if (stack.size() >= 2) {
                        stack.push(LogicalXor.evaluate(stack.pop(), stack.pop()));
                    }
                    break;

                case "or":
                    if (stack.size() >= 2) {
                        stack.push(LogicalOr.evaluate(stack.pop(), stack.pop()));
                    }
                    break;

                case "+":
                    if (stack.size() == 1) {
                        stack.push(UnaryPlus.evaluate(stack.pop()));
                    }

                    if (stack.size() >= 2) {
                        stack.push(PlusSign.evaluate(stack.pop(), stack.pop()));
                    }
                    break;

                case "-":
                    if (stack.size() == 1) {
                        stack.push(UnaryMinus.evaluate(stack.pop()));
                    }

                    if (stack.size() >= 2) {
                        stack.push(MinusSign.evaluate(stack.pop(), stack.pop()));
                    }
                    break;

                case "*":
                    if (stack.size() >= 2) {
                        stack.push(MultiplySign.evaluate(stack.pop(), stack.pop()));
                    }
                    break;

                case "/":
                    if (stack.size() >= 2) {
                        stack.push(DivideSign.evaluate(stack.pop(), stack.pop()));
                    }
                    break;

                case ">":
                    if (stack.size() >= 2) {
                        stack.push(GreaterSign.evaluate(stack.pop(), stack.pop()));
                    }
                    break;

                case "<":
                    if (stack.size() >= 2) {
                        stack.push(LessSign.evaluate(stack.pop(), stack.pop()));
                    }
                    break;

                case ">=":
                    if (stack.size() >= 2) {
                        stack.push(GreaterEqualSign.evaluate(stack.pop(), stack.pop()));
                    }
                    break;

                case "<=":
                    if (stack.size() >= 2) {
                        stack.push(LessEqualSign.evaluate(stack.pop(), stack.pop()));
                    }
                    break;

                case "=":
                    if (stack.size() >= 2) {
                        stack.push(EqualSign.evaluate(stack.pop(), stack.pop()));
                    }
                    break;

                case "/=":
                    if (stack.size() >= 2) {
                        stack.push(NotEqualSign.evaluate(stack.pop(), stack.pop()));
                    }
                    break;

                default:
                    try {
                        stack.push(token);
                    } catch (NumberFormatException e) {
                        // Ignore invalid tokens
                    }
                    break;
            }
        }

        return stack.pop();
    }

}
