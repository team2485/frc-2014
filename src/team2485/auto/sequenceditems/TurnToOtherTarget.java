package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;
import team2485.comp.TargetTracker;

/**
 * Turn to face the other target detected by the {@code TargetTracker}, or right by default.
 * @author Bryce Matsumori
 */
public class TurnToOtherTarget implements SequencedItem {
    private boolean completed = false;

    public void run() {
        completed = Robot.drive.rotateTo(
            Robot.tracker.getAutoTrackState() == TargetTracker.TRACK_LEFT ?
             8 :
            -8); // TRACK_RIGHT or default
        System.out.println("Turning to the other target");
    }

    public double duration() {
        return completed ? 0 : 5;
    }
}
