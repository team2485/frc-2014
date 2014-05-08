package team2485.util;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Logs messages to the SmartDashboard.
 * Interface based on Android's {@code android.util.Log}, accessible
 * <a href="http://developer.android.com/reference/android/util/Log.html">here</a>.
 *
 * @author Camille Considine
 * @author Bryce Matsumori
 */
public class Log {
    /**
     * Whether the logger should log or not.
     */
    public static boolean enabled = false;

    private static StringBuffer logBuffer = new StringBuffer();

    private Log() {
    }

    /**
     * Adds a line to the log.
     * @param s The text.
     */
    public static void addString(String s) {
        if (enabled) logBuffer.append(s).append('\n');
    }

    /**
     * Adds a line to the log.
     * @param type The message type.
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void addString(String type, String tag, String msg) {
        if (enabled) logBuffer.append(type).append('\t').append(tag).append('\t').append(msg).append('\n');
    }

    /**
     * Sends the accumulated log messages to the SmartDashboard.
     */
    public static void send() {
        if (logBuffer.length() > 0) {
            SmartDashboard.putString("log", logBuffer.toString() );
            logBuffer.setLength(0);
        }
    }

    /**
     * Send a debug log message.
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void d(String tag, String msg) {
        addString("debug", tag, msg);
    }

    /**
     * Send an error log message.
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void e(String tag, String msg) {
        addString("error", tag, msg);
    }

    /**
     * Send a warning log message.
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void w (String tag, String msg) {
        addString("warn", tag, msg);
    }

    /**
     * Send an info log message.
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void i(String tag, String msg) {
        addString("info", tag, msg);
    }
}
