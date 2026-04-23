public class ALU {
    public static int exec(String op, int a, int b, int imm) {
        switch(op) {
            case "add": case "addi": return a + (op.endsWith("i")? imm : b);
            case "sub": return a - b;
            case "and": case "andi": return a & (op.endsWith("i")? imm : b);
            case "or": case "ori": return a | (op.endsWith("i")? imm : b);
            case "xor": case "xori": return a ^ (op.endsWith("i")? imm : b);
            case "sll": case "slli": return a << (op.endsWith("i")? imm : b);
            case "srl": case "srli": return a >>> (op.endsWith("i")? imm : b);
            case "sra": case "srai": return a >> (op.endsWith("i")? imm : b);
            case "slt": case "slti": return (a < (op.endsWith("i")? imm : b))?1:0;
            case "sltu": case "sltiu": return (Integer.compareUnsigned(a, (op.endsWith("i")? imm : b))<0)?1:0;
            default: return 0;
        }
    }
}
