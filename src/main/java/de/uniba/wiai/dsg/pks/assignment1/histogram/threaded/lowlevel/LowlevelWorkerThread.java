package de.uniba.wiai.dsg.pks.assignment1.histogram.threaded.lowlevel;

import de.uniba.wiai.dsg.pks.assignment.model.Histogram;
import de.uniba.wiai.dsg.pks.assignment1.histogram.shared.OutputThread;
import de.uniba.wiai.dsg.pks.assignment1.histogram.shared.OutputThread;
import de.uniba.wiai.dsg.pks.assignment1.shared.SimpleFileHandler;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static de.uniba.wiai.dsg.pks.assignment1.shared.HistogramServiceHelper.aggregateDistribution;
import static de.uniba.wiai.dsg.pks.assignment1.shared.HistogramServiceHelper.computeDistribution;

@ThreadSafe
public class LowlevelWorkerThread extends Thread {

    private final Path path;
    private final String fileExtension;
    @GuardedBy(value = "itself")
    private final LowlevelLinkedBlockingQueue<Histogram> histograms;
    @GuardedBy(value = "itself")
    private final OutputThread out;

    public LowlevelWorkerThread(Path path, String fileExtension, LowlevelLinkedBlockingQueue<Histogram> histograms) {
        this.path = path;
        this.fileExtension = fileExtension;
        this.histograms = histograms;
        this.out = OutputThread.getInstance();
    }

    @Override
    public void run() {

        long[] distribution = new long[26];
        long lines = 0;
        long files = 0;
        long processedFiles = 0;

        List<Path> fileList = SimpleFileHandler.getRegularFiles(path);

        for (Path file : fileList) {
            if (!Thread.currentThread().isInterrupted()) {
                files++;
                if (file.getFileName().toString().endsWith(fileExtension)) {
                    try {
                        List<String> lineList = SimpleFileHandler.getLines(file);
                        distribution = aggregateDistribution(distribution, computeDistribution(lineList));
                        lines += lineList.size();
                        processedFiles++;
                    } catch (IOException e) {
                        //swallow to jump to the next file and calculate & accumulate all valid documents
                        continue;
                    }
                }
            } else {
                return;
            }
            out.print("File: \t\t" + file.toString() + " finished!");
        }

        out.print("Directory: \t" + path.toString() + " finished!\n [distr: " +
                Arrays.toString(distribution) + ", lines=" + lines + ", files=" +
                files + ", processedFiles=" + processedFiles + ", directories=1]");

        Histogram histogram = new Histogram();
        histogram.setDistribution(distribution);
        histogram.setLines(lines);
        histogram.setFiles(files);
        histogram.setProcessedFiles(processedFiles);
        histogram.setDirectories(1);
        try {
            histograms.put(histogram);
        } catch (InterruptedException e) {
            histograms.add(histogram);
        }
    }
}

