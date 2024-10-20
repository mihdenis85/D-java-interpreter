package src.core.syntax;

import src.core.syntax.interfaces.SyntaxElement;

import java.util.ArrayList;

public class Program {
    private final ArrayList<SyntaxElement> program;

    public Program(ArrayList<SyntaxElement> variables) {
        this.program = variables;
    }

    public ArrayList<SyntaxElement> getProgram() {
        return program;
    }
}
