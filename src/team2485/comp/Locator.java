package team2485.comp;

import edu.wpi.first.wpilibj.Encoder;
import team2485.auto.SequencerFactory;

public class Locator {
    private final Encoder leftEncoder;
    private final Encoder rightEncoder;
    private double lastLeftDist, lastRightDist;
    private final DriveTrain drive;
    private double x, y;

    private static final double DRIVE_TRAIN_WIDTH = 1.0;

    public Locator(Encoder leftEncoder, Encoder rightEncoder, DriveTrain drive) {
        this.leftEncoder  = leftEncoder;
        this.rightEncoder = rightEncoder;
        this.drive = drive;
    }

    public void run() {
        double dist     = (leftEncoder.getDistance() - lastLeftDist + rightEncoder.getDistance() - lastRightDist) * 0.5,
               dtAngle  = drive.getAngle(),
               encAngle = (rightEncoder.getDistance() - lastRightDist - (leftEncoder.getDistance() - lastLeftDist)) / DRIVE_TRAIN_WIDTH;

        lastLeftDist  = leftEncoder.getDistance();
        lastRightDist = rightEncoder.getDistance();

        dtAngle  *= Math.PI / 180;
        encAngle *= Math.PI / 180;

        x += dist * Math.cos(dtAngle);
        y += dist * Math.sin(dtAngle);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setAutoPosition(int autoType) {
        switch (autoType) {
            case SequencerFactory.NONE:
            case SequencerFactory.FORWARD:
            case SequencerFactory.FORWARD_TRUSS:
            case SequencerFactory.FORWARD_CUSTOM:
            case SequencerFactory.TWO_BALL_HOT:
                x = 0;
                y = 96; // 9ft - ~1ft = 8ft
                break;

            case SequencerFactory.ONE_BALL_LEFT:
            case SequencerFactory.ONE_BALL_TRUSS_LEFT:
            case SequencerFactory.ONE_BALL_FROM_LEFT_TO_CENTER_TRUSS:
            case SequencerFactory.ONE_BALL_ANGLED_SHOT_LEFT:
            case SequencerFactory.ONE_BALL_CUSTOM_LEFT:
            // treat these as left for now
            case SequencerFactory.THREE_BALL:
            case SequencerFactory.TWO_BALL_NO_HOT:
                x = -72; // -6ft
                y = 96;
                break;

            case SequencerFactory.ONE_BALL_RIGHT:
            case SequencerFactory.ONE_BALL_TRUSS_RIGHT:
            case SequencerFactory.ONE_BALL_FROM_RIGHT_TO_CENTER_TRUSS:
            case SequencerFactory.ONE_BALL_ANGLED_SHOT_RIGHT:
                x = 72; // 6ft
                y = 96;
                break;
        }
    }
}