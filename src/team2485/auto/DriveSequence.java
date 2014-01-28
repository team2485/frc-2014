package team2485.auto;

import team2485.Robot;

/**
 * Sequenced Item specialized for drive
 *
 * @author Marty Kausas
 */
public class DriveSequence implements SequencedItem {

    private double distance;
    private boolean done = false;

    /**
     * Default constructor
     *
     * @param feet
     */
    public DriveSequence(double feet) {
        distance = feet;
    }

    public void run() {
        done = Robot.drive.driveTo(distance);
    }

    public double duration() {
        return done ? 0 : Double.MAX_VALUE;
    }

}
