import java.util.*;

public class TwoPassAssembler {

    static Map<String, Integer> opcodeTable = Map.of(
        "MOV", 1,
        "ADD", 2,
        "SUB", 3,
        "MUL", 4,
        "JMP", 5,
        "HLT", 0
    );

    static class Symbol {
        String name;
        int address;

        Symbol(String name, int address) {
            this.name = name;
            this.address = address;
        }

        public String toString() {
            return name + " -> " + address;
        }
    }

    static List<String> intermediateCode = new ArrayList<>();
    static Map<String, Integer> symbolTable = new LinkedHashMap<>();
    static int locationCounter = 0;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter assembly code line by line.");
        System.out.println("Type END to finish input.\n");

        List<String> sourceCode = new ArrayList<>();

        // Taking input
        while (true) {
            System.out.print("> ");
            String line = sc.nextLine().trim();
            sourceCode.add(line);
            if (line.equals("END")) break;
        }

        // Run Pass I
        passOne(sourceCode);

        // Output symbol table and intermediate code
        System.out.println("\n--- Symbol Table ---");
        for (Map.Entry<String, Integer> entry : symbolTable.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }

        System.out.println("\n--- Intermediate Code ---");
        for (String line : intermediateCode) {
            System.out.println(line);
        }

        // Run Pass II
        System.out.println("\n--- Machine Code ---");
        passTwo(intermediateCode, symbolTable);
    }

    public static void passOne(List<String> sourceCode) {
        for (String line : sourceCode) {
            String[] parts = line.trim().split("\\s+");
            if (parts.length == 0 || parts[0].isEmpty()) continue;

            String label = "", opcode = "", operand = "";

            if (parts.length == 1) {
                opcode = parts[0];
            } else if (parts.length == 2) {
                opcode = parts[0];
                operand = parts[1];
            } else if (parts.length == 3) {
                label = parts[0];
                opcode = parts[1];
                operand = parts[2];
            }

            if (opcode.equals("START")) {
                locationCounter = Integer.parseInt(operand);
                intermediateCode.add("(AD,START) " + operand);
            } else if (opcode.equals("END")) {
                intermediateCode.add("(AD,END)");
                break;
            } else if (opcode.equals("DC") || opcode.equals("DS")) {
                symbolTable.put(label, locationCounter);
                intermediateCode.add("(DL," + opcode + ") " + operand);
                locationCounter += Integer.parseInt(operand);
            } else {
                if (!label.isEmpty()) {
                    symbolTable.put(label, locationCounter);
                }
                String icLine = "(IS," + opcode + ")";
                if (!operand.isEmpty()) {
                    icLine += " " + operand;
                }
                intermediateCode.add(icLine);
                locationCounter++;
            }
        }
    }

    public static void passTwo(List<String> ic, Map<String, Integer> symTable) {
        for (String line : ic) {
            if (line.startsWith("(AD")) {
                continue;
            } else if (line.startsWith("(DL")) {
                System.out.println("----");
                continue;
            } else if (line.startsWith("(IS,")) {
                String[] parts = line.replace("(", "").replace(")", "").split("[,\\s]+");
                String mnemonic = parts[1];
                String operand = (parts.length > 2) ? parts[2] : "";

                int opcode = opcodeTable.getOrDefault(mnemonic, -1);
                int address = 0;

                if (!operand.isEmpty()) {
                    if (symTable.containsKey(operand)) {
                        address = symTable.get(operand);
                    } else {
                        try {
                            address = Integer.parseInt(operand);
                        } catch (NumberFormatException e) {
                            address = 0;
                        }
                    }
                }
                System.out.printf("%02d %03d\n", opcode, address);
            }
        }
    }
}

/*
START 100
> LOOP MOV A
> ADD B
> SUB C
> MUL D
> JMP LOOP
> HLT
> A DC 1
> B DC 2
> C DC 3
> D DC 4
> END
*/
