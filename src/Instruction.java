public class Instruction {
    public String raw;
    public String opcode;
    public int rd, rs1, rs2;
    public int imm;
    public InstructionType type;

    public Instruction(String raw) {
        this.raw = raw.trim();
    }

    public String toString() {
        return raw;
    }
}

enum InstructionType {
    R, I, S, B, U, J, UNKNOWN
}
