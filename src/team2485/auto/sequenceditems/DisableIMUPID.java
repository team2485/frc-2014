package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 *
 * @author MK
 */
public class DisableIMUPID implements SequencedItem {

    private boolean hasRun;

    public void run() {
        Robot.drive.disableIMUPID();
        hasRun = true;
    }

    public double duration() {
        return hasRun ? 0 : 1;
    }

}
