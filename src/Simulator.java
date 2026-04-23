import java.util.*;
import java.util.stream.Collectors;

public class Simulator {
    public RegisterFile registers;
    public Memory memory;
    public InstructionMemory imem;
    private CPU cpu;
    private int cycleCount = 0;

    public Simulator() {
        registers = new RegisterFile();
        memory = new Memory(1024); // words
        imem = new InstructionMemory();
        cpu = new CPU(registers, memory, imem);
    }

    public void reset() {
        registers.reset();
        memory.reset();
        imem.clear();
        cpu.reset();
        cycleCount = 0;
    }

    public void loadProgram(String asm) {
        imem.loadFromString(asm);
        cpu.reset(); // start PC at 0
    }

    public void stepCycle() {
        cpu.cycle();
        cycleCount++;
    }

    public boolean isHalted() {
        return cpu.isHalted();
    }

    public int getCycleCount() {
        return cycleCount;
    }

    public String dumpPipeline() {
        return cpu.dumpPipeline();
    }
}
