package team2485;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import team2485.auto.*;
import team2485.comp.*;

/**
 * Team 2485's code for our 2013 FRC season.
 * Robot Name: ??UNKNOWN??
 *
 * @author Bryce Matsumori
 * @author Marty Kausas
 */
public class Robot extends IterativeRobot {

    // Custom Classes
    public static DriveTrain drive;
    public static Catapult catapult;
    private Sequencer autoSequence;

    public void robotInit() {
        drive       = new DriveTrain(1, 2, 1, 1, 2);
        catapult    = new Catapult(1, 2, 3);
    }

    public void autonomousInit() {
        int autonomousType = (int) SmartDashboard.getNumber("autoType", SequencerFactory.NONE);
        autoSequence = SequencerFactory.createAuto(autonomousType);
    }

    public void autonomousPeriodic() {
        autoSequence.run();
        globalPeriodic();
    }

    public void teleopInit() {
    }

    public void teleopPeriodic() {
        globalPeriodic();
    }

    public void testPeriodic() {
        globalPeriodic();
    }

    public void globalPeriodic() {

    }
}
