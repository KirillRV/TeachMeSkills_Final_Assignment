package main.com.teachmeskills.final_assignment.fileparser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseParser<T> implements Parser<T> {

    @Override
    public List<T> parseFiles(List<Path> files) {
        List<T> parsedObjects = new ArrayList<>();

        for (Path file : files) {
            try {
                List<String> lines = Files.readAllLines(file);
                lines.removeIf(String::isBlank);
                lines.replaceAll(String::trim);
                T parsedObject = analyzeTextFromFile(lines);
                parsedObjects.add(parsedObject);
            } catch (IOException e) {
                System.err.println("Error during file reading: " + file.getFileName());
            }
        }

        return parsedObjects;
    }

    protected abstract T analyzeTextFromFile(List<String> lines);
}

