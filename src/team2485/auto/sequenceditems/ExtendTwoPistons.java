package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 * Shoots using two pistons (medium shot).
 * @author Marty Kausas
 * @author Camille Considine
 */
public class ExtendTwoPistons implements SequencedItem {
    public void run() {
        Robot.catapult.extendTwo();
    }

    public double duration() {
        return 0.5; // TODO: Figure out length
    }
}
