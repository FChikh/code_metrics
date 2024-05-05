package org.metrics;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Stream;

public class CyclomaticComplexityChecker {
    // Pattern to detect function definitions
    private static final Pattern methodPattern = Pattern.compile(
            "\\b(public|protected|private|static|final|synchronized|abstract|native)?\\s*" + // Optional common modifiers
                    "\\b(?:void|boolean|byte|char|short|int|long|float|double|[A-Z][\\w<>]*)\\s+" + // Return types and classes
                    "([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\([^)]*\\)\\s*(\\{|;)" // Method name, parameters, and either opening brace or semicolon
    );
    private static final Pattern classPattern = Pattern.compile("class\\s+([a-zA-Z_][a-zA-Z0-9_]*)");
    // Patterns to detect decision points that increase the cyclomatic complexity
    private static final Pattern decisionPattern = Pattern.compile("\\b(if|else if|for|while|case|catch|do)\\b");

    // Pattern to identify JavaDoc comments
    private static final Pattern javaDocStartPattern = Pattern.compile("^\\s*/\\*\\*");
    private static final Pattern javaDocEndPattern = Pattern.compile("\\*/");
    private static final Pattern singleLineCommentPattern = Pattern.compile("//");

    private final List<MethodComplexity> allMethods = new ArrayList<>();
    private boolean inMultiLineComment = false;

    public void loadDirectory(String directoryPath) throws IOException {
        try (Stream<Path> entries = Files.walk(Paths.get(directoryPath))
        ) {
            List<Path> javaFiles = entries.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .toList();
            scanFiles(javaFiles);
        } catch (IOException e) {
            System.err.println("Error walking directory: " + directoryPath + "; " + e.getMessage());
        }
    }

    public void scanFiles(List<Path> javaFiles) {
        for (Path file : javaFiles) {
            try {
                analyzeFile(file);
            } catch (IOException e) {
                System.err.println("Error reading file: " + file + "; " + e.getMessage());
            }
        }
    }

    private void analyzeFile(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        String currentMethod = null;
        int decisionPoints = 0;
        int exitPoints = 1;

        for (String line : lines) {
            // Ignore comments

            if (skipCommentLines(line)) {
                continue;
            }

            Matcher classMatcher = classPattern.matcher(line);
            Matcher methodMatcher = methodPattern.matcher(line);

            if (classMatcher.find()) {
                // Ignore for now but confirms that we're processing a class
                continue;
            } else if (methodMatcher.find()) {
                // Process the current method if we're switching to a new method
                if (currentMethod != null) {
                    int complexity = calculateComplexity(decisionPoints, exitPoints);
                    allMethods.add(new MethodComplexity(currentMethod, file.getFileName().toString(), complexity));
                }
                // Set up the new method
                currentMethod = methodMatcher.group(2); // Method name
                decisionPoints = 0;
                exitPoints = 1;
            } else {
                // Count decision points
                Matcher decisionMatcher = decisionPattern.matcher(line);
                while (decisionMatcher.find()) {
                    decisionPoints++;
                }


            }
        }

        // Add final method if it exists
        if (currentMethod != null) {
            int complexity = calculateComplexity(decisionPoints, exitPoints);
            allMethods.add(new MethodComplexity(currentMethod, file.getFileName().toString(), complexity));
        }
    }

    // Method to skip comment lines
    private boolean skipCommentLines(String line) {
        if (inMultiLineComment) {
            if (line.contains("*/")) {
                inMultiLineComment = false;
            }
            return true;
        }

        if (line.startsWith("//") || line.startsWith("/*") || line.startsWith("*")) {
            if (line.startsWith("/*") && !line.contains("*/")) {
                inMultiLineComment = true;
            }
            return true;
        }

        return false;
    }

    private int calculateComplexity(int decisionPoints, int exitPoints) {
        return decisionPoints - exitPoints + 2;
    }

    public String getTopComplexMethods() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nTop 3 Methods with the Highest Cyclomatic Complexity:\n");
        allMethods.stream()
                .sorted(Comparator.comparingInt(MethodComplexity::complexity).reversed())
                .limit(3)
                .forEach(method ->
                        sb.append(String.format("Method '%s' in file '%s' - Complexity: %d%n",
                                method.methodName(), method.fileName(), method.complexity())));
        return sb.toString();
    }

    // Helper class to store method details
    private record MethodComplexity(String methodName, String fileName, int complexity) {
    }
}
