package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;
import team2485.auto.SequencerFactory;

/**
 *
 * @author W.A.R.Lords
 */
public class TargetShotWithoutRetractTime implements SequencedItem {

    public void run() {
        SequencerFactory.createShot(SequencerFactory.TARGET_SHOT).run();
    }

    public double duration() {
        return 0.4;
    }

}
