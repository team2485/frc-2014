package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;
import team2485.auto.SequencerFactory;
import team2485.comp.TargetTracker;

/**
 *
 * @author W.A.R.Lords
 */
public class WaitForHot implements SequencedItem{
    private double duration = 0.1;
    public static final int LEFT = 1,
                            RIGHT = 2;
    private int sideOfField;

    public WaitForHot(int sideOfField) {
        this.sideOfField = sideOfField;
    }

    // Intentionally left empty
    public void run() {
        int hot = Robot.tracker.getAutoTrackState();

        if((hot == TargetTracker.TRACK_LEFT && sideOfField == LEFT) || (hot == TargetTracker.TRACK_RIGHT && sideOfField == RIGHT))
            duration = 0;
        else
            duration = 5.0 - SequencerFactory.TARGET_FLIP_PAUSE_TIME - SequencerFactory.RETRACT_EXTEND_TIME;

        System.out.println("Duration = " + duration);
    }

    public double duration() {
        return duration;
    }
}
