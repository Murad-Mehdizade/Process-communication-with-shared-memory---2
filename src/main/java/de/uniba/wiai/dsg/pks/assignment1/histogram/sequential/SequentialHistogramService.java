package de.uniba.wiai.dsg.pks.assignment1.histogram.sequential;

import de.uniba.wiai.dsg.pks.assignment.model.Histogram;
import de.uniba.wiai.dsg.pks.assignment.model.HistogramService;
import de.uniba.wiai.dsg.pks.assignment.model.HistogramServiceException;
import net.jcip.annotations.ThreadSafe;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static de.uniba.wiai.dsg.pks.assignment1.shared.HistogramServiceHelper.aggregateDistribution;
import static de.uniba.wiai.dsg.pks.assignment1.shared.HistogramServiceHelper.computeDistribution;

@ThreadSafe
public class SequentialHistogramService implements HistogramService {

    public SequentialHistogramService() {
        // REQUIRED FOR GRADING - DO NOT REMOVE DEFAULT CONSTRUCTOR
        // but you can add code below
    }

    @Override
    public Histogram calculateHistogram(String rootDirectory, String fileExtension) throws HistogramServiceException {
        Histogram histogram = new Histogram();
        return calculateRecursiveHistogram(rootDirectory, fileExtension, histogram);
    }

    /**
     * Recursive helper method to calculate our histogram
     *
     * @param rootDirectory the directory the method works on
     * @param fileExtension the file extension the method is looking for
     * @param histogram     the histogram which is passed from the previous recursion step
     * @return the calculated histogram
     * @throws HistogramServiceException wraps up occurring exceptions
     */
    private Histogram calculateRecursiveHistogram(String rootDirectory, String fileExtension, Histogram histogram)
            throws HistogramServiceException {

        Path path = Path.of(rootDirectory);
        histogram.setDirectories(histogram.getDirectories() + 1);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
                if (!Thread.currentThread().isInterrupted()) {
                    if (Files.isRegularFile(entry)) {
                        histogram.setFiles(histogram.getFiles() + 1);
                        if (entry.getFileName().toString().endsWith(fileExtension)) {
                            List<String> lines = Files.readAllLines(entry, StandardCharsets.UTF_8);
                            histogram.setDistribution(aggregateDistribution(histogram.getDistribution(),
                                    computeDistribution(lines)));
                            histogram.setLines(histogram.getLines() + lines.size());
                            histogram.setProcessedFiles(histogram.getProcessedFiles() + 1);
                        }
                        System.out.printf("File: \t\t%s finished!%n", entry.toString());
                    } else if (Files.isDirectory(entry)) {
                        this.calculateRecursiveHistogram(entry.toString(), fileExtension, histogram);
                        System.out.printf(
                                "Directory: \t%s finished!%n [distr: %s, lines=%d, files=%d, processedFiles=%d, " +
                                        "directories=%d]%n", entry.toString(),
                                Arrays.toString(histogram.getDistribution()), histogram.getLines(),
                                histogram.getFiles(),
                                histogram.getProcessedFiles(), histogram.getDirectories());
                    }
                } else {
                    throw new HistogramServiceException();
                }
            }
        } catch (IOException e) {
            throw new HistogramServiceException(e);
        }
        return histogram;
    }

    @Override
    public String toString() {
        return "SequentialHistogramService";
    }

}
