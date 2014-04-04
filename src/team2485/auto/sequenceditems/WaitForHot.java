package team2485.auto.sequenceditems;

import edu.wpi.first.wpilibj.DriverStation;
import team2485.Robot;
import team2485.auto.SequencedItem;
import team2485.comp.TargetTracker;

/**
 *
 * @author W.A.R.Lords
 */
public class WaitForHot implements SequencedItem {
    private double duration = 0.1;
    public static final int
            LEFT  = 1,
            RIGHT = 2;
    private final int sideOfField;
    private double startTime = -1;

    public WaitForHot(int sideOfField) {
        this.sideOfField = sideOfField;
    }

    public void run() {
        int hot = Robot.tracker.getAutoTrackState();
        if (startTime == -1) startTime = DriverStation.getInstance().getMatchTime();

        if ((hot == TargetTracker.TRACK_LEFT  && sideOfField == LEFT) ||
            (hot == TargetTracker.TRACK_RIGHT && sideOfField == RIGHT)) {
            duration = 0.0;
        }
        else {
            duration = 5.0 - startTime;
        }

        System.out.println("Duration = " + duration);
    }

    public double duration() {
        return duration;
    }
}
