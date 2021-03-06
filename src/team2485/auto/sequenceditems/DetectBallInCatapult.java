package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 *
 * @author W.A.R.Lords
 */
public class DetectBallInCatapult implements SequencedItem {
    private boolean done = false;

    public void run() {
       done = Robot.catapult.inCatapult();
    }

    public double duration() {
        return done ? 0 : 2.5;
    }
}
