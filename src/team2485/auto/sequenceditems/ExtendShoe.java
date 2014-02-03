package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 * Extends the shoe piston
 * @author Marty
 */
public class ExtendShoe implements SequencedItem {
    public void run() {
        Robot.catapult.setShootingPosition();
    }

    public double duration() {
        return 0.5; // TODO: Figure out length
    }
}
