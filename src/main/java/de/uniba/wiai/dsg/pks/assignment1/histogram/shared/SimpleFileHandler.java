package de.uniba.wiai.dsg.pks.assignment1.histogram.shared;

import net.jcip.annotations.ThreadSafe;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@ThreadSafe
public class SimpleFileHandler {

    public static List<Path> getDirectoriesRecursively(Path path) {
        List<Path> directoryList = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path p : stream) {
                if (Files.isDirectory(p)) {
                    directoryList.add(p);
                    directoryList.addAll(getDirectoriesRecursively(p));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return directoryList;
    }

    public static List<Path> getRegularFiles(Path path) {
        List<Path> regularFilesList = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path p : stream) {
                if (Files.isRegularFile(p)) {
                    regularFilesList.add(p);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return regularFilesList;
    }

    public static List<String> getLines(Path path) throws IOException {
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

}
