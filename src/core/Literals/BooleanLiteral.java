package src.core.Literals;

public class BooleanLiteral {
    public boolean boolValue;

    public BooleanLiteral(String boolValue) {
        this.boolValue = Boolean.parseBoolean(boolValue);
    }

    public boolean getBoolValue() {
        return this.boolValue;
    }
}
