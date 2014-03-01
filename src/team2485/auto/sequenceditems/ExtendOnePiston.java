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
        if(Robot.errorInAutonomous)
            return 0;
        return 0.5;
    }
}
