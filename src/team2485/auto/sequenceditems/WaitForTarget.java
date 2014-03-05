package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;
import team2485.comp.TargetTracker;

/**
 * Wait for the {@code TargetTracker} to detect targets.
 * @author Bryce Matsumori
 */
public class WaitForTarget implements SequencedItem {
    private boolean found = false;

    public void run() {
        found = Robot.tracker.isConnected() && Robot.tracker.getTrackState() != TargetTracker.TRACK_NONE;

        if (found) Robot.tracker.setAutoTrackState();

        System.out.println("found in wait for target detection = " + found + " tracker auto state " + Robot.tracker.getAutoTrackState());
    }

    public double duration() {
        return found ? 0.0 : 0.4; // maximum seconds before timing out
    }
}
