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
    private static final int potSlippage = -570;
    private boolean rollersOn = false;

    public static double
            kP = 0.01,
            kI = 0,
            kD = 0;

    private double potValue;
    private boolean isPID = false;

    // TODO: Find these setpoints
    public static final double
            IN_CATAPULT      = 2975 + potSlippage,
            UP_POSITION      = 2390 + potSlippage,
            FORTYFIVE_UP     = 2524 + potSlippage,
            PICKUP           = 2850 + potSlippage,
            POPPER_POSITION  = 2017 + potSlippage,
            LOW_LIMIT        = 1951 + potSlippage,
            DEFENSE          = 2059 + potSlippage,
            STARTING_CONFIG  = 2331 + potSlippage;

    // all the way down 2475

    private double currentSetpoint = UP_POSITION;
    public static final double ROLLERS_FORWARD = -1.0, ROLLERS_REVERSE = -ROLLERS_FORWARD;
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

//    public void toggleRollers() {
//        if (rollersOn)
//            startRollers(ROLLERS_FORWARD);
//        else
//            stopRollers();
//    }

    /**
     * Starts rollers on intake arm
     * @param value
     */
    public void startRollers(double value) {
        rollerMotors.set(value);
        rollersOn = true;
    }

    /**
     * Stops rollers on intake arm
     */
    public void stopRollers() {
        rollerMotors.set(0);
        rollersOn = false;
    }

    public double getPotValue() {
        return pot.get();
    }


    /**
     * Sets PID values
     * @param kP
     * @param kI
     * @param kD
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
     * @return
     */
    public boolean setSetpoint(double setpoint) {
        return setSetpoint(setpoint, true);
    }

    /**
     * Sets setpoint and turns rollers on or off
     * @param setpoint
     * @param rollersOn
     * @return
     */
    public boolean setSetpoint(double setpoint, boolean rollersOn) {
        armPID.setSetpoint(setpoint);
        currentSetpoint = setpoint;

        isPID = true;

        if (!armPID.onTarget()) {
            armPID.enable();
        } else
            armPID.disable();

        if (currentSetpoint == PICKUP && rollersOn) {
            startRollers(1.0);
        }

        return armPID.onTarget();
    }


    /**
     * Moves arm manually
     * @param speed
     */
    public void moveArm(double speed) {
        if (Math.abs(speed) > 0.5) {
            armPID.disable();
            currentSetpoint = armPID.getSetpoint();
            isPID = false;

            // TODO: implement stop
            if ((speed > 0 && potValue > IN_CATAPULT - 100) || (speed < 0 && potValue < LOW_LIMIT)) {
                armMotors.set(0.0);
            } else {
                // TODO: Find the speeds we want for manual arm movement
                armMotors.set(speed);
            }
        } else if (!armPID.isEnable()) {
            armMotors.set(0.0);
        }
    }

    /**
     * Call this method in every execution of teleopPeriodic in order to ensure
     * the right PID gains are being used
     */
    public void run() {
        potValue = pot.get();

//        if (armPID.getSetpoint() == IN_CATAPULT && potValue > IN_CATAPULT - 50)
//            if (rollersOn)
//                stopRollers();

        if (armPID.getSetpoint() == IN_CATAPULT && potValue > IN_CATAPULT - 100) {
            stopRollers();
        }

        System.out.println("pot val " + potValue);

        if (isPID) {
            if (!armPID.onTarget()) {
                armPID.enable();
            } else {
                armPID.disable();
                isPID = false;
            }
        }
    }

    /**
     * Returns current arm motor voltage
     * @return
     */
    public double getMotorArmVoltage() {
        return armMotors.get();
    }

    public boolean rollersOn() {
        return rollersOn;
    }

}

