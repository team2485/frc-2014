package team2485.comp;

import com.kauailabs.nav6.frc.IMU;
import edu.wpi.first.wpilibj.*;
import team2485.util.*;

/**
 * Class representing the drive train and managing its motors and sensors.
 *
 * @author Marty Kausas
 */
public class DriveTrain {

    private Talon leftDrive, rightDrive;
    private Encoder encoder;
    private Solenoid gearShifter;

    private final double
            NORMAL_SPEED_RATING = 1.0,
            FAST_SPEED_RATING   = 1.0,
            SLOW_SPEED_RATING   = 0.6;

    private double driveSpeed = NORMAL_SPEED_RATING;

    // AUTONOMOUS
    private DummyOutput dummyImuOutput;
    private DummyOutput dummyEncoderOutput;
    private PIDController imuPID;
    private PIDController encPID;
    public static double
            kP_G_Rotate = 0.028,
            kI_G_Rotate = 0.0,
            kD_G_Rotate = 0.0;
    public static double kP_G_Drive, kI_G_Drive, kD_G_Drive;
    public static double
            kP_E = 0.075,
            kI_E,
            kD_E;

    private final double AbsTolerance_Imu_DriveTo = 2.0;
    private final double AbsTolerance_Imu_TurnTo = 3.0;
    private final double AbsTolerance_Enc = 5;

    private double lowEncRate = 5;

    // W.A.R. LORD DRIVE
    private double oldWheel = 0.0;
    private double quickStopAccumulator = 0.0;
    private final double throttleDeadband = 0.1;
    private final double wheelDeadband = 0.1;

    private final double sensitivityHigh = 0.85;
    private final double sensitivityLow = 0.75;
    private boolean isQuickTurn = false;

    private IMU imu;

    /**
     *
     * Constructor with IMU.
     *
     * @param leftDrive
     * @param rightDrive
     * @param imu Can be null, set it later with {@code setImu()}
     * @param encoder
     * @param gearShifter
     */
    public DriveTrain(Talon leftDrive, Talon rightDrive, IMU imu, Encoder encoder, Solenoid gearShifter) {
        this.leftDrive      = leftDrive;
        this.rightDrive     = rightDrive;
        this.imu            = imu;
        this.encoder        = encoder;
        this.gearShifter   = gearShifter;

        if (imu != null) {
            dummyImuOutput = new DummyOutput();
            imuPID = new PIDController(kP_G_Rotate, kI_G_Rotate, kD_G_Rotate, imu, dummyImuOutput);
            imuPID.setAbsoluteTolerance(AbsTolerance_Imu_DriveTo);
        }

        dummyEncoderOutput = new DummyOutput();
        encPID = new PIDController(kP_E, kI_E, kD_E, encoder, dummyEncoderOutput);
        encPID.setAbsoluteTolerance(AbsTolerance_Enc);

        encoder.reset();
    }

    public DriveTrain(Talon leftDrive, Talon rightDrive, Encoder encoder, Solenoid gearShifter) {
        this.leftDrive   = leftDrive;
        this.rightDrive  = rightDrive;
        this.encoder     = encoder;
        this.gearShifter = gearShifter;

        dummyEncoderOutput = new DummyOutput();
        encoder.setPIDSourceParameter(Encoder.PIDSourceParameter.kDistance);
        encoder.setDistancePerPulse(0.05178664);
        encoder.start();

        encPID = new PIDController(kP_E, kI_E, kD_E, encoder, dummyEncoderOutput);
        encPID.setAbsoluteTolerance(AbsTolerance_Enc);
    }

    /**
     * Constructor using only ports instead of objects
     *
     * @param leftDrivePort
     * @param rightDrivePort
     * @param encoderPortA
     * @param encoderPortB
     * @param gearShifterPort
     */
    public DriveTrain(int leftDrivePort, int rightDrivePort, int encoderPortA, int encoderPortB, int gearShifterPort) {
        this(new Talon(leftDrivePort), new Talon(rightDrivePort), new Encoder(encoderPortA, encoderPortB), new Solenoid(gearShifterPort));
    }

    public void setImu(IMU imu) {
        this.imu = imu;

        dummyImuOutput = new DummyOutput();
        imuPID = new PIDController(kP_G_Rotate, kI_G_Rotate, kD_G_Rotate, imu, dummyImuOutput);
        imuPID.setAbsoluteTolerance(AbsTolerance_Imu_DriveTo);
    }

    // <editor-fold defaultstate="collapsed" desc="Autonomous Methods">

    /**
     * Rotates to the parameter degrees from current robot angle
     *
     * @param degrees
     * @return when finished
     */
    public boolean rotateTo(double degrees) {
        if (!imuPID.isEnable()) {
            imuPID.enable();
            imuPID.setSetpoint(degrees);
        }
        if (imuPID.getP() != kP_G_Rotate || imuPID.getI() != kI_G_Rotate || imuPID.getD() != kD_G_Rotate) {
            initPIDGyroRotate();
        }

        double imuOutput = dummyImuOutput.get();
        setLeftRight(imuOutput, -imuOutput);
        System.out.println("p " + imuPID.getP() + " kpg rotate " + kP_G_Rotate);
        System.out.println("imu outputs " + imuOutput);

        System.out.println("ROTATION Error: " + imuPID.getError() + " Current Setpoint: " + imuPID.getSetpoint() + " yaw " + imu.getYaw());
        System.out.println("enc rate " + Math.abs(encoder.getRate()));

        System.out.println("imuPID.onTarget() = " + imuPID.onTarget() + " Math.abs(encoder.getRate()) < lowEncRate " + (Math.abs(encoder.getRate()) < lowEncRate));

        imuPID.setAbsoluteTolerance(AbsTolerance_Imu_TurnTo);

        if (imuPID.onTarget() && Math.abs(encoder.getRate()) < lowEncRate) {
            imuPID.disable();
            setLeftRight(0, 0);
            return true;
        }
        return false;
    }

    /**
     * Rotates to zero degrees from current robot angle
     * @return when finished
     */
    public boolean rotateToZero() {
        return rotateTo(0);
    }

    /**
     * Drives to the parameter feet from current position
     *
     * @param inches
     * @return when finished
     */
    public boolean driveTo(double inches) {
        if (!encPID.isEnable()) {
            encoder.start();
            encPID.enable();
        }

        encPID.setSetpoint(inches);

        double encoderOutput = dummyEncoderOutput.get();
        double leftOutput  = encoderOutput;
        double rightOutput = encoderOutput;

        setLeftRight(leftOutput, rightOutput);

        // Check to see if we're on target
        if (encPID.onTarget() && Math.abs(encoder.getRate()) < lowEncRate) {
            setLeftRight(0.0, 0.0);
            encPID.disable();
            return true;
        }
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
        boolean isHighGear = isQuickTurn;

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

        leftPwm  += angularPower;
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

        setLeftRight(leftPwm, rightPwm);
    }

    // </editor-fold>

    /**
     * Sets the drive to quick turn mode
     * @param isQuickTurn
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
        leftDrive.set(leftOutput * driveSpeed);
        rightDrive.set(-rightOutput * driveSpeed);
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
        gearShifter.set(false);
    }

    /**
     * Set the gear boxes into low gear
     */
    public void lowGear() {
        gearShifter.set(true);
    }

    /**
     * Switch into high speed mode
     */
    public void setHighSpeed() {
        driveSpeed = FAST_SPEED_RATING;
    }

    /**
     * Switch into low speed mode
     */
    public void setLowSpeed() {
        driveSpeed = SLOW_SPEED_RATING;
    }

    /**
     * Switch to normal speed mode
     */
    public void setNormalSpeed() {
        driveSpeed = NORMAL_SPEED_RATING;
    }

    public void setPIDGyroDrive() {
        imuPID.setPID(kP_G_Drive, kI_G_Drive, kD_G_Drive);
        imuPID.setAbsoluteTolerance(AbsTolerance_Imu_DriveTo);
    }

    public void initPIDGyroRotate() {
        imuPID.setPID(kP_G_Rotate, kI_G_Rotate, kD_G_Rotate);
        imuPID.setAbsoluteTolerance(AbsTolerance_Imu_TurnTo);
    }

    public void initPIDEncoder() {
        encPID.setPID(kP_E, kI_E, kD_E);
    }

    public void resetSensors() {
        encoder.reset();
        imu.zeroYaw();
    }

    public double getEncoderOutput() {
        return encoder.getDistance();
    }

    public double getAngle() {
        if (imu == null) return 0;
        return imu.getYaw();
    }

    public void disableIMUPID() {
        imuPID.disable();
    }

    public void disableEncoderPID() {
        encPID.disable();
    }

    // </editor-fold>
}
