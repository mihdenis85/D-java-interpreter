package src.core.syntax;

import src.core.syntax.interfaces.JSONConvertable;
import src.core.syntax.interfaces.StatementElement;
import src.core.syntax.interfaces.SyntaxElement;

import java.util.ArrayList;

public class Program implements JSONConvertable {
    private final ArrayList<StatementElement> program;

    public Program(ArrayList<StatementElement> variables) {
        this.program = variables;
    }

    public ArrayList<StatementElement> getProgram() {
        return program;
    }

    @Override
    public String toJSONString() {
        return "{\"Program\": " + JSONConvertable.listToJsonString(program) + "\n}";
    }
}
