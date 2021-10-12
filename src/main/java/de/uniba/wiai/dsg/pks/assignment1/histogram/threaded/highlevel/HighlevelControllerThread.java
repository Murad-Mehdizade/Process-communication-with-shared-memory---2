package de.uniba.wiai.dsg.pks.assignment1.histogram.threaded.highlevel;

import de.uniba.wiai.dsg.pks.assignment.model.Histogram;
import de.uniba.wiai.dsg.pks.assignment1.histogram.shared.OutputThread;
import de.uniba.wiai.dsg.pks.assignment1.shared.SimpleFileHandler;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import static de.uniba.wiai.dsg.pks.assignment1.shared.HistogramServiceHelper.aggregateHistograms;

@ThreadSafe
public class HighlevelControllerThread extends Thread {

    @GuardedBy(value = "itself")
    private final OutputThread out;
    @GuardedBy(value = "itself")
    private final LinkedBlockingQueue<Histogram> histograms = new LinkedBlockingQueue<>();
    private final String rootDirectory;
    private final String fileExtension;
    private final Histogram histogram;

    public HighlevelControllerThread(String rootDirectory, String fileExtension, Histogram histogram) {
        this.rootDirectory = rootDirectory;
        this.fileExtension = fileExtension;
        this.histogram = histogram;
        this.out = OutputThread.getInstance();
    }

    @Override
    public void run() {
        // Create local variables
        Path path = Path.of(rootDirectory);

        // Start output thread if necessary
        if (!out.isInterrupted()) {
            out.start();
        }

        // Traverse file tree and get a list of all found directories
        List<Path> directories = SimpleFileHandler.getDirectoriesRecursively(path);
        directories.add(path);

        // Start a new thread for every directory
        Thread[] threads = new Thread[directories.size()];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new HighlevelWorkerThread(directories.get(i), fileExtension, histograms);
            threads[i].start();
        }

        // Wait until every thread is ready
        for (Thread t : threads) {
            try {
                t.join();
                aggregateHistograms(histogram, histograms.take());
            } catch (InterruptedException e) {
                for (Thread toBeInterrupted : threads) {
                    toBeInterrupted.interrupt();
                }
            }
        }

    }

}
