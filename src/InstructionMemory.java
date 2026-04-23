import java.util.*;
import java.util.stream.*;

public class InstructionMemory {
    private java.util.List<Instruction> instrs = new ArrayList<>();

    public void loadFromString(String asm) {
        instrs.clear();
        String[] lines = asm.split("\\n");
        for(String l: lines) {
            String s = l.split("#")[0].trim();
            if(s.isEmpty()) continue;
            instrs.add(Parser.parseLine(s));
        }
    }

    public Instruction get(int pcWord) {
        if(pcWord < 0 || pcWord >= instrs.size()) return null;
        return instrs.get(pcWord);
    }

    public void clear() {
        instrs.clear();
    }

    public int size() { return instrs.size(); }
}
