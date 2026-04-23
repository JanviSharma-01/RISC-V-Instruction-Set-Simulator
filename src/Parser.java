public class Parser {
    // Very simple parser: expects RISC-V assembly in a small subset.
    // Supported forms (examples):
    // add x1, x2, x3
    // addi x1, x2, 10
    // lw x1, 0(x2)
    // sw x1, 0(x2)
    // beq x1, x2, 4  (branch offset in words)
    // jal x1, 8
    // jalr x1, 0(x2)
    // lui x1, 100
    public static Instruction parseLine(String line) {
        Instruction inst = new Instruction(line);
        String s = line.trim();
        String[] tokens = s.split("[,\\s()]+");
        if(tokens.length==0) { inst.opcode="nop"; inst.type=InstructionType.UNKNOWN; return inst; }
        String op = tokens[0];
        inst.opcode = op;
        try {
            switch(op) {
                case "add": case "sub": case "sll": case "slt": case "sltu": case "xor": case "srl": case "sra": case "or": case "and":
                    inst.type = InstructionType.R;
                    inst.rd = parseReg(tokens[1]);
                    inst.rs1 = parseReg(tokens[2]);
                    inst.rs2 = parseReg(tokens[3]);
                    break;
                case "addi": case "slti": case "sltiu": case "xori": case "ori": case "andi":
                    inst.type = InstructionType.I;
                    inst.rd = parseReg(tokens[1]);
                    inst.rs1 = parseReg(tokens[2]);
                    inst.imm = Integer.parseInt(tokens[3]);
                    break;
                case "slli": case "srli": case "srai":
                    inst.type = InstructionType.I;
                    inst.rd = parseReg(tokens[1]);
                    inst.rs1 = parseReg(tokens[2]);
                    inst.imm = Integer.parseInt(tokens[3]);
                    break;
                case "lw": case "lh": case "lb": case "lhu": case "lbu":
                    inst.type = InstructionType.I;
                    inst.rd = parseReg(tokens[1]);
                    inst.imm = Integer.parseInt(tokens[2]);
                    inst.rs1 = parseReg(tokens[3]);
                    break;
                case "sw": case "sh": case "sb":
                    inst.type = InstructionType.S;
                    inst.rs2 = parseReg(tokens[1]);
                    inst.imm = Integer.parseInt(tokens[2]);
                    inst.rs1 = parseReg(tokens[3]);
                    break;
                case "beq": case "bne": case "blt": case "bge": case "bltu": case "bgeu":
                    inst.type = InstructionType.B;
                    inst.rs1 = parseReg(tokens[1]);
                    inst.rs2 = parseReg(tokens[2]);
                    inst.imm = Integer.parseInt(tokens[3]);
                    break;
                case "lui": case "auipc":
                    inst.type = InstructionType.U;
                    inst.rd = parseReg(tokens[1]);
                    inst.imm = Integer.parseInt(tokens[2]);
                    break;
                case "jal":
                    inst.type = InstructionType.J;
                    inst.rd = parseReg(tokens[1]);
                    inst.imm = Integer.parseInt(tokens[2]);
                    break;
                case "jalr":
                    inst.type = InstructionType.I;
                    inst.rd = parseReg(tokens[1]);
                    inst.imm = Integer.parseInt(tokens[2]);
                    inst.rs1 = parseReg(tokens[3]);
                    break;
                case "nop":
                    inst.type = InstructionType.UNKNOWN;
                    break;
                default:
                    inst.type = InstructionType.UNKNOWN;
            }
        } catch(Exception e) {
            inst.type = InstructionType.UNKNOWN;
        }
        return inst;
    }

    private static int parseReg(String s) {
        s = s.trim();
        if(s.startsWith("x")) return Integer.parseInt(s.substring(1));
        if(s.equals("zero")) return 0;
        return Integer.parseInt(s);
    }
}
