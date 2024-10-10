package src.core.enums;

public enum Punct {
    tkDot,
    tkComma,
    tkSemicolon,
    tkColon,
    tkOpenedArrayBracket,
    tkClosedArrayBracket,
    tkOpenedTupleBracket,
    tkClosedTupleBracket,
    tkOpenedBracket,
    tkClosedBracket,
    tkArrowFunctionSign,
    tkAssignment;

    public static boolean contains(Code type) {
        if (type == null) {
            return false;
        }
        for (Punct punct : Punct.values()) {
            if (punct.name().equals(type.name())) {
                return true;
            }
        }
        return false;
    }
}
