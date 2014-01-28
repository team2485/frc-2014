package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 *
 * @author Marty Kausas
 * @author Camille Considine
 */
public class ExtendThreePistons implements SequencedItem {

    private double length = 0.5;

    public void run() {
        Robot.catapult.extendThree();
    }

    public double duration() {
        return length;
    }

}
