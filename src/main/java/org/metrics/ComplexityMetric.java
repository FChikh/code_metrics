package org.metrics;

import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;

import java.io.IOException;
import java.util.*;

/**
 * This class checks the cyclomatic complexity of methods found within Java source files.
 */
public class ComplexityMetric extends FileReader {
    private final List<MethodComplexity> allMethods = new ArrayList<>();

    @Override
    public void loadDirectory(String directoryPath) throws IOException, IllegalArgumentException {
        allMethods.clear();
        super.loadDirectory(directoryPath);
    }

    protected void analyzeCompilationUnit(CompilationUnit cu, String relativeFilePath) {
        List<TypeDeclaration<?>> types = cu.getTypes();
        for (TypeDeclaration<?> type : types) {
            List<MethodDeclaration> methods = type.getMethods();
            for (MethodDeclaration method : methods) {
                int complexity = CyclomaticComplexityCalculator.calculate(method);
                allMethods.add(new MethodComplexity(method.getNameAsString(), relativeFilePath, complexity));
            }
        }
    }

    /**
     * Retrieves a summary of the top three methods with the highest cyclomatic complexity.
     *
     * @return A string summary of the most complex methods.
     */
    public String getResults() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nTop 3 methods with the Highest Complexity:\n");
        allMethods.stream()
                .sorted(Comparator.comparingInt(MethodComplexity::complexity).reversed())
                .limit(3)
                .forEach(sb::append);
        return sb.toString();
    }

    public List<MethodComplexity> getMethodComplexities() {
        return allMethods;
    }

    /**
     * Utility class to handle complexity calculations for a method.
     */
    static class CyclomaticComplexityCalculator {

        /**
         * Calculates the cyclomatic complexity for a given method.
         *
         * @param method The method to analyze.
         * @return The calculated cyclomatic complexity score.
         */
        public static int calculate(MethodDeclaration method) {
            CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
            method.accept(visitor, null);
            return visitor.getComplexity();
        }
    }

}
