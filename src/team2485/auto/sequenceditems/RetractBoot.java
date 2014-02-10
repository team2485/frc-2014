package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;
import team2485.comp.Catapult;

/**
 *
 * @author Marty
 */
public class RetractBoot implements SequencedItem {

    public void run() {
        Robot.catapult.retractBoot();
    }

    public double duration() {
        return 0.5; // TODO: Figure out the duration
    }
}
