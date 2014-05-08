package team2485.auto;

import java.util.Vector;
import team2485.util.Controllers;
import team2485.util.Controllers.ControllerDataDump;

/**
 * Records controller input.
 * @author Bryce Matsumori
 */
public class InputRecorder {
    private boolean running = false;
    private long interval   = 10;
    private final Vector data = new Vector();

    /**
     * Initializes a new input recorder with the specified interval.
     * @param interval the interval
     */
    public InputRecorder(int interval) {
        this.interval = interval;
    }

    /**
     * Starts the input recording thread. The thread runs until {@code stop()}
     * is executed.
     */
    public void start() {
        if (!running) {
            running = true;
            new InputRecorderThread().start();
        }
    }

    /**
     * Stops the input recording thread if it is running.
     */
    public void stop() {
        running = false;
    }

    /**
     * Gets the data recorded so far.
     * @return an array containing the data
     */
    public ControllerDataDump[] getData() {
        ControllerDataDump[] dumps = new ControllerDataDump[data.size()]; // lol
        data.copyInto(dumps);
        return dumps;
    }

    /**
     * Gets the recorded controller data at the specified snapshot index.
     * @param index the index
     * @return the controller input data
     */
    public ControllerDataDump getDumpAt(int index) {
        return (ControllerDataDump)data.elementAt(index);
    }

    /**
     * Gets the interval between data snapshots.
     * @return the interval, in milliseconds
     */
    public long getInterval() {
        return interval;
    }

    /**
     * Sets the interval between data snapshots.
     * @param value the interval, in milliseconds
     */
    public void setInterval(long value) {
        interval = value;
    }

    private class InputRecorderThread extends Thread {
        public InputRecorderThread() {
            super("Input Recorder Thread");
        }

        public void run() {
            while (running) {
                data.addElement(Controllers.getAllData());

                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) { }
            }
        }
    }
}
