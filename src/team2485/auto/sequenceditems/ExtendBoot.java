package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;
import team2485.auto.SequencerFactory;
import team2485.comp.Catapult;

/**
 *
 * @author Marty
 */
public class ExtendBoot implements SequencedItem {

    public void run() {
        Robot.catapult.extendBoot();
    }

    public double duration() {
        if(Robot.errorInAutonomous)
            return 0;

        return SequencerFactory.RETRACT_EXTEND_TIME;
    }
}
