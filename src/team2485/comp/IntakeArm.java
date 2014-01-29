package team2485.comp;

import team2485.util.TwoSpeedControllers;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Talon;

/**
 * Intake Arm class
 *
 * @author Anoushka Bose
 */
public class IntakeArm {

    private TwoSpeedControllers armMotors, rollerMotors;

    private PIDController armPID;
    private AnalogPotentiometer pot;

    // TODO: Tune the PID Constants
    public static double
            kP_UP   = 0.001,
            kI_UP   = 0,
            kD_UP   = 0,
            kP_DOWN = 0.001,
            kI_DOWN = 0,
            kD_DOWN = 0,
            kP_STAT = 0.001,
            kI_STAT = 0,
            kD_STAT = 0;

    // TODO: Find these setpoints
    public static final double
            IN_CATAPULT      = 200,
            UP_CONFIGURATION = 500,
            PICKUP           = 800;

    private double currentSetpoint = UP_CONFIGURATION;
    // TODO: Find the default speed
    public static double DEFAULT_ROLLER_SPEED = 0.8;

    /**
     * Constructor takes references to speed controllers and pot used by intake
     * system.
     * @param rollerMotors
     * @param armMotors
     * @param pot
     */
    public IntakeArm(TwoSpeedControllers rollerMotors, TwoSpeedControllers armMotors, AnalogPotentiometer pot) {
        this.armMotors      = armMotors;
        this.rollerMotors   = rollerMotors;
        this.pot            = pot;

        currentSetpoint     = UP_CONFIGURATION;
        armPID              = new PIDController(kP_UP, kI_UP, kD_UP, pot, armMotors);

        armPID.setOutputRange(-1.0, 1.0);
        armPID.enable();
    }

    /**
     * Constructor takes PWM channels for motors and channel for pot on analog
     * breakout port
     * @param rollerMotorChannel
     * @param armMotorChannel
     * @param potChannel
     */
    public IntakeArm(int rollerMotorChannel, int armMotorChannel, int potChannel) {
        this(new TwoSpeedControllers(new Talon(rollerMotorChannel)), new TwoSpeedControllers(new Talon(armMotorChannel)), new AnalogPotentiometer(potChannel));
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


    /**
     * Sets PID values for up state
     * In future, this will not have parameters b/c we should know values
     * @param Kp
     * @param Ki
     * @param Kd
     */
    public void setPIDUp(double Kp, double Ki, double Kd) {
        kP_UP = Kp;
        kI_UP = Ki;
        kD_UP = Kd;

        armPID.setPID(kP_UP, kI_UP, kD_UP);
    }

    /**
     * Sets PID values for down state
     * In future, this will not have parameters b/c we should know values
     * @param Kp
     * @param Ki
     * @param Kd
     */
     public void setPIDDown(double Kp, double Ki, double Kd) {
        kP_DOWN = Kp;
        kI_DOWN = Ki;
        kD_DOWN = Kd;

        armPID.setPID(kP_DOWN, kI_DOWN, kD_DOWN);
    }

     /**
      * Sets PID values for stationary state
      * In future, this will not have parameters b/c we should know values
      * @param Kp
      * @param Ki
      * @param Kd
      */
     public void setPIDStationary(double Kp, double Ki, double Kd) {
        kP_STAT = Kp;
        kI_STAT = Ki;
        kD_STAT = Kd;

        armPID.setPID(kP_STAT, kI_STAT, kD_STAT);
    }

    /**
     * Sets setpoint
     * @param setpoint
     */
    public void setSetpoint(double setpoint) {
        armPID.enable();
        armPID.setSetpoint(setpoint);
        currentSetpoint = setpoint;
    }

    /**
     * Moves arm manually
     * @param direction
     */
    public void moveArm(double direction) {
        if (Math.abs(direction) > 0.5) {
            armPID.disable();
            currentSetpoint = armPID.getSetpoint();
            // TODO: Find the speeds we want for manual arm movement
            armMotors.set(direction > 0 ? 0.5 : -0.5);
        } else if (!armPID.isEnable()) {
            armMotors.set(0.0);
        }
    }

    /**
     * Call this method in every execution of teleopPeriodic in order to ensure
     * the right PID gains are being used
     */
    public void execute() {
        double potValue = pot.get();

        // TODO: Find what 50 should be...
        if (potValue < 50) {
            currentSetpoint = 50;
            armPID.setSetpoint(currentSetpoint);
            armPID.enable();
            setPIDStationary(kP_STAT, kI_STAT, kD_STAT);
        } else if (potValue > 950) {
            currentSetpoint = 950;
            armPID.setSetpoint(currentSetpoint);
            armPID.enable();
            setPIDStationary(kP_STAT, kI_STAT, kD_STAT);
        } else if (!armPID.isEnable()) {
            // this left blank intentionally
        } else if (armPID.onTarget()) {
            setPIDStationary(kP_STAT, kI_STAT, kD_STAT);
        } else if (potValue > UP_CONFIGURATION) {
            if(potValue < currentSetpoint)
                setPIDDown(kP_DOWN, kI_DOWN, kD_DOWN);
            else
                setPIDUp(kP_UP, kI_UP, kD_UP);
        } else if (potValue < UP_CONFIGURATION) {
            if(potValue < currentSetpoint)
                setPIDUp(kP_UP, kI_UP, kD_UP);
            else
                setPIDDown(kP_DOWN, kI_DOWN, kD_DOWN);
        }
    }

    /**
     * Returns current motor voltage
     * @return
     */
    public double getMotorVoltage() {
        return armMotors.get();
    }

}

