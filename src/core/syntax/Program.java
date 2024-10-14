package src.core.syntax;

import src.core.syntax.interfaces.JSONConvertable;
import src.core.syntax.interfaces.SyntaxElement;

import java.util.ArrayList;

public class Program implements JSONConvertable {
    private final ArrayList<SyntaxElement> program;

    public Program(ArrayList<SyntaxElement> variables) {
        this.program = variables;
    }

    public ArrayList<SyntaxElement> getProgram() {
        return program;
    }

    @Override
    public String toJSONString() {
        return "{\"Program\": " + JSONConvertable.listToJsonString(program) + "\n}";
    }
}
