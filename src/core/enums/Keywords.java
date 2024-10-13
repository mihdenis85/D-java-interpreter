package src.core.enums;

public enum Keywords {
    tkIs,
    tkEnd,
    tkVar,
    tkReturn,
    tkWhileLoop,
    tkForLoop,
    tkLook,
    tkIf,
    tkThen,
    tkInteger,
    tkReal,
    tkString,
    tkBoolean,
    tkEmpty,
    tkNot,
    tkOr,
    tkAnd,
    tkXor,
    tkPrint,
    tkIfStatement,
    tkFunction,
    tkElse,
    tkLoop;


    public static boolean contains(Code type) {
        if (type == null) {
            return false;
        }
        for (Keywords keyword : Keywords.values()) {
            if (keyword.name().equals(type.name())) {
                return true;
            }
        }
        return false;
    }
}
