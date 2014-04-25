package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

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
        return completed ? 0.0 : 1.0;
    }
}
