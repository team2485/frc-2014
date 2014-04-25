package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 *
 * @author W.A.R.Lords
 */
public class MoveArm implements SequencedItem {
    private final double setpoint;
    private boolean done = false;
    private boolean rollersOn = true;

    public MoveArm(double setpoint) {
        this.setpoint = setpoint;
    }

    public MoveArm(double setpoint, boolean rollersOn) {
        this.setpoint  = setpoint;
        this.rollersOn = rollersOn;
    }

    public void run() {
        done = Robot.arm.setSetpoint(setpoint, rollersOn);
    }

    public double duration() {
        if (Robot.errorInAutonomous) return 0;
        return done ? 0 : Integer.MAX_VALUE;
    }
}
