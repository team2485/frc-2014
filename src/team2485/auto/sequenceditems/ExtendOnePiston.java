package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;
import team2485.auto.SequencerFactory;

/**
 * Shoots using only one piston (weak shot).
 * @author Marty Kausas
 * @author Camille Considine
 */
public class ExtendOnePiston implements SequencedItem {
    public void run() {
        Robot.catapult.extendOne();
    }

    public double duration() {
        return SequencerFactory.RETRACT_EXTEND_TIME;
    }
}
