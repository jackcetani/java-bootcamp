# Exercise — Variables and Data Types

**Module 1** · Pre-lab practice · then open [`../../lab1/LAB-1-GUIDE.md`](../lab1/LAB-1-GUIDE.md)  
**Folder:** `examples/module-01-exercises/` ([setup](EXERCISES-INDEX.md))

## Goal

Create `Variables.java` with local variables of several primitive types and one `String`; print each.

## Starter / reference

```java
public class Variables {
    public static void main(String[] args) {
        int age = 21;
        long population = 8_000_000_000L;
        double price = 19.99;
        boolean enrolled = true;
        char grade = 'A';
        String name = "Aman";

        System.out.println(age);
        System.out.println(population);
        System.out.println(price);
        System.out.println(enrolled);
        System.out.println(grade);
        System.out.println(name);
    }
}
```

## Steps

### Step 1 — Create `Variables.java`

1. Right-click `module-01-exercises` → **New → File** (not Java Class).
2. Name it exactly `Variables.java`.
3. Paste the starter code above. Save (**Ctrl+S** / **⌘S**).

Ignore any yellow *outside of the module source root* banner.

### Step 2 — Compile and run

**Windows:**

```powershell
cd $env:USERPROFILE\java-bootcamp\examples\module-01-exercises
javac Variables.java
java Variables
```

**macOS:**

```bash
cd ~/java-bootcamp/examples/module-01-exercises
javac Variables.java
java Variables
```

**Expected:** Six lines print (age, population, price, enrolled, grade, name) with no errors.

Then commit/push **after Lab 1 Step 0** (see [EXERCISES-INDEX.md](EXERCISES-INDEX.md)).

## Expected result

All declared values print without compile/runtime errors.

## Pass criteria

_Mark each row **Pass** or **Fail** in your lab notes (GitHub markdown files are not interactive checklists)._

| # | Confirm | Your notes |
| - | ------- | ---------- |
| 1 | Code compiles and runs (or notes complete if analysis-only) | Pass / Fail |
| 2 | You can explain the result in one sentence | Pass / Fail |
