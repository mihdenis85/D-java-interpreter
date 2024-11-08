package src.core;
import java.util.Stack;
import java.lang.Character;

public class SHA {
    private static boolean letterOrDigit(char c) {
        return Character.isLetterOrDigit(c);
    }

    static int getPrecedence(char ch) {
        if (ch == '+' || ch == '-')
            return 1;
        else if (ch == '*' || ch == '/')
            return 2;

        return -1;
    }

    static boolean hasLeftAssociativity(char ch) {
        return ch == '+' || ch == '-' || ch == '/' || ch == '*';
    }

    static String infixToRpn(String expression) {
        Stack<Character> stack = new Stack<>();
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < expression.length(); ++i) {
            char c = expression.charAt(i);
            if (letterOrDigit(c))
                output.append(c);
            else {
                while (
                        !stack.isEmpty()
                                && getPrecedence(c)
                                <= getPrecedence(stack.peek())
                                && hasLeftAssociativity(c)) {
                    output.append(stack.pop());
                }
                stack.push(c);
            }
        }

        while (!stack.isEmpty()) {
            output.append(stack.pop());
        }

        return output.toString();
    }

    public String analyze(String expression) {
        return infixToRpn(expression);
    }
}

