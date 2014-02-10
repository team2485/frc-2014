package team2485.comp;

import edu.wpi.first.wpilibj.*;
import team2485.util.*;

/**
 * Class representing the drive train and managing its motors and sensors.
 *
 * @author Marty Kausas
 */
public class DriveTrain {

    private Talon leftDrive, rightDrive;
    private Gyro gyro;
    private Encoder encoder;
    private Solenoid gearShifter;

    // TODO: Find the speed ratings
    private final double
            NORMAL_SPEED_RATING = 0.7,
            FAST_SPEED_RATING = 1.0,
            SLOW_SPEED_RATING = 0.6;

    private double driveSpeed = NORMAL_SPEED_RATING;

    // --- AUTONOMOUS --- //
    private DummyOutput dummyGyroOutput;
    private DummyOutput dummyEncoderOutput;
    private PIDController gyroPID;
    private PIDController encPID;
    // TODO: Tune each PID values
    private double Kp_G_Rotate, Ki_G_Rotate, Kd_G_Rotate;
    private double Kp_G_Drive, Ki_G_Drive, Kd_G_Drive;
    private double Kp_E, Ki_E, Kd_E;
    // TODO: Find all tolerances
    private double AbsTolerance_Gyro_DriveTo;
    private double AbsTolerance_Gyro_TurnTo;
    private double AbsTolerance_Enc;
    // TODO: Find low speed rates
    private double lowEncRate;
    // --- END AUTO --- //

    // --- W.A.R. LORD DRIVE STUFF --- //
    private double oldWheel = 0.0;
    private double quickStopAccumulator = 0.0;
    private final double throttleDeadband = 0.1;
    private final double wheelDeadband = 0.1;

    // Constants from old file
    private final double sensitivityHigh = 0.85;
    private final double sensitivityLow = 0.75;
    private boolean isQuickTurn = false;
    // --- END DRIVE STUFF --- //

    /**
     * Default Constructor
     *
     * @param leftDrive
     * @param rightDrive
     * @param gyro
     * @param encoder
     */
    public DriveTrain(Talon leftDrive, Talon rightDrive, Gyro gyro, Encoder encoder, Solenoid gearShifter) {
        this.leftDrive      = leftDrive;
        this.rightDrive     = rightDrive;
        this.gyro           = gyro;
        this.encoder        = encoder;
        this.gearShifter    = gearShifter;

        dummyGyroOutput     = new DummyOutput();
        dummyEncoderOutput  = new DummyOutput();
        gyroPID             = new PIDController(Kp_G_Rotate, Ki_G_Rotate, Kd_G_Rotate, gyro, dummyGyroOutput);
        encPID              = new PIDController(Kp_E, Ki_E, Kd_E, encoder, dummyEncoderOutput);

        gyroPID.setAbsoluteTolerance(AbsTolerance_Gyro_DriveTo);
        encPID.setAbsoluteTolerance(AbsTolerance_Enc);

        gyro.reset();
        encoder.reset();
    }

    /**
     * Constructor using only ports instead of objects
     *
     * @param leftDrivePort
     * @param rightDrivePort
     * @param gyroPort
     * @param encoderPortA
     * @param encoderPortB
     * @param gearShifterPort
     */
    public DriveTrain(int leftDrivePort, int rightDrivePort, int gyroPort, int encoderPortA, int encoderPortB, int gearShifterPort) {
        this(new Talon(leftDrivePort), new Talon(rightDrivePort), new Gyro(gyroPort), new Encoder(encoderPortA, encoderPortB), new Solenoid(gearShifterPort));
    }

    // <editor-fold defaultstate="collapsed" desc="Autonomous Methods">
    /**
     * Rotates to the parameter degrees from current robot angle
     *
     * @param degrees
     * @return when finished
     */
    public boolean rotateTo(double degrees) {
        if (!gyroPID.isEnable())
            gyroPID.enable();
        if (gyroPID.getP() != Kp_G_Rotate ||
                gyroPID.getI() != Ki_G_Rotate ||
                gyroPID.getD() != Kd_G_Rotate) {
            gyroPID.setPID(Kp_G_Rotate, Ki_G_Rotate, Kd_G_Rotate);
            gyroPID.setAbsoluteTolerance(AbsTolerance_Gyro_TurnTo);
        }

        gyroPID.setSetpoint(degrees);

        double gyroOutput = dummyGyroOutput.get();
        setLeftRight(gyroOutput, gyroOutput);

        if (gyroPID.onTarget() && Math.abs(encoder.getRate()) < lowEncRate) {
            gyroPID.disable();
            setLeftRight(0, 0);
            gyro.reset();
            return true;
        } else
            return false;
    }

    /**
     * Drives to the parameter feet from current position
     *
     * @param inches
     * @return when finished
     */
    public boolean driveTo(double inches) {
        if (!encPID.isEnable())
            encPID.enable();

        if (!gyroPID.isEnable())
            gyroPID.enable();
        if (gyroPID.getP() != Kp_G_Drive) {
            gyroPID.setPID(Kp_G_Drive, Ki_G_Drive, Kd_G_Drive);
            gyroPID.setAbsoluteTolerance(AbsTolerance_Gyro_DriveTo);

        }

        //        This corrects the error better but too jerky
        //        if (Math.abs(driveEncoder.getRate()) < 30)
        //            encPID.setPID(Constant.Kp_E, Constant.Ki_E, Constant.Kd_E);
        //        else
        //            encPID.setPID(Constant.Kp_E, 0, Constant.Kd_E);

        // TODO: Find the right distance per pulse (put in constructor)
        encPID.setSetpoint(inches * 121.98);
        gyroPID.setSetpoint(0); // assumption that the gyro has been reset

        double gyroOutput = dummyGyroOutput.get();
        double encoderOutput = dummyEncoderOutput.get();
        double leftOutput = encoderOutput + gyroOutput;
        double rightOutput = -(encoderOutput - gyroOutput);

        setLeftRight(leftOutput, rightOutput);

        // Check to see if we're on target
        if (gyroPID.onTarget()  && encPID.onTarget() &&
                Math.abs(encoder.getRate()) < lowEncRate) {
            setLeftRight(0.0, 0.0);
            gyroPID.disable();
            encPID.disable();
            return true;
        } else
            return false;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="W.A.R. Lord Drive">
    /**
     * W.A.R. Lord Drive
     * This drive method is based off of Team 254's Ultimate Ascent
     * cheesyDrive code.
     *
     * @param controllerY
     * @param controllerX
     */
    public void warlordDrive(double controllerY, double controllerX) {
        boolean isHighGear = true;
        //        drive.shift(isHighGear);

        double wheelNonLinearity;

        double wheel = handleThreshold(controllerX, wheelDeadband);
        double throttle = -handleThreshold(controllerY, throttleDeadband);

        double negInertia = wheel - oldWheel;
        oldWheel = wheel;

        if (isHighGear) {
            wheelNonLinearity = 0.6;
            // Apply a sin function that's scaled to make it feel better.
            wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) /
                    Math.sin(Math.PI / 2.0 * wheelNonLinearity);
            wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) /
                    Math.sin(Math.PI / 2.0 * wheelNonLinearity);
        } else {
            wheelNonLinearity = 0.5;
            // Apply a sin function that's scaled to make it feel better.
            wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) /
                    Math.sin(Math.PI / 2.0 * wheelNonLinearity);
            wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) /
                    Math.sin(Math.PI / 2.0 * wheelNonLinearity);
            wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) /
                    Math.sin(Math.PI / 2.0 * wheelNonLinearity);
        }

        double leftPwm, rightPwm, overPower;
        double sensitivity = 1.7;

        double angularPower;
        double linearPower;

        // Negative inertia!
        double negInertiaAccumulator = 0.0;
        double negInertiaScalar;
        if (isHighGear) {
            negInertiaScalar = 5.0;
            sensitivity = sensitivityHigh;
        } else {
            if (wheel * negInertia > 0) {
                negInertiaScalar = 2.5;
            } else {
                if (Math.abs(wheel) > 0.65) {
                    negInertiaScalar = 5.0;
                } else {
                    negInertiaScalar = 3.0;
                }
            }
            sensitivity = sensitivityLow;

            if (Math.abs(throttle) > 0.1) {
                // sensitivity = 1.0 - (1.0 - sensitivity) / Math.abs(throttle);
            }
        }
        double negInertiaPower = negInertia * negInertiaScalar;
        negInertiaAccumulator += negInertiaPower;

        wheel = wheel + negInertiaAccumulator;
        linearPower = throttle;

        // Quickturn!
        if (isQuickTurn) {
            if (Math.abs(linearPower) < 0.2) {
                double alpha = 0.1;
                wheel = wheel > 1 ? 1.0 : wheel;
                quickStopAccumulator = (1 - alpha) * quickStopAccumulator + alpha *
                        wheel * 0.5;
            }
            overPower = 1.0;
            if (isHighGear) {
                sensitivity = 1.0;
            } else {
                sensitivity = 1.0;
            }
            angularPower = wheel;
        } else {
            overPower = 0.0;
            angularPower = Math.abs(throttle) * wheel * sensitivity - quickStopAccumulator;
            if (quickStopAccumulator > 1) {
                quickStopAccumulator -= 1;
            } else if (quickStopAccumulator < -1) {
                quickStopAccumulator += 1;
            } else {
                quickStopAccumulator = 0.0;
            }
        }

        rightPwm = leftPwm = linearPower;
        leftPwm += angularPower;

        rightPwm -= angularPower;

        if (leftPwm > 1.0) {
            rightPwm -= overPower * (leftPwm - 1.0);
            leftPwm = 1.0;
        } else if (rightPwm > 1.0) {
            leftPwm -= overPower * (rightPwm - 1.0);
            rightPwm = 1.0;
        } else if (leftPwm < -1.0) {
            rightPwm += overPower * (-1.0 - leftPwm);
            leftPwm = -1.0;
        } else if (rightPwm < -1.0) {
            leftPwm += overPower * (-1.0 - rightPwm);
            rightPwm = -1.0;
        }

        // TODO: Remove printlns after testing has been done
        System.out.println("Outputs pwm: \t(" + leftPwm + ", " + rightPwm + ")");
        System.out.println("Ang and Lin: \t(" + angularPower + ", " + linearPower + ")");

        setLeftRight(leftPwm, -rightPwm);
    }
    // </editor-fold>

    /**
     * Sets the drive to quick turn mode
     */
    public void setQuickTurn(boolean isQuickTurn) {
        this.isQuickTurn = isQuickTurn;
    }

    // <editor-fold defaultstate="collapsed" desc="General Methods">
    /**
     * Sends outputs values to the left and right side
     * of the drive base.
     *
     * @param leftOutput
     * @param rightOutput
     */
    private void setLeftRight(double leftOutput, double rightOutput) {
        leftDrive.set(leftOutput);
        rightDrive.set(-rightOutput);
    }

    /**
     * Thresholds values
     *
     * @param val
     * @param deadband
     * @return
     */
    private double handleThreshold(double val, double threshold) {
        return (Math.abs(val) > Math.abs(threshold)) ? val : 0.0;
    }

    /**
     * Set the gear boxes into high gear
     */
    public void highGear() {
        gearShifter.set(true);
    }

    /**
     * Set the gear boxes into low gear
     */
    public void lowGear() {
        gearShifter.set(false);
    }

    /**
     * Switch into high speed mode
     */
    public void setHighSpeed() { driveSpeed = FAST_SPEED_RATING; }

    /**
     * Switch into low speed mode
     */
    public void setLowSpeed() { driveSpeed = SLOW_SPEED_RATING; }

    /**
     * Switch to normal speed mode
     */
    public void setNormalSpeed() { driveSpeed = NORMAL_SPEED_RATING; }

    /**
     * Set the PID values for the gyro {@code PIDController}g
     *
     * @param Kp
     * @param Ki
     * @param Kd
     */
    public void setPIDGyro(double Kp, double Ki, double Kd) {
        Kp_G_Rotate = Kp;
        Ki_G_Rotate = Ki;
        Kd_G_Rotate = Kd;
    }

    /**
     * Set the PID values for the encoder {@code PIDController}
     *
     * @param Kp
     * @param Ki
     * @param Kd
     */
    public void setPIDEnc(double Kp, double Ki, double Kd) {
        Kp_E = Kp;
        Ki_E = Ki;
        Kd_E = Kd;
    }

    public void resetSensors() {
        gyro.reset();
        encoder.reset();
    }
    // </editor-fold>
}
