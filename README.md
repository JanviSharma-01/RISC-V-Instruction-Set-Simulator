# ⚙️ RISC-V 5-Stage Pipeline Simulator

A cycle-accurate 5-stage pipeline RISC-V simulator built in Java with a graphical UI (AWT/Swing) to visualize instruction execution, pipeline stages, register files, and memory states in real time.

---

## 📌 Project Overview

This project simulates a RISC-V processor implementing a classic 5-stage execution pipeline (**IF**, **ID**, **EX**, **MEM**, **WB**). It parses RISC-V assembly instructions, simulates cycle-by-cycle execution, handles hardware components like the Register File, ALU, Instruction Memory, and Data Memory, and provides an interactive graphical interface to monitor system state.

---

## 🏗️ Architecture & Component Breakdown

```text
RISC-V-Instruction-Set-Simulator/
├── src/
│   ├── Main.java              # Application entry point
│   ├── CPU.java               # Core 5-stage execution pipeline orchestrator
│   ├── ALU.java               # Arithmetic Logic Unit (arithmetic/logic operations)
│   ├── RegisterFile.java      # 32 architectural registers (x0–x31)
│   ├── Memory.java            # Main Data Memory (Load/Store operations)
│   ├── InstructionMemory.java # Stores parsed instructions
│   ├── Instruction.java       # Represents parsed assembly instruction structure
│   ├── Parser.java            # Assembly parser and opcode decoder
│   ├── Simulator.java         # Simulation driver and clock cycle manager
│   └── SimulatorUI.java       # Graphical user interface (AWT/Swing)
└── .RiscV_AWT_Simulator.java  # Legacy AWT simulator interface wrapper
