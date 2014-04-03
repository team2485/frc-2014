package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 *
 * @author Camille Considine
 */
public class SetLowGear implements SequencedItem {

    private boolean hasRun;
    
    public void run() {
        if (!hasRun) {
            Robot.drive.lowGear();
            hasRun = true;
        }
    }

    public double duration() {
        return hasRun ? 0 : 1;
    }


}

