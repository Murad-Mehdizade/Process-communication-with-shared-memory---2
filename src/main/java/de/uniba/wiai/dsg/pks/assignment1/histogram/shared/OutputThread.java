package de.uniba.wiai.dsg.pks.assignment1.histogram.shared;

import de.uniba.wiai.dsg.pks.assignment1.histogram.threaded.lowlevel.LowlevelLinkedBlockingQueue;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class OutputThread extends Thread {

    @GuardedBy(value = "itself")
    private static final LowlevelLinkedBlockingQueue<String> outputQueue = new LowlevelLinkedBlockingQueue<>();
    @GuardedBy(value = "itself")
    private static OutputThread outputThread = new OutputThread();

    private OutputThread() { }

    public static OutputThread getInstance() {
        return outputThread;
    }

    public void print(String message) {
        try {
            outputQueue.put(message);
        } catch (InterruptedException e) {
            // shutdown
            interrupt();
            outputThread = new OutputThread();
        }
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                System.out.println(outputQueue.take());
            } catch (InterruptedException e) {
                // shutdown
                interrupt();
                outputThread = new OutputThread();
            }
        }
        outputThread = new OutputThread();
    }

}
