package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 * Sequenced Item specialized for rotation
 *
 * @author Marty Kausas
 */
public class RotateSequence implements SequencedItem {

    private boolean done = false;
    private double angle;

    /**
     * Default Constructor
     *
     * @param angle
     */
    public RotateSequence(double angle) {
        this.angle = angle;
    }

    public void run() {
        done = Robot.drive.rotateTo(angle);
    }

    public double duration() {
        return done ? 0 : Double.MAX_VALUE;
    }
}
