package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;
import team2485.auto.SequencerFactory;

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
        return SequencerFactory.RETRACT_EXTEND_TIME;
    }
}
