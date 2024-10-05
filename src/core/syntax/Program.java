package src.core.syntax;

import java.util.ArrayList;

public class Program {
    private final ArrayList<ArrayList<?>> program;

    public Program(ArrayList<Variable> variableDeclaration, ArrayList<Operator> operatorDeclaration) {
        this.program = new ArrayList<>();
        this.program.add(variableDeclaration);
        this.program.add(operatorDeclaration);
    }

    public ArrayList<ArrayList<?>> getProgram() {
        return program;
    }
}
