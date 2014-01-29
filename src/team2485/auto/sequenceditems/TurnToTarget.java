package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;
import team2485.comp.TargetTracker;

/**
 * Turn to face the target detected by the {@code TargetTracker}, or right by default.
 * @author Bryce Matsumori
 */
public class TurnToTarget implements SequencedItem {
    private boolean completed = false;
    private int trackState = -1; // save it so we don't change directions after starting

    public void run() {
        if (trackState == -1) trackState = Robot.tracker.getTrackState();

        if (trackState == TargetTracker.TRACK_LEFT) {
            completed = Robot.drive.rotateTo(-20);
        }
        else { // TRACK_RIGHT or default
            completed = Robot.drive.rotateTo(-20);
        }
    }

    public double duration() {
        return completed ? 0 : 5;
    }
}
