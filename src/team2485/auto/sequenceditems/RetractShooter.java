package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;
import team2485.auto.SequencerFactory;

/**
 *
 * @author Marty Kausas
 * @author Camille Considine
 */
public class RetractShooter implements SequencedItem {
    public void run() {
        Robot.catapult.retract();
    }

    public double duration() {
        if(Robot.errorInAutonomous)
            return 0;

        return SequencerFactory.RETRACT_EXTEND_TIME;
    }
}
