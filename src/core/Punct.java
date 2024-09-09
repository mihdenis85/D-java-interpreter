package src.core;

public class Punct extends Token {
    public final Type type;
    public Punct(Type type, Span span, Code codeType) {
        super(span, codeType, type.lexeme);
        this.type = type;
    }

    public enum Type {
        tkMultiplySign("*"),
        tkDivideSign("/"),
        tkPlusSign("+"),
        tkMinusSign("-"),
        tkEqualSign("="),
        tkLessSign("<"),
        tkLessEqualSign("<="),
        tkGreaterSign(">"),
        tkGreaterEqualSign(">="),
        tkDivideEqualSign("/="),
        tkAssignment(":="),
        tkComma(","),
        tkSemicolon(";"),
        tkColon(":"),
        tkOpenedArrayBracket("["),
        tkClosedArrayBracket("]"),
        tkOpenedTupleBracket("{"),
        tkClosedTupleBracket("}"),
        tkArrowFunctionSign("=>");

        public final String lexeme;

        Type(String lexeme) {
            this.lexeme = lexeme;
        }

        @Override
        public String toString() {
            return lexeme;
        }
    }
}
