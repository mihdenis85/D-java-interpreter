package src.core.syntax;

import src.core.syntax.interfaces.JSONSerializable;
import src.core.syntax.interfaces.StatementElement;

import java.util.ArrayList;

public class Program implements JSONSerializable {
    private final ArrayList<StatementElement> program;

    public Program(ArrayList<StatementElement> variables) {
        this.program = variables;
    }

    public ArrayList<StatementElement> getProgram() {
        return program;
    }

    @Override
    public String serializeToJson() {
        return "{\"Program\": " + JSONSerializable.serializeListToJson(program) + "\n}";
    }
}
