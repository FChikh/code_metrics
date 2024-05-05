package org.metrics;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

import java.io.IOException;
import java.util.*;
import java.util.regex.*;

/**
 * This class analyzes Java files to check whether method names follow naming conventions.
 */
public class StyleChecker extends FileReader {
    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("[a-z]+[a-zA-Z0-9]*");
    private final List<String> nonCamelCaseMethods = new ArrayList<>();
    private int totalMethods = 0;

    @Override
    public void loadDirectory(String directoryPath) throws IOException, IllegalArgumentException {
        totalMethods = 0;
        nonCamelCaseMethods.clear();
        super.loadDirectory(directoryPath);
    }

    /**
     * Analyzes a compilation unit for method naming convention compliance.
     *
     * @param cu CompilationUnit to analyze.
     * @param relativeFilePath The relative path of the file being analyzed.
     */
    protected void analyzeCompilationUnit(CompilationUnit cu, String relativeFilePath) {
        List<TypeDeclaration<?>> types = cu.getTypes();
        for (TypeDeclaration<?> type : types) {
            List<MethodDeclaration> methods = type.getMethods();
            for (MethodDeclaration method : methods) {
                String methodName = method.getNameAsString();
                totalMethods++;
                if (!NamingConventionChecker.isCamelCase(methodName)) {
                    nonCamelCaseMethods.add(String.format("%s in file: %s", methodName, relativeFilePath));
                }
            }
        }
    }

    public int getTotalMethods() {
        return totalMethods;
    }

    public List<String> getNonCamelCaseMethods() {
        return nonCamelCaseMethods;
    }

    /**
     * Generates a string summary of analysis results, including the percentage of non-camelCase methods.
     *
     * @return A formatted string of the results.
     */
    public String getResults() {
        StringBuilder results = new StringBuilder();

        if (!failedFiles.isEmpty()) {
            results.append("Files that failed to parse:\n");
            failedFiles.forEach(file -> results.append(file).append("\n"));
        }

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

    /**
     * Utility class to handle naming convention checks.
     */
    static class NamingConventionChecker {
        private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("[a-z]+[a-zA-Z0-9]*");

        /**
         * Checks if a given name follows camelCase naming conventions.
         *
         * @param name The name to check.
         * @return `true` if the name follows camelCase, otherwise `false`.
         */
        public static boolean isCamelCase(String name) {
            return CAMEL_CASE_PATTERN.matcher(name).matches();
        }
    }
}
