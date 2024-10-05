package src.core.syntax;

import java.util.ArrayList;

public class OperatorDeclaration {
    private final ArrayList<Operator> operators;

    public OperatorDeclaration(ArrayList<Operator> operators) {
        this.operators = operators;
    }

    public ArrayList<Operator> getOperators() {
        return operators;
    }
}
