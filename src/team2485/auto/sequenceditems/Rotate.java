package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 * Sequenced Item specialized for rotation
 *
 * @author Marty Kausas
 */
public class Rotate implements SequencedItem {

    private boolean done = false;
    private double angle;

    /**
     * Rotate to the specified angle, in degrees.
     *
     * @param angle The angle.
     */
    public Rotate(double angle) {
        this.angle = angle;
    }

    public void run() {
        done = Robot.drive.rotateTo(angle);
    }

    public double duration() {
        return done ? 0 : Double.MAX_VALUE;
    }
}
