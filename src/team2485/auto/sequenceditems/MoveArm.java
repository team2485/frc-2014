package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 *
 * @author W.A.R.Lords
 */
public class MoveArm implements SequencedItem {

    double setpoint;
    boolean done = false;

    public MoveArm(double setpoint) {
        this.setpoint = setpoint;
    }

    public void run() {
        done = Robot.arm.setSetpoint(setpoint);
    }

    public double duration() {
        return done ? 0 : Integer.MAX_VALUE;
    }
}
