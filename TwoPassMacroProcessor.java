import java.util.*;

public class TwoPassMacroProcessor {

    static class MacroProcessor {

        Map<String, Integer> MNT = new HashMap<>();
        List<String> MDT = new ArrayList<>();
        List<String> intermediateCode = new ArrayList<>();

        // Pass 1: Build MNT and MDT
        public void pass1(List<String> inputLines) {
            boolean inMacroDef = false;
            String macroName = null;

            for (String line : inputLines) {
                line = line.trim();
                if (line.equalsIgnoreCase("MACRO")) {
                    inMacroDef = true;
                    continue;
                }

                if (inMacroDef) {
                    if (macroName == null) {
                        macroName = line.split(" ")[0];
                        MNT.put(macroName, MDT.size());
                    }

                    MDT.add(line);

                    if (line.equalsIgnoreCase("MEND")) {
                        inMacroDef = false;
                        macroName = null;
                    }
                } else {
                    intermediateCode.add(line);
                }
            }
        }

        // Pass 2: Expand macros
        public void pass2() {
            System.out.println("\n--- Expanded Code (Pass 2 Output) ---");

            for (String line : intermediateCode) {
                String[] tokens = line.split(" ");
                if (MNT.containsKey(tokens[0])) {
                    expandMacro(tokens);
                } else {
                    System.out.println(line);
                }
            }
        }

        // Macro expansion logic
        private void expandMacro(String[] callTokens) {
            String macroName = callTokens[0];
            int mdtIndex = MNT.get(macroName);

            // Get macro definition line (with parameters)
            String defLine = MDT.get(mdtIndex);
            String[] defParts = defLine.split(" ");
            if (defParts.length < 2) {
                System.out.println("// Error: No parameters found for macro " + macroName);
                return;
            }

            String[] formalParams = defParts[1].split(",");
            String[] actualArgs = Arrays.copyOfRange(callTokens, 1, callTokens.length);

            Map<String, String> ALA = new HashMap<>();
            for (int i = 0; i < formalParams.length; i++) {
                ALA.put(formalParams[i].trim(), actualArgs[i].trim());
            }

            // Expand lines from MDT
            for (int i = mdtIndex + 1; i < MDT.size(); i++) {
                String macroLine = MDT.get(i);
                if (macroLine.equalsIgnoreCase("MEND")) break;

                for (String param : ALA.keySet()) {
                    macroLine = macroLine.replace(param, ALA.get(param));
                }
                System.out.println(macroLine);
            }
        }

        // Display tables
        public void displayTables() {
            System.out.println("\n--- Macro Name Table (MNT) ---");
            for (Map.Entry<String, Integer> entry : MNT.entrySet()) {
                System.out.println(entry.getKey() + " -> " + entry.getValue());
            }

            System.out.println("\n--- Macro Definition Table (MDT) ---");
            for (int i = 0; i < MDT.size(); i++) {
                System.out.println(i + ": " + MDT.get(i));
            }

            System.out.println("\n--- Intermediate Code ---");
            for (String line : intermediateCode) {
                System.out.println(line);
            }
        }
    }

    // Main driver function
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        MacroProcessor processor = new MacroProcessor();

        System.out.println("Enter the source code line by line. Type 'END_OF_CODE' to finish input:");
        List<String> inputLines = new ArrayList<>();

        while (true) {
            String line = sc.nextLine();
            if (line.equalsIgnoreCase("END_OF_CODE")) break;
            inputLines.add(line);
        }

        // Perform Pass 1
        processor.pass1(inputLines);

        // Display MNT, MDT, and Intermediate Code
        processor.displayTables();

        // Perform Pass 2
        processor.pass2();

        System.out.println("\n=== Code Execution Successful ===");
    }
}

/*
MACRO          
INCR &ARG1
ADD &ARG, =1
MEND
START
INCR A
END
END_OF_CODE
*/
