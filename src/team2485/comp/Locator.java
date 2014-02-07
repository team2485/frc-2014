package team2485.comp;

import edu.wpi.first.wpilibj.visa.VisaException;
import team2485.Robot;

/**
 * Tracks the robot's current position using the IMU and encoders.
 * @author Bryce Matsumori
 */
public class Locator implements IntegratingIMU.IMUDataHandler {
    public IntegratingIMU imu = null;
    private boolean calibrating = true;

    private double absoluteX, absoluteY, lastEncoderDist = 0, lastIMULinearX, lastIMULinearY;

    /**
     * Creates a new Locator and tries to connect to the IMU.
     */
    public Locator() {
        try {
            imu = new IntegratingIMU();
            imu.setHandler(this);
        } catch (VisaException ex) {
            final Locator self = this;
            new Thread(new Runnable() {
                public void run() {
                    // Try getting the IMU for 10 * 100 (1 sec)
                    for (int i = 0; i < 10 && imu == null; i++) {
                        try {
                            imu = new IntegratingIMU();
                            imu.setHandler(self);
                        } catch (VisaException ex) { }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) { }
                    }

                    if (imu == null) System.err.println("Could not connect to the IMU!");
                    else System.out.println("Connected to the IMU");
                }
            }, "IMU Connect").start();
        }
    }

    /**
     * Gets whether we are connected to the IMU.
     * @return Connected?
     */
    public boolean isConnected() {
        return imu != null;
    }

    /**
     * Gets whether the IMU is connected and calibrating.
     * @return Connected and calibrating?
     */
    public boolean isCalibrating() {
        return isCalibrating(false);
    }

    /**
     * Gets whether the IMU is connected and calibrating.
     * @param zeroYawOnFinish Whether to zero the yaw when we have finished calibrating.
     * @return Connected and calibrating?
     */
    public boolean isCalibrating(boolean zeroYawOnFinish) {
        if (imu == null || !calibrating) return false;

        calibrating = imu.isCalibrating();
        if (!calibrating && zeroYawOnFinish) imu.zeroYaw(); // after calibrating

        return calibrating;
    }

    /**
     * Returns the IMU's position along the x-axis in meters.
     * @return The x position.
     */
    public float getImuX() {
        return imu.getWorldLinearX();
    }

    /**
     * Returns the IMU's position along the y-axis in meters.
     * @return The y position.
     */
    public float getImuY() {
        return imu.getWorldLinearY();
    }

    /**
     * Returns the locator's y position, based off the encoder and accelerometer input.
     * @return The tracked x position.
     */
    public double getX() {
        return absoluteX;
    }

    /**
     * Returns the locator's y position, based off the encoder and accelerometer input.
     * @return The tracked y position.
     */
    public double getY() {
        return absoluteY;
    }

    /**
     * Gets the IMU.
     * @return The IMU, or null if not connected yet.
     */
    public IntegratingIMU imu() {
        return imu;
    }

    public String toString() {
        return new StringBuffer(String.valueOf(imu.getWorldLinearX())).append(',').append(imu.getWorldLinearY()).toString();
    }

    /**
     * Updates the locator. Should be called each iteration.
     */
    public void handleIMUData() {
        double newDist = Robot.drive.getEncoderDistance();
        double distDiff = newDist - lastEncoderDist;
        double newIMUX = imu.getWorldLinearX(), newIMUY = imu.getWorldLinearY();
        double imuXDiff = newIMUX - lastIMULinearX;
        double imuYDiff = newIMUY - lastIMULinearY;

        lastEncoderDist = newDist;
        lastIMULinearX = newIMUX;
        lastIMULinearY = newIMUY;

        // encoder low pass, acc high pass
        final double angle = (imu.getYaw() + 90) * Math.PI / 180.0;
        final double limit = 0.9;

        absoluteX += limit * (Math.cos(angle) * distDiff) + (1 - limit) * imuXDiff;
        absoluteY += limit * (Math.sin(angle) * distDiff) + (1 - limit) * imuYDiff;
    }
}
