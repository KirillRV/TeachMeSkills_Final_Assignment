package main.com.teachmeskills.final_assignment.fileparser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseParser<T> implements Parser<T> {

    @Override
    public Map<T, Path> parseFiles(List<Path> files) {
        Map<T, Path> parsedObjects = new HashMap<>();

        for (Path file : files) {
            try {
                List<String> lines = Files.readAllLines(file);
                lines.removeIf(String::isBlank);
                lines.replaceAll(String::trim);
                T parsedObject = analyzeTextFromFile(lines);
                parsedObjects.put(parsedObject, file);
            } catch (IOException e) {
                System.err.println("Error during file reading: " + file.getFileName());
            }
        }

        return parsedObjects;
    }

    protected abstract T analyzeTextFromFile(List<String> lines);
}

