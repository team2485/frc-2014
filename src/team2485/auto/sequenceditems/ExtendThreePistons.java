package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 * Shoots using all three pistons (strong shot).
 * @author Marty Kausas
 * @author Camille Considine
 */
public class ExtendThreePistons implements SequencedItem {
    public void run() {
        Robot.catapult.extendThree();
    }

    public double duration() {
        return 0.5; // TODO: Figure out length
    }
}
