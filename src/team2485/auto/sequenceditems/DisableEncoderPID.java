package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 *
 * @author Marty Kausas
 */
public class DisableEncoderPID implements SequencedItem {
    private boolean hasRun;

    public void run() {
        Robot.drive.disableEncoderPID();
        hasRun = true;
    }

    public double duration() {
        return hasRun ? 0 : 1;
    }

}
