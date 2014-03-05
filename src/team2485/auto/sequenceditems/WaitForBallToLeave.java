package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 *
 * @author W.A.R.Lords
 */
public class WaitForBallToLeave implements SequencedItem {

    public void run() {
    }

    public double duration() {
        return Robot.catapult.inCatapult() ? Integer.MAX_VALUE : 0;
    }

}
