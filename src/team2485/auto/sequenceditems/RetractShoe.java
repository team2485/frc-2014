package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 * Retracts the shoe piston
 * @author Marty
 */
public class RetractShoe implements SequencedItem {
    public void run() {
        Robot.catapult.setIntakePosition();
    }

    public double duration() {
        return 0.5; // TODO: Figure out length
    }
}
