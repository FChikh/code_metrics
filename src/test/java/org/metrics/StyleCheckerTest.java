package org.metrics;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class StyleCheckerTest {

    @Test
    void testIsCamelCase() {
        // Positive Cases
        assertTrue(StyleChecker.NamingConventionChecker.isCamelCase("simpleMethod"));
        assertTrue(StyleChecker.NamingConventionChecker.isCamelCase("calculateScore"));
        assertTrue(StyleChecker.NamingConventionChecker.isCamelCase("getValue1"));
        // Technically this is also a lowerCamelCase
        assertTrue(StyleChecker.NamingConventionChecker.isCamelCase("getvalue"));

        // Negative Cases
        assertFalse(StyleChecker.NamingConventionChecker.isCamelCase("SimpleMethod"));
        assertFalse(StyleChecker.NamingConventionChecker.isCamelCase("calculate_score"));
        assertFalse(StyleChecker.NamingConventionChecker.isCamelCase("GetValue"));
    }

    @Test
    void testGetResults() {
        StyleChecker checker = new StyleChecker();

        String sampleCode = "public class Example { public void exampleMethod() {} public void notCamel_case() {} }";
        CompilationUnit cu = StaticJavaParser.parse(sampleCode);

        checker.analyzeCompilationUnit(cu, "Example.java");
        assertTrue(checker.getNonCamelCaseMethods().contains("notCamel_case in file: Example.java"));
    }
}