package src.core.enums;

public enum ExpressionStopper {
    tkEnd,
    tkLoop,
    tkElse,
    tkComma,
    tkSemicolon,
    tkThen,
    tkClosedArrayBracket,
    tkClosedBracket,
    tkClosedTupleBracket;

    public static boolean contains(Code type) {
        if (type == null) {
            return false;
        }
        for (ExpressionStopper stopper : ExpressionStopper.values()) {
            if (stopper.name().equals(type.name())) {
                return true;
            }
        }
        return false;
    }
}
