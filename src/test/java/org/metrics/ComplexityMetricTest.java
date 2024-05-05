package org.metrics;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

class ComplexityMetricTest {

    private CompilationUnit cu;
    private static final String path = "src/test/java/org/metrics/example/";

    @BeforeEach
    void setUp() throws Exception {
        // Tests for Calculator Only
        cu = StaticJavaParser.parse(Paths.get(path + "ExampleComplexity.java"));
    }

    @Test
    void testSimpleMethodComplexity() {
        testMethodComplexity("simpleMethod", 1);
    }

    @Test
    void testConditionalMethodComplexity() {
        testMethodComplexity("conditionalMethod", 2); // 1 'if-else' contributes 2 paths
    }

    @Test
    void testMultipleReturnsComplexity() {
        // 'switch' with 3 cases and a default contributes 4 paths, but these paths are independent due to input
        // Summing up, 3 additional branches (3 cases), 4 returns: decisionPoint - exitPoint + 2 = 1
        testMethodComplexity("multipleReturns", 1);
    }

    @Test
    void testLoopMethodComplexity() {
        testMethodComplexity("loopMethod", 2); // 1 'for' loop contributes 2 paths
    }

    @Test
    void testSwitchMethodComplexity() {
        testMethodComplexity("switchMethod", 3); // 'switch' with 2 cases and a default contributes 3 paths
    }

    @Test
    void testComplexMethodComplexity() {
        testMethodComplexity("complexMethod", 5); // Two nested loops, try-catch, and finally contribute 5 paths
    }

    private void testMethodComplexity(String methodName, int expectedComplexity) {
        MethodDeclaration method = cu.getClassByName("ExampleComplexity")
                .orElseThrow(() -> new AssertionError("Class not found"))
                .getMethodsByName(methodName)
                .stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("Method not found: " + methodName));

        int actualComplexity = ComplexityMetric.CyclomaticComplexityCalculator.calculate(method);
        assertEquals(expectedComplexity, actualComplexity, "Complexity mismatch for method: " + methodName);
    }

    @Test
    void testEntireDirectory() {
        ComplexityMetric complexityChecker = new ComplexityMetric();
        try {
            complexityChecker.loadDirectory(path);
            String result = complexityChecker.getResults();
            assertTrue(result.contains("methodName=complexMethod, fileName=ExampleComplexity.java, complexity=5"));
            assertTrue(result.contains("methodName=switchMethod, fileName=ExampleComplexity.java, complexity=3"));
            assertTrue(result.contains("methodName=conditionalMethod, fileName=ExampleComplexity.java, complexity=2"));
        } catch (IOException e) {
            fail("Error while reading from directory");
        }

    }
}