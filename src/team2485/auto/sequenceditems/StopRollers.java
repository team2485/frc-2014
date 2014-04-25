package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 *
 * @author W.A.R.Lords
 */
public class StopRollers implements SequencedItem {
    private boolean hasRun = false;

    public void run() {
        hasRun = true;
        Robot.arm.stopRollers();
    }

    public double duration() {
        if (Robot.errorInAutonomous) return 0;
        return hasRun ? 0 : Integer.MAX_VALUE;
    }
}
