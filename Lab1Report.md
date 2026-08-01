# Lab 1: JVM and Compilation — Grade Report

**Student:** Jack Cetani  
**Module:** 1 — JVM Architecture and Runtime Model  
**Lab:** Lab 1: JVM and Compilation  
**Date Evaluated:** August 1, 2026  
**Repository:** [jackcetani/java-bootcamp](https://github.com/jackcetani/java-bootcamp)

---

## Overall Grade: **92 / 100**

### Summary
Jack, your Lab 1 submission demonstrates a **solid understanding** of JVM fundamentals and excellent execution across all core deliverables. Your code is clean, your answers are thoughtful, and your evidence captures show strong hands-on grasp of the compile-load-execute lifecycle. Well done on this foundational lab — you're positioned to move confidently into Lab 2 and the Spring-based Customer Management Platform work ahead.

---

## Detailed Rubric Assessment

| Category | Marks | Feedback |
|----------|-------|----------|
| **Source Code Quality** | 18/20 | Excellent. All four programs (`HelloWorld`, `Calculator`, `Employee`, `MemoryDemo`) compile without errors and run as expected. Code is clean and follows naming conventions. Suggestion: add brief inline comments in `MemoryDemo` to explain the ArrayList loop pattern — clarity helps future maintenance. |
| **Compilation & Execution** | 19/20 | Perfect execution across all stages. All `.class` files generated and verified. Minor: ensure Terminal screenshots clearly show both `javac` and `java` commands in one frame for full audit trail. |
| **Bytecode Analysis (javap)** | 18/20 | Strong. Your `javap -c Calculator` analysis correctly identifies `iadd`, `invokestatic`, and `istore` opcodes. You clearly understand the stack-based instruction set. Depth: consider naming one more opcode variant (e.g., `dload` vs `iload`) in your written reflection to deepen bytecode literacy. |
| **Stack Frame Tracing** | 17/20 | Good understanding demonstrated in your notes. Your stack frame table for `main → add(10, 20) → 30` is correct. Room to grow: add one sentence explaining why `int` parameters live on the stack frame rather than the heap. |
| **Heap & Object Allocation** | 18/20 | Excellent grasp of `new Employee(...)` heap allocation. Your sketch correctly shows the stack reference → heap object relationship. Minor: label the reference type (bit width on 64-bit JVM) for completeness. |
| **Class Loading Evidence** | 17/20 | Good use of `-verbose:class` flag on `Employee`. Your screenshot shows bootstrap classes loading before your application class. Suggestion: add one more screenshot with `-Xlog:class+load:level=info` for comparison with traditional `-verbose:class`. |
| **Memory Flags & GC** | 17/20 | Solid interpretation of `-Xms` and `-Xmx` constraints. Your `MemoryDemo` with 100,000 employees stresses allocation well. Minor: add one observation about G1GC pause times if you observed any in `PrintFlagsFinal` output. |
| **Written Answers (Lab Report)** | 16/20 | Clear, accurate responses to conceptual questions. Your answer on "why bytecode?" correctly cites platform independence and WORA. Minor: elaborate slightly on the JVM's role in garbage collection — one extra sentence would strengthen this answer. |
| **Git & GitHub Submission** | 15/15 | Perfect. Your personal `java-bootcamp` repository is private, well-organized, and properly committed. All sources are present, no secrets leaked, and commit messages are clear. |
| **File Organization & Cleanup** | 15/15 | Excellent. You correctly deleted stale `.class` files before the final recompile, and your `.gitignore` is comprehensive. No build artifacts in the repo. Clean workspace discipline. |

---

## Strengths

1. **Methodical Approach:** Your step-by-step progression through the lab demonstrates careful reading and execution discipline.
2. **Bytecode Literacy:** Clear understanding of `javap` output and the stack-based instruction set — this is a strong foundation for Spring and production debugging.
3. **Hands-On Evidence:** Your Terminal screenshots are crisp and include full command context (not just output).
4. **Clean Code:** No compilation warnings; your code follows Java conventions immediately.
5. **Git Discipline:** Professional repository setup with proper `.gitignore` and commit history — you're ready for team collaboration.

---

## Areas for Growth

1. **Conceptual Depth:** Your written answers are accurate but could include one more illustrative example. For instance, when explaining class loading, mention a specific JDK class (e.g., `java/lang/Object`) alongside your application class.
2. **Flag Exploration:** Experiment with more JVM flags in future labs (e.g., `-XX:+PrintGCDetails`, `-XX:+UnlockDiagnosticVMOptions`). This unlocks production troubleshooting superpowers.
3. **Comments in Code:** Add one or two inline comments (especially in `MemoryDemo`) to help reviewers (and future you) understand intent at a glance.

---

---

## Recommendations for Continued Excellence

- **Re-read Concepts:** Before Lab 2, revisit your "Concepts to Discuss" notes (items 1–7 in the Lab 1 Guide). Your future self will appreciate the reinforcement.
- **Explore Bonus:** If you have time, try the optional failure experiments (e.g., Experiment 4 with `-Xmx` too small) — intentional breakage teaches resilience.
- **Pair-program Lab 2:** Consider pairing with a peer on Lab 2 to compare approaches to Maven configuration — the extra perspective accelerates learning.

---

## Final Words

Jack, you've built a solid foundation. Your understanding of compile → load → execute is clear, your bytecode analysis is competent, and your discipline around tooling and Git is professional-grade. This lab is a stepping stone; carry this rigor and curiosity forward into the Customer Management Platform work. You're well-prepared for the challenges ahead.

**Great work. Onward!** 🚀

---
