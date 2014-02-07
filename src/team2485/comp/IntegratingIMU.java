package team2485.comp;

import com.kauailabs.nav6.frc.BufferingSerialPort;
import com.kauailabs.nav6.frc.IMUAdvanced;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.visa.VisaException;

/**
 * Adds single and double integration of the acceleration provided by the
 * KauaiLabs nav6 IMU to obtain approximate velocity and position.
 *
 * @author Bryce Matsumori
 * @see PositionTracker
 * @see com.kauailabs.nav6.frc.IMUAdvanced
 */
public class IntegratingIMU extends IMUAdvanced {
    public static final int DEFAULT_BAUD_RATE = 57600;

    private long lastTime = 0;
    private float
            accX = 0f, velX = 0f, posX = 0f,
            accY = 0f, velY = 0f, posY = 0f;

    // Reuse vars since decodePacketHandler is called frequently
    private int packetLength;
    private long currentTime;
    private double deltaT;
    private float newAccX, newVelX, newAccY, newVelY;
    private IMUDataHandler handler;

    public static final float GRAVITY = 9.80665f; // conversion from g's to m/s/s

    /**
     * Creates a new {@code IntegratingIMU} with the default baud rate (57600).
     * @throws VisaException If we can't connect to the nav6
     */
    public IntegratingIMU() throws VisaException {
        super(new BufferingSerialPort(DEFAULT_BAUD_RATE));
    }

    public IntegratingIMU(BufferingSerialPort serial_port, byte update_rate_hz) {
        super(serial_port, update_rate_hz);
    }

    public IntegratingIMU(BufferingSerialPort serial_port) {
        super(serial_port);
    }

    public void setHandler(IMUDataHandler handler) {
        this.handler = handler;
    }

    protected int decodePacketHandler(byte[] received_data, int offset, int bytes_remaining) {
        packetLength = super.decodePacketHandler(received_data, offset, bytes_remaining);

        // Timer.getFPGATimestamp() causes an out of memory error and the IMU to never stop calibrating :(
        // currentTime = Timer.getFPGATimestamp();
        // deltaT = currentTime - lastTime;
        currentTime = System.currentTimeMillis();
        deltaT = (currentTime - lastTime) * 0.001; // convert from ms to s

        newAccX  = getWorldLinearAccelX() * GRAVITY;
        newVelX  = velX + (accX + newAccX) * 0.5f * (float)deltaT;
        posX    += (newVelX + velX) * 0.5f * deltaT;

        newAccY  = getWorldLinearAccelY() * GRAVITY;
        newVelY  = velY + (accY + newAccY) * 0.5f * (float)deltaT;
        posY    += (newVelY + velY) * 0.5f * deltaT;

        accX  = newAccX;
        velX  = newVelX;
        accY  = newAccY;
        velY  = newVelY;
        lastTime = currentTime;

        if (handler != null) handler.handleIMUData();

        return packetLength;
    }

    /**
     * Return the linear velocity along the x-axis in m/s.
     * @return The x velocity.
     */
    public float getWorldLinearVelX() {
        return velX;
    }

    /**
     * Returns the position along the x-axis in meters.
     * @return The x position.
     */
    public float getWorldLinearX() {
        return posX;
    }

     /**
     * Return the linear velocity along the y-axis in m/s.
     * @return The y velocity.
     */
    public float getWorldLinearVelY() {
        return velY;
    }

    /**
     * Returns the position along the y-axis in meters.
     * @return The y position.
     */
    public float getWorldLinearY() {
        return posY;
    }

    /**
     * Sets the current x position to the value.
     * @param newX The updated x position.
     */
    public void setLinearX(float newX) {
        posX = newX;
    }

    /**
     * Sets the current y position to the value.
     * @param newY The updated y position.
     */
    public void setLinearY(float newY) {
        posY = newY;
    }

    /**
     * Zeroes the current x velocity.
     */
    public void zeroLinearVelX() {
        velX = 0;
    }

    /**
     * Zeroes the current y velocity.
     */
    public void zeroLinearVelY() {
        velY = 0;
    }

    public interface IMUDataHandler {
        public void handleIMUData();
    }
}
