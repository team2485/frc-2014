package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;
import team2485.comp.TargetTracker;

/**
 *
 * @author W.A.R.Lords
 */
public class TurnToZero implements SequencedItem {
    private boolean completed = false;

    public void run() {
        completed = Robot.drive.rotateToZero();
    }

    public double duration() {
        // TODO: Find correct time
        return completed ? 0.0 : 5.0;
    }
}
