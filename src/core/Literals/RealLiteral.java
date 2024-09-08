package src.core.Literals;

public class RealLiteral {
    public float realValue;

    public RealLiteral(String realValue) {
        this.realValue = Float.parseFloat(realValue);
    }

    public float getRealValue() {
        return this.realValue;
    }
}
