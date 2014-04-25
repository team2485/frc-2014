package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 * Sequenced Item specialized for drive
 *
 * @author Marty Kausas
 */
public class Drive implements SequencedItem {
    private final double distance;
    private boolean done = false;

    /**
     * Drive forward the specified distance, in inches.
     *
     * @param inches The distance.
     */
    public Drive(double inches) {
        distance = inches;
    }

    public void run() {
        done = Robot.drive.driveTo(distance);
    }

    public double duration() {
        return done ? 0 : 2.0;
    }
}
