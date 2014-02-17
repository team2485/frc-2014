package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;
import team2485.auto.SequencerFactory;

/**
 * Retracts the shoe piston
 * @author Marty
 */
public class RetractShoe implements SequencedItem {
    public void run() {
        Robot.catapult.setIntakePosition();
    }

    public double duration() {
        if(Robot.errorInAutonomous)
            return 0;

        return SequencerFactory.RETRACT_EXTEND_TIME;
    }
}
