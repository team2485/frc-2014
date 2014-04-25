package team2485.auto.sequenceditems;

import team2485.Robot;
import team2485.auto.SequencedItem;

/**
 *
 * @author Bryce Matsumori
 */
public class MoveArmNoWait implements SequencedItem {
    private final double setpoint;
    private boolean rollersOn = true;

    public MoveArmNoWait(double setpoint) {
        this.setpoint = setpoint;
    }

    public MoveArmNoWait(double setpoint, boolean rollersOn) {
        this.setpoint  = setpoint;
        this.rollersOn = rollersOn;
    }

    public void run() {
        Robot.arm.setSetpoint(setpoint, rollersOn);
    }

    public double duration() {
        return 0;
    }
}
