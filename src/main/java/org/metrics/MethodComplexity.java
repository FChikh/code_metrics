package org.metrics;

/**
 * Represents the complexity details of a method, including the name, file location, and complexity score.
 */
public final class MethodComplexity {
    private final String methodName;
    private final String fileName;
    private final int complexity;

    /**
     * Constructs a new `MethodComplexity` instance.
     *
     * @param methodName The name of the method.
     * @param fileName The file where the method is located.
     * @param complexity The calculated complexity score for the method.
     */
    MethodComplexity(String methodName, String fileName, int complexity) {
        this.methodName = methodName;
        this.fileName = fileName;
        this.complexity = complexity;
    }

    @Override
    public String toString() {
        return "methodName=" + methodName + ", " +
                "fileName=" + fileName + ", " +
                "complexity=" + complexity + '\n';
    }

    public String methodName() {
        return methodName;
    }

    public String fileName() {
        return fileName;
    }

    public int complexity() {
        return complexity;
    }
}