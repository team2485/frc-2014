package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 *
 * @author W.A.R.Lords
 */
public class DisableArmPID implements SequencedItem {
    private boolean hasRun = false;

    public void run() {
        Robot.arm.disableArmPID();
        hasRun = true;
    }

    public double duration() {
        return hasRun ? 0 : 1;
    }
}
