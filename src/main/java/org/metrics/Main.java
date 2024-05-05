package org.metrics;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide the directory path as an argument");
            System.exit(1);
        }
        String directoryPath = args[0];

        CyclomaticComplexityChecker complexityChecker = new CyclomaticComplexityChecker();
        StyleChecker styleChecker = new StyleChecker();

        try {
            System.out.println("\nAnalyzing cyclomatic complexity:");
            complexityChecker.loadDirectory(directoryPath);
            System.out.println(complexityChecker.getTopComplexMethods());
            System.out.println("\nChecking code style:");
            styleChecker.loadDirectory(directoryPath);
            System.out.println(styleChecker.getResults());
        } catch (IOException e) {
            System.err.println("Error processing files: " + e.getMessage());
            System.exit(1);
        }
    }
}