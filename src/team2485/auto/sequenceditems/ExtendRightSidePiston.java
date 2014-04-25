package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 *
 * @author W.A.R.Lords
 */
public class ExtendRightSidePiston implements SequencedItem {

    public void run() {
        Robot.catapult.extendRightPiston();
    }

    public double duration() {
        if (Robot.errorInAutonomous) return 0;
        return 0.5;
    }
}
