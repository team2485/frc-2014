package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;
import team2485.auto.SequencerFactory;
import team2485.comp.Catapult;

/**
 * Retracts the shoe piston
 * @author Marty
 */
public class RetractShoe implements SequencedItem {

    private boolean initialCheck = true;
    private double duration;

    public void run() {
        if (initialCheck) {
            duration = Robot.catapult.shoeExtended() ? 0.2 : 0;
            initialCheck = false;
        }

        Robot.catapult.retractShoe();
    }

    public double duration() {
        if(Robot.errorInAutonomous)
            return 0;

        return duration;
    }
}
