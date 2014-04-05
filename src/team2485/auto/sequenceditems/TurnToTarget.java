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

    public void run() {
        completed = Robot.drive.rotateTo(
            Robot.tracker.getAutoTrackState() == TargetTracker.TRACK_LEFT ?
            -15 :
             15); // TRACK_RIGHT or default
    }

    public double duration() {
        return completed ? 0.0 : 2.5;
    }
}
