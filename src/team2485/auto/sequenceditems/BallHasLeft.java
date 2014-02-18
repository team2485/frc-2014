package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 *
 * @author W.A.R.Lords
 */
public class BallHasLeft implements SequencedItem {
//    public static boolean hasBall = false;

    public void run() {
//       hasBall = Robot.catapult.inCatapult();
//       if(hasBall)
//           Robot.errorInAutonomous = true;
    }

    public double duration() {
//        return 0.05;
        return Robot.catapult.inCatapult() ? Integer.MAX_VALUE : 0;
    }

}
