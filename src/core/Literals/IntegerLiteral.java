package src.core.Literals;

public class IntegerLiteral {
    public int intValue;

    public IntegerLiteral(String intValue) {
        this.intValue = Integer.parseInt(intValue);
    }

    public int getIntValue() {
        return this.intValue;
    }
}
