package team2485;

import edu.wpi.first.wpilibj.Encoder;
import team2485.comp.DriveTrain;

class Locator {
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
}
