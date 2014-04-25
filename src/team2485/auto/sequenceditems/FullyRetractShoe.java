package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;
import team2485.comp.Catapult;

/**
 * Retracts the shoe piston
 * @author Marty
 */
public class FullyRetractShoe implements SequencedItem {
    private boolean initialCheck = true;
    private double duration;

    public void run() {
        if (initialCheck) {
            duration = Robot.catapult.getShoeState() == Catapult.FULLY_RETRACTED ? 0 : 0.2;
            initialCheck = false;
        }

        Robot.catapult.retractShoeFull();
    }

    public double duration() {
        if(Robot.errorInAutonomous)
            return 0;

        return duration;
    }
}
