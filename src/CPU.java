// A simple 5-stage pipeline simulator with basic forwarding and stall for load-use.
public class CPU {
    private RegisterFile regs;
    private Memory mem;
    private InstructionMemory imem;
    private int pc = 0;
    private boolean halted = false;

    // Pipeline registers (hold instruction and basic fields)
    private IF_ID if_id = new IF_ID();
    private ID_EX id_ex = new ID_EX();
    private EX_MEM ex_mem = new EX_MEM();
    private MEM_WB mem_wb = new MEM_WB();

    public CPU(RegisterFile regs, Memory mem, InstructionMemory imem) {
        this.regs = regs;
        this.mem = mem;
        this.imem = imem;
    }

    public void reset() {
        pc = 0;
        halted = false;
        if_id.clear();
        id_ex.clear();
        ex_mem.clear();
        mem_wb.clear();
    }

    public boolean isHalted() { return halted; }

    public void cycle() {
        // writeback
        writeBack();
        // memory stage
        memoryStage();
        // execute stage
        executeStage();
        // decode stage
        decodeStage();
        // fetch stage
        fetchStage();
    }

    private void writeBack() {
        if(mem_wb.instr==null) return;
        Instruction inst = mem_wb.instr;
        if(inst.type==InstructionType.R || inst.opcode.endsWith("i") || inst.opcode.equals("lui") || inst.opcode.equals("auipc") || inst.opcode.equals("jal")) {
            regs.write(mem_wb.rd, mem_wb.writeData);
        }
        // loads handled: load value placed in writeData earlier
        // For jalr/jal handled in EX/MEM stages
    }

    private void memoryStage() {
        ex_mem.transferTo(mem_wb);
        if(mem_wb.instr==null) { return; }
        Instruction inst = mem_wb.instr;
        switch(inst.opcode) {
            case "lw":
                int val = mem.loadWord(mem_wb.aluResult);
                mem_wb.writeData = val;
                break;
            case "sw":
                mem.storeWord(mem_wb.aluResult, mem_wb.storeValue);
                break;
            default:
                // nothing
        }
    }

    private void executeStage() {
        // simple ALU ops and branch decisions
        id_ex.transferTo(ex_mem);
        if(ex_mem.instr==null) return;
        Instruction inst = ex_mem.instr;
        int a = ex_mem.rs1Val;
        int b = ex_mem.rs2Val;
        int imm = ex_mem.imm;
        switch(inst.opcode) {
            case "add": case "sub": case "and": case "or": case "xor":
            case "sll": case "srl": case "sra": case "slt": case "sltu":
            case "addi": case "andi": case "ori": case "xori": case "slli": case "srli": case "srai": case "slti": case "sltiu":
                ex_mem.aluResult = ALU.exec(inst.opcode, a, b, imm);
                ex_mem.rd = ex_mem.destReg;
                break;
            case "lw": case "sw":
                ex_mem.aluResult = a + imm;
                ex_mem.rd = ex_mem.destReg;
                break;
            case "beq":
                if(a==b) { pc = ex_mem.pc + imm; flushIFID(); }
                break;
            case "bne":
                if(a!=b) { pc = ex_mem.pc + imm; flushIFID(); }
                break;
            case "jal":
                ex_mem.aluResult = ex_mem.pc + 1;
                pc = ex_mem.pc + imm;
                flushIFID();
                ex_mem.rd = ex_mem.destReg;
                ex_mem.writeData = ex_mem.aluResult;
                break;
            case "jalr":
                ex_mem.aluResult = ex_mem.pc + 1;
                int target = (a + imm);
                pc = target;
                flushIFID();
                ex_mem.rd = ex_mem.destReg;
                ex_mem.writeData = ex_mem.aluResult;
                break;
            case "lui":
                ex_mem.aluResult = imm;
                ex_mem.rd = ex_mem.destReg;
                break;
            case "auipc":
                ex_mem.aluResult = ex_mem.pc + imm;
                ex_mem.rd = ex_mem.destReg;
                break;
            default:
                // unknown/no-op
        }
    }

    private void decodeStage() {
        if_id.transferTo(id_ex);
        if(id_ex.instr==null) return;
        Instruction inst = id_ex.instr;
        id_ex.pc = if_id.pc;
        // read registers
        id_ex.rs1Val = regs.read(inst.rs1);
        id_ex.rs2Val = regs.read(inst.rs2);
        id_ex.imm = inst.imm;
        id_ex.destReg = inst.rd;
        // simple hazard detection for load-use: if previous is lw and using the loaded reg, stall
        if(ex_mem.instr!=null && ex_mem.instr.opcode.equals("lw") && ex_mem.destReg!=0) {
            int loadedReg = ex_mem.destReg;
            if(inst.rs1==loadedReg || inst.rs2==loadedReg) {
                // insert bubble: stall by clearing id_ex and not advancing if_id next cycle
                id_ex.clear();
                // prevent fetch from advancing pc by restoring if_id (we'll simply leave pc unchanged)
                return;
            }
        }
    }

    private void fetchStage() {
        Instruction inst = imem.get(pc);
        if_id.instr = inst;
        if_id.pc = pc;
        if(inst==null) {
            // no instruction -> halt
            halted = true;
            return;
        }
        pc = pc + 1;
    }

    private void flushIFID() {
        if_id.clear();
    }

    public String dumpPipeline() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("PC=%d\n", pc));
        sb.append("IF/ID: " + (if_id.instr==null? "empty": if_id.instr.raw) + "\n");
        sb.append("ID/EX: " + (id_ex.instr==null? "empty": id_ex.instr.raw) + "\n");
        sb.append("EX/MEM: " + (ex_mem.instr==null? "empty": ex_mem.instr.raw) + "\n");
        sb.append("MEM/WB: " + (mem_wb.instr==null? "empty": mem_wb.instr.raw) + "\n");
        return sb.toString();
    }

    // Pipeline register classes and simple transfer methods
    class IF_ID {
        Instruction instr; int pc;
        void clear(){ instr=null; pc=0; }
        void transferTo(ID_EX dest) {
            dest.instr = this.instr;
            dest.pc = this.pc;
        }
    }
    class ID_EX {
        Instruction instr; int pc;
        int rs1Val, rs2Val, imm; int destReg;
        void clear(){ instr=null; pc=0; rs1Val=rs2Val=imm=destReg=0; }
        void transferTo(EX_MEM dest) {
            dest.instr = this.instr;
            dest.pc = this.pc;
            dest.rs1Val = this.rs1Val;
            dest.rs2Val = this.rs2Val;
            dest.imm = this.imm;
            dest.destReg = this.destReg;
        }
    }
    class EX_MEM {
        Instruction instr; int pc;
        int rs1Val, rs2Val, imm; int destReg;
        int aluResult; int storeValue; int rd; int writeData;
        void clear(){ instr=null; pc=0; rs1Val=rs2Val=imm=destReg=aluResult=storeValue=rd=writeData=0; }
        void transferTo(MEM_WB dest) {
            dest.instr = this.instr;
            dest.aluResult = this.aluResult;
            dest.rd = this.rd;
            dest.writeData = this.writeData;
            dest.storeValue = this.rs2Val;
        }
    }
    class MEM_WB {
        Instruction instr; int aluResult; int rd; int writeData; int storeValue;
        void clear(){ instr=null; aluResult=rd=writeData=storeValue=0; }
    }
}
