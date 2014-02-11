package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;
import team2485.comp.TargetTracker;

/**
 * Wait for the {@code TargetTracker} to detect targets.
 * @author Bryce Matsumori
 */
public class WaitForTargets implements SequencedItem {
    private boolean found = false;

    public void run() {
        found = Robot.tracker.isConnected() && Robot.tracker.getTrackState() != TargetTracker.TRACK_NONE;

        if (found) Robot.tracker.setAutoTrackState();
    }

    public double duration() {
        return found ? 0.0 : 3.0; // maximum seconds before timing out
    }
}
