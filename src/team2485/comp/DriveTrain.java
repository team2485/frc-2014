package team2485.comp;

import edu.wpi.first.wpilibj.*;
import team2485.auto.*;
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

    // --- AUTONOMOUS --- //
    private DummyOutput dummyGyroOutput;
    private DummyOutput dummyEncoderOutput;
    private PIDController gyroPID;
    private PIDController encPID;
    private double Kp_G_Rotate, Ki_G_Rotate, Kd_G_Rotate;
    private double Kp_G_Drive, Ki_G_Drive, Kd_G_Drive;
    private double Kp_E, Ki_E, Kd_E;
    private double AbsTolerance_Gyro_DriveTo;
    private double AbsTolerance_Gyro_TurnTo;
    private double AbsTolerance_Enc;
    private double lowEncRate;
    // --- END AUTO --- //

    // --- CHEESY DRIVE STUFF --- //
    private double oldWheel = 0.0;
    private double quickStopAccumulator = 0.0;
    private final double throttleDeadband = 0.1;
    private final double wheelDeadband = 0.1;

    // Constants from poof constant file
    private final double sensitivityHigh = 0.85;
    private final double sensitivityLow = 0.75;
    // --- END CHEESY DRIVE STUFF --- //

    /**
     * Default Constructor
     *
     * @param leftDrive
     * @param rightDrive
     * @param gyro
     * @param encoder
     */
    public DriveTrain(Talon leftDrive, Talon rightDrive, Gyro gyro, Encoder encoder) {
        this.leftDrive      = leftDrive;
        this.rightDrive     = rightDrive;
        this.gyro           = gyro;
        this.encoder        = encoder;

        dummyGyroOutput     = new DummyOutput();
        dummyEncoderOutput  = new DummyOutput();
        gyroPID             = new PIDController(Kp_G_Rotate, Ki_G_Rotate, Kd_G_Rotate, gyro, dummyGyroOutput);
        encPID              = new PIDController(Kp_E, Ki_E, Kd_E, encoder, dummyEncoderOutput);

        gyroPID.setAbsoluteTolerance(AbsTolerance_Gyro_DriveTo);
        encPID.setAbsoluteTolerance(AbsTolerance_Enc);
    }

    /**
     * Constructor using only ports instead of objects
     *
     * @param leftDrivePort
     * @param rightDrivePort
     * @param gyroPort
     * @param encoderPortA
     * @param encoderPortB
     */
    public DriveTrain(int leftDrivePort, int rightDrivePort, int gyroPort, int encoderPortA, int encoderPortB) {
        this(new Talon(leftDrivePort), new Talon(rightDrivePort), new Gyro(gyroPort), new Encoder(encoderPortA, encoderPortB));
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
        boolean isQuickTurn = false; //controlBoard.getQuickTurn();
        boolean isHighGear = true;
        //        drive.shift(isHighGear);

        double wheelNonLinearity;

        double wheel = handleDeadband(controllerX, wheelDeadband);
        double throttle = -handleDeadband(controllerY, throttleDeadband);

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

        System.out.println("Outputs pwm: \t(" + leftPwm + ", " + rightPwm + ")");
        System.out.println("Ang and Lin: \t(" + angularPower + ", " + linearPower + ")");

        setLeftRight(leftPwm, -rightPwm);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="General Methods">
    public void setLeftRight(double leftOutput, double rightOutput) {
        leftDrive.set(leftOutput);
        rightDrive.set(-rightOutput);
    }

    public double handleDeadband(double val, double deadband) {
        return (Math.abs(val) > Math.abs(deadband)) ? val : 0.0;
    }

    public void setPIDGyro(double Kp, double Ki, double Kd) {
        Kp_G_Rotate = Kp;
        Ki_G_Rotate = Ki;
        Kd_G_Rotate = Kd;
    }

    public void setPIDEnc(double Kp, double Ki, double Kd) {
        Kp_E = Kp;
        Ki_E = Ki;
        Kd_E = Kd;
    }
    // </editor-fold>
}
