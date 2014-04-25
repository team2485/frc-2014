package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;
import team2485.comp.Catapult;

/**
 * Extends the shoe piston
 * @author Marty
 */
public class FullyExtendShoe implements SequencedItem {
    private boolean initialCheck = true;
    private double duration;

    public void run() {
        if (initialCheck) {
            duration = Robot.catapult.getShoeState() == Catapult.FULLY_EXTENDED ? 0 : 0.55;
            initialCheck = false;
        }

        Robot.catapult.extendShoeFull();
    }

    public double duration() {
       if (Robot.errorInAutonomous) return 0;
        return duration;
    }
}
