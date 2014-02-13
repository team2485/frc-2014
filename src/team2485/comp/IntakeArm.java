package team2485.comp;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Talon;

/**
 * Intake Arm class
 *
 * @author Anoushka Bose
 */
public class IntakeArm {

    private Talon armMotors, rollerMotors;

    private PIDController armPID;
    private AnalogPotentiometer pot;

    // TODO: Tune the PID Constants
    public static double
            kP = 0.01,
            kI = 0,
            kD = 0;

    private boolean isPID = false;

    // TODO: Find these setpoints
    public static final double
            IN_CATAPULT      = 3000,
            UP_POSITION      = 2427,
            PICKUP           = 2770,
            POPPER_POSITION  = 2017;

    private double currentSetpoint = UP_POSITION;
    public static double ROLLERS_FORWARD = -1.0, ROLLERS_REVERSE = ROLLERS_FORWARD *= -1;
    public static final int ABSOLUTE_TOLERANCE = 25;

    /**
     * Constructor takes references two talons and pot used by intake
     * system.
     * @param rollerMotors
     * @param armMotors
     * @param pot
     */
    public IntakeArm(Talon rollerMotors, Talon armMotors, AnalogPotentiometer pot) {
        this.armMotors      = armMotors;
        this.rollerMotors   = rollerMotors;
        this.pot            = pot;

        currentSetpoint     = UP_POSITION;
        armPID              = new PIDController(kP, kI, kD, pot, armMotors);

        armPID.setAbsoluteTolerance(ABSOLUTE_TOLERANCE);
        armPID.setOutputRange(-1.0, 1.0);
    }

    /**
     * Constructor takes PWM channels for talons and channel for pot on analog
     * breakout port
     * @param rollerMotorChannel
     * @param armMotorChannel
     * @param potChannel
     */
    public IntakeArm(int rollerMotorChannel, int armMotorChannel, int potChannel) {
        this(new Talon(rollerMotorChannel), new Talon(armMotorChannel), new AnalogPotentiometer(potChannel));
    }

    /**
     * Starts rollers on intake arm
     * @param value
     */
    public void startRollers(double value) {
        rollerMotors.set(value);
    }

    /**
     * Stops rollers on intake arm
     */
    public void stopRollers() {
        rollerMotors.set(0);
    }

    public double getPotValue() {
        return pot.get();
    }


    /**
     * Sets PID values
     * @param Kp
     * @param Ki
     * @param Kd
     */
    public void setPID(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;

        armPID.setPID(kP, kI, kD);
    }

    /**
     * Sets setpoint
     * @param setpoint
     */
    public void setSetpoint(double setpoint) {
        armPID.setSetpoint(setpoint);
        currentSetpoint = setpoint;

        isPID = true;

        if (!armPID.onTarget()) {
            armPID.enable();
        } else
            armPID.disable();

        if (currentSetpoint == PICKUP)
            startRollers(ROLLERS_FORWARD);
    }

    /**
     * Moves arm manually
     * @param direction
     */
    public void moveArm(double speed) {
        if (Math.abs(speed) > 0.5) {
            armPID.disable();
            currentSetpoint = armPID.getSetpoint();
            // TODO: Find the speeds we want for manual arm movement
            armMotors.set(speed);
        } else if (!armPID.isEnable()) {
            armMotors.set(0.0);
        }
    }

    /**
     * Call this method in every execution of teleopPeriodic in order to ensure
     * the right PID gains are being used
     */
    public void run() {
        double potValue = pot.get();

        if (isPID) {
            if (!armPID.onTarget()) {
                armPID.enable();
            } else {
                armPID.disable();
                isPID = false;
            }
        }


        // TODO: Find what values to keep the rollers on for
//        if (50 > potValue && 400 < potValue)
//            startRollers(1.0);
//        else
//            stopRollers();

        // TODO: Find what 50 should be...
//        if (armPID.getSetpoint() < 2000 || potValue < 2000) {
//            currentSetpoint = 2000;
//            armPID.setSetpoint(currentSetpoint);
//            armPID.enable();
//            setPID(kP, kI, kD);
//        } else if (potValue > 3000) {
//            currentSetpoint = 3000;
//            armPID.setSetpoint(currentSetpoint);
//            armPID.enable();
//            setPID(kP, kI, kD);
//        }


    }

    /**
     * Returns current arm motor voltage
     * @return
     */
    public double getMotorArmVoltage() {
        return armMotors.get();
    }

}

