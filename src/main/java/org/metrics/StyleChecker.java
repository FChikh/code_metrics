package org.metrics;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Stream;

public class StyleChecker {
    // Pattern for accurately detecting method definitions
    private static final Pattern methodPattern = Pattern.compile("\\b(public|protected|private|static|final|synchronized|abstract|native)?\\s*" + // Optional common modifiers
            "\\b(?:void|boolean|byte|char|short|int|long|float|double|[A-Z][\\w<>]*)\\s+" + // Return types and classes
            "([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\([^)]*\\)\\s*(\\{|;)" // Method name, parameters, and either opening brace or semicolon
    );

    // Pattern to verify camelCase compliance
    private static final Pattern camelCasePattern = Pattern.compile("[a-z]+[a-zA-Z0-9]*");

    // Stores non-compliant methods for reporting
    private final List<String> nonCamelCaseMethods = new ArrayList<>();
    private int totalMethods = 0;

    /**
     * Analyzes all Java files in the specified directory for naming style violations.
     *
     * @param directoryPath The root directory to analyze.
     * @throws IOException If an error occurs while reading the files.
     */
    public void loadDirectory(String directoryPath) throws IOException {
        try (Stream<Path> entries = Files.walk(Paths.get(directoryPath))) {
            List<Path> javaFiles = entries.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".java")).toList();
            scanFiles(javaFiles);
        } catch (IOException e) {
            System.err.println("Error walking directory: " + directoryPath + "; " + e.getMessage());
        }
    }

    /**
     * Analyzes a list of Java files for method naming convention compliance.
     *
     * @param javaFiles The list of Java files to analyze.
     */
    private void scanFiles(List<Path> javaFiles) {
        for (Path file : javaFiles) {
            try {
                analyzeFile(file);
            } catch (IOException e) {
                System.err.printf("Error reading file: %s; %s%n", file, e.getMessage());
            }
        }
    }

    /**
     * Analyzes a single Java file for method naming convention compliance.
     *
     * @param file The Java file to analyze.
     * @throws IOException If an error occurs while reading the file.
     */
    private void analyzeFile(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);

        for (String line : lines) {
            Matcher matcher = methodPattern.matcher(line);
            if (matcher.find()) {
                String methodName = matcher.group(2);
                totalMethods++;
                if (!isCamelCase(methodName)) {
                    nonCamelCaseMethods.add(String.format("Non-camelCase method: %s in file: %s", methodName, file));
                }
            }
        }
    }

    /**
     * Checks whether a given method name follows camelCase naming conventions.
     *
     * @param name The method name to check.
     * @return `true` if the method name follows camelCase, otherwise `false`.
     */
    private boolean isCamelCase(String name) {
        return camelCasePattern.matcher(name).matches();
    }

    /**
     * Generates a string summary of analysis results, including the percentage of non-camelCase methods.
     *
     * @return A formatted summary of the results.
     */
    public String getResults() {
        StringBuilder results = new StringBuilder();

        if (totalMethods > 0) {
            double incorrectPercentage = (double) nonCamelCaseMethods.size() / totalMethods * 100;
            results.append(String.format("Non-camelCase method names: %.2f%% of all methods%n", incorrectPercentage));
        } else {
            results.append("No methods found in the provided directory.\n");
        }

        if (!nonCamelCaseMethods.isEmpty()) {
            results.append("Non-camelCase Methods:\n");
            nonCamelCaseMethods.forEach(method -> results.append(method).append("\n"));
        }

        return results.toString();
    }
}
