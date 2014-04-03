package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 *
 * @author W.A.R.Lords
 */
public class ExtendCatcher implements SequencedItem {

    private boolean hasRun = false;

    public void run() {
        Robot.catcher.extend();
        hasRun = true;
    }

    public double duration() {
        return hasRun ? 0 : 1;
    }

}
