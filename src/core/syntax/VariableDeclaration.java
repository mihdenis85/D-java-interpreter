package src.core.syntax;

import java.util.ArrayList;

public class VariableDeclaration {
    private final ArrayList<Variable> variables;

    public VariableDeclaration(ArrayList<Variable> variables) {
        this.variables = variables;
    }

    public ArrayList<Variable> getVariables() {
        return variables;
    }
}
