package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 *
 * @author Marty Kausas
 */
public class SetHighGear implements SequencedItem {

    private boolean hasRun;
    public void run() {
        if (!hasRun) {
            Robot.drive.highGear();
            hasRun = true;
        }
    }

    public double duration() {
        return hasRun ? 0 : 0.5;
    }


}
