package de.uniba.wiai.dsg.pks.assignment1.histogram.threaded.highlevel;

import de.uniba.wiai.dsg.pks.assignment.model.Histogram;
import de.uniba.wiai.dsg.pks.assignment.model.HistogramService;
import de.uniba.wiai.dsg.pks.assignment.model.HistogramServiceException;
import de.uniba.wiai.dsg.pks.assignment1.histogram.shared.OutputThread;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class HighlevelHistogramService implements HistogramService {

	public HighlevelHistogramService() {
		// REQUIRED FOR GRADING - DO NOT REMOVE DEFAULT CONSTRUCTOR
		// but you can add code below
	}

	@Override
	public Histogram calculateHistogram(String rootDirectory, String fileExtension) throws HistogramServiceException {

		// Empty old results
		Histogram histogram = new Histogram();

		// Create controller
		HighlevelControllerThread controller = new HighlevelControllerThread(rootDirectory, fileExtension, histogram);

		// start controller
		controller.start();

		// join controller
		try {
			controller.join();
		} catch (InterruptedException e) {
			OutputThread.getInstance().interrupt();
			controller.interrupt();
			throw new HistogramServiceException();
		}

		OutputThread.getInstance().interrupt();

		return histogram;
	}

	@Override
	public String toString() {
		return "HighlevelHistogramService";
	}

}
