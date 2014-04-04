package team2485.comp;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

/**
 * Retroreflective target tracker using the Axis camera.
 * Communicates with the SmartDashboard to obtain tracking and distance info.
 *
 * @author Bryce Matsumori
 */
public class TargetTracker {
    private final NetworkTable table;
    private int autoTrackState = -1;

    public static final int
            TRACK_NONE   = 0,
            TRACK_LEFT   = 1,
            TRACK_RIGHT  = 2;

    public TargetTracker() {
        table = NetworkTable.getTable("vision");
        table.addTableListener("targets", new ITableListener() {
            public void valueChanged(ITable table, String key, Object value, boolean immediate) {
                System.out.println("RECEIVED TRACK STATE " + ((Double)value).intValue());
            }
        }, true);
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

    /**
     * Gets the track state saved for autonomous.
     * @return The track state, or -1 if not set yet.
     */
    public int getAutoTrackState() {
        return autoTrackState;
    }

    /**
     * Save the current track state so we maintain the value throughout autonomous.
     * This is so we don't change directions after starting.
     */
    public void setAutoTrackState() {
        this.autoTrackState = getTrackState();
    }

    /**
     * Reset the autonomous track state.
     */
    public void resetAutoTrackState() {
        this.autoTrackState = -1;
    }
}
