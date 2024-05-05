package org.metrics;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class to load and analyze Java files from a specified directory.
 */
public abstract class FileReader {
    final List<String> failedFiles = new ArrayList<>();

    /**
     * Analyzes all Java files in the specified directory for naming style violations.
     *
     * @param directoryPath The root directory to analyze.
     * @throws IOException If an error occurs while reading the files.
     */
    public void loadDirectory(String directoryPath) throws IOException, IllegalArgumentException {
        Path rootPath = Paths.get(directoryPath).toAbsolutePath().normalize();

        // Check if the path is valid
        if (!Files.isDirectory(rootPath)) {
            throw new IllegalArgumentException("Provided path is not a valid directory: " + rootPath);
        }
        SourceRoot sourceRoot = new SourceRoot(rootPath);
        List<ParseResult<CompilationUnit>> results = sourceRoot.tryToParse("");

        for (ParseResult<CompilationUnit> result : results) {
            if (result.isSuccessful() && result.getResult().isPresent()) {
                CompilationUnit cu = result.getResult().get();
                String relativeFilePath = rootPath.relativize(cu.getStorage().get().getPath()).toString();
                analyzeCompilationUnit(cu, relativeFilePath);
            } else {
                failedFiles.add("Failed to parse: " + result.getProblems().toString());
            }
        }
    }

    /**
     * Analyzes a parsed compilation unit for specific information.
     *
     * @param cu The parsed compilation unit.
     * @param relativeFilePath The relative path of the Java file being analyzed.
     */
    protected abstract void analyzeCompilationUnit(CompilationUnit cu, String relativeFilePath);
}
