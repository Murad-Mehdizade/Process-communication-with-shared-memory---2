package de.uniba.wiai.dsg.pks.assignment1.histogram.shared;

import de.uniba.wiai.dsg.pks.assignment.model.Histogram;
import net.jcip.annotations.Immutable;

import java.util.List;

@Immutable
public class HistogramServiceHelper {

    /**
     * Calculates the distribution for the given lines with the help of the characters ascii codes to sort them into
     * the right bin
     *
     * @param lines the lines which the distribution should be calculated for
     * @return the calculated distribution
     */
    public static long[] computeDistribution(List<String> lines) {
        long[] distribution = new long[26];

        for (String line : lines) {
            for (char character : line.toCharArray()) {
                int ascii = character - 97;
                if (ascii >= 0 && ascii < 26) {
                    distribution[ascii]++;
                } else {
                    ascii = character - 65;
                    if (ascii >= 0 && ascii < 26) {
                        distribution[ascii]++;
                    }
                }
            }
        }
        return distribution;
    }

    /**
     * Aggregates the data of the passed histograms into the first one
     *
     * @param aggregationHistogram the histogram where the aggregated data will be stored
     * @param partialHistogram     the histogram which will be added to the
     */
    public static void aggregateHistograms(Histogram aggregationHistogram, Histogram partialHistogram) {

        aggregationHistogram.setDirectories(aggregationHistogram.getDirectories() + partialHistogram.getDirectories());
        aggregationHistogram.setFiles(aggregationHistogram.getFiles() + partialHistogram.getFiles());
        aggregationHistogram.setProcessedFiles(
                aggregationHistogram.getProcessedFiles() + partialHistogram.getProcessedFiles());
        aggregationHistogram.setLines(aggregationHistogram.getLines() + partialHistogram.getLines());
        aggregationHistogram.setDistribution(
                aggregateDistribution(aggregationHistogram.getDistribution(), partialHistogram.getDistribution()));

    }

    /**
     * Aggregates two distributions into a new one
     *
     * @param dist1 first distribution
     * @param dist2 second distribution
     * @return the aggregated distribution
     */
    public static long[] aggregateDistribution(long[] dist1, long[] dist2) {
        long[] newDist = new long[26];
        for (int i = 0; i < 26; i++) {
            newDist[i] = dist1[i] + dist2[i];
        }
        return newDist;
    }

}
