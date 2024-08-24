package com.example.braille_sync.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrailleConverter {
    // Step 1: Create a Braille binary map
    private static final Map<Character, String> brailleMap = new HashMap<>();

    static {
        brailleMap.put('a', "100000");
        brailleMap.put('b', "101000");
        brailleMap.put('c', "110000");
        brailleMap.put('d', "110100");
        brailleMap.put('e', "100100");
        // ... Add the rest of the characters and symbols
    }

    // Method to convert text to Braille binary
    public static List<String> convertToBrailleBinary(String text, int width, int height) {
        List<String> brailleBinary = new ArrayList<>();

        // Step 2: Convert each character in the text to Braille binary
        for (char c : text.toCharArray()) {
            if (brailleMap.containsKey(c)) {
                brailleBinary.add(brailleMap.get(c));
            } else {
                // Handle characters not in the map
                brailleBinary.add("000000"); // Empty or undefined Braille pattern
            }
        }

        // Step 3: Format the output to fit within the page dimensions and store each line as a string
        List<String> outputLines = new ArrayList<>();
        int rowCount = 0;

        for (int i = 0; i < brailleBinary.size(); i += width) {
            for (int line = 0; line < 3; line++) { // Each Braille character takes up 3 lines
                StringBuilder lineBuilder = new StringBuilder();

                for (int j = 0; j < width && (i + j) < brailleBinary.size(); j++) {
                    String brailleChar = brailleBinary.get(i + j);
                    lineBuilder.append(brailleChar.substring(line * 2, (line * 2) + 2)).append(" ");
                }

                outputLines.add(lineBuilder.toString());
                rowCount++;

                if (rowCount >= height) {
                    break;
                }
            }
            if (rowCount >= height) {
                break;
            }
        }

        return outputLines;
    }

}
