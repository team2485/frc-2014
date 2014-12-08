package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 *
 * @author Marty Kausas
 */
public class RetractBoot implements SequencedItem {

    public void run() {
        Robot.catapult.retractBoot();
    }

    public double duration() {
        if (Robot.errorInAutonomous) return 0;
        return 0.2;
    }
}
