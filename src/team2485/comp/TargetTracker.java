package team2485.comp;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 * Retroreflective target tracker using the Axis camera.
 * Communicates with the SmartDashboard to obtain tracking and distance info.
 *
 * @author Bryce Matsumori
 */
public class TargetTracker {
    private NetworkTable table;

    public static final int
            TRACK_NONE  = 0,
            TRACK_LEFT  = 1,
            TRACK_RIGHT = 2,
            TRACK_BOTH  = 3;

    public TargetTracker() {
        table = NetworkTable.getTable("vision");
    }

    /**
     * Whether the Axis camera is connected to the SmartDashboard and
     * the SmartDashboard is connected to the robot.
     * @return Whether we are connected.
     */
    public boolean isConnected() {
        return table.getBoolean("connected", false);
    }

    /**
     * Gets the target tracking state.
     * @return The track state.
     */
    public int getTrackState() {
        return (int)table.getNumber("targets", TRACK_NONE);
    }

    /**
     * Gets the calculated distance to the target.
     * @return The distance, or -1 if not tracking a target.
     */
    public double getDistance() {
        return table.getNumber("distance", -1);
    }
}
