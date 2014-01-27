package team2485.util;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.Timer;

/**
 * Averages inconsistent Encoder input over time for smoother output.
 * Internally, the wrapper maintains a circular buffer containing the last encoder input.
 *
 * @author W.A.R. Lords
 */
public class EncoderWrapper implements PIDSource {
    private Encoder encoder;

    private double[] counts;
    private double[] times;

    private int currIndex = 0;
    private double lastCount = 0, thisCount = 0;
    private double lastTime = 0, thisTime = 1;
    private double averageGet;

    /**
     * Constructs a new Encoder wrapper.
     * @param enc The encoder to wrap.
     * @param ringSize The number of values to average.
     */
    public EncoderWrapper(Encoder enc, int ringSize) {
        encoder = enc;
        encoder.setPIDSourceParameter(Encoder.PIDSourceParameter.kRate);
        encoder.setDistancePerPulse(1);

        counts = new double[ringSize];
        times  = new double[ringSize];

        encoder.start();
        thisTime = Timer.getFPGATimestamp();
    }

    /**
     * Gets the last {@code pidGet()} output.
     * @return The last output.
     */
    public double getLastOutput() {
        return averageGet;
    }

    /**
     * Gets the averaged output.
     * @return The result to use in a PIDController.
     * @see PIDSource
     */
    public double pidGet() {
        thisTime = Timer.getFPGATimestamp();
        thisCount = encoder.getRaw();

        counts[currIndex] = thisCount - lastCount;
         times[currIndex] = thisTime - lastTime;

        lastTime = thisTime;
        lastCount = thisCount;

        double countDiffSum = 0, timeDiffSum = 0;
        for (int i = 0; i < counts.length; i++) {
            countDiffSum += counts[currIndex];
             timeDiffSum +=  times[currIndex];
        }
        averageGet = countDiffSum / timeDiffSum;

        // convert to rpm's by multiplying by 60 seconds, then dividing by 360*4 counts
        averageGet /= (24.0);

        currIndex++;
        if (currIndex > counts.length - 1) currIndex = 0;

        return averageGet;
    }
}
