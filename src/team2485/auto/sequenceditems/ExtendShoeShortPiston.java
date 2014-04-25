package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;
import team2485.comp.Catapult;

/**
 *
 * @author W.A.R.Lords
 */
public class ExtendShoeShortPiston implements SequencedItem {
    private boolean initialCheck = true;
    private double duration;

    public void run() {
        if (initialCheck) {
            duration = Robot.catapult.getShoeState() == Catapult.SHORT_EXTENDED ? 0 : 0.45;
            initialCheck = false;
        }

        Robot.catapult.extendShoeShortPiston();
    }

    public double duration() {
       if (Robot.errorInAutonomous) return 0;
        return duration;
    }
}
