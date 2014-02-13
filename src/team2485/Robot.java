package team2485;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import team2485.auto.*;
import team2485.comp.*;
import team2485.util.Controllers;

/**
 * Team 2485's code for our 2014 FRC season.
 * Robot Name: Odin
 *
 * @author Bryce Matsumori
 * @author Marty Kausas
 * @author Anoushka Bose
 * @author Camille Considine
 * @author Mike Maunu
 */
public class Robot extends IterativeRobot {

    // Subsystems
    public static DriveTrain drive;
    public static Catapult catapult;
    public static IntakeArm arm;
    public static TargetTracker tracker;

    private Sequencer autoSequence;

    // WPI Classes
    private Compressor compressor;

    public void robotInit() {
        drive      = new DriveTrain(new Talon(10), new Talon(8), new Encoder(13, 14), new Solenoid(5), new Solenoid(6));
        catapult   = new Catapult(1, 2, 3, 8, 4);
        arm        = new IntakeArm(new Talon(9), new Talon(7), new AnalogPotentiometer(1, 1000));

        compressor = new Compressor(1, 1);
        tracker    = new TargetTracker();

        Controllers.set(new Joystick(1), new Joystick(2));
    }

    public void autonomousInit() {
        int autonomousType = (int) SmartDashboard.getNumber("autoType", SequencerFactory.ONE_BALL);
        autoSequence = SequencerFactory.createAuto(autonomousType);
        tracker.resetAutoTrackState();
    }

    public void autonomousPeriodic() {
        autoSequence.run();
        globalPeriodic();
    }

    public void teleopInit() {
        arm.stopRollers();
    }

    public void teleopPeriodic() {

        //<editor-fold defaultstate="collapsed" desc="Driver Controls">
        // --- START DRIVER CONTROLS --- //
        // TODO: Figure out threshold values
        drive.warlordDrive(
                Controllers.getAxis(Controllers.XBOX_AXIS_LY, 0.2f),
                Controllers.getAxis(Controllers.XBOX_AXIS_RX, 0.2f));

        // Controlling speeds with XBOX LT & RT
        if (Controllers.getAxis(Controllers.XBOX_AXIS_TRIGGER) > 0) {
            drive.setLowSpeed();
        } else if (Controllers.getAxis(Controllers.XBOX_AXIS_TRIGGER) < 0) {
            drive.setHighSpeed();
        } else {
            drive.setNormalSpeed();
        }

        // Gear Shifting
        if (Controllers.getButton(Controllers.XBOX_BTN_A)) {
            drive.lowGear();
        } else if (Controllers.getButton(Controllers.XBOX_BTN_B)) {
            drive.highGear();
        }

        // Quick turn
        if (Controllers.getButton(Controllers.XBOX_BTN_RBUMP))
            drive.setQuickTurn(true);
        else
            drive.setQuickTurn(false);

        // Driver pickup controls
        if (Controllers.getButton(Controllers.XBOX_BTN_X))
            arm.setSetpoint(IntakeArm.PICKUP);

        // --- END DRIVER CONTROLS --- //
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Operator Controls">
        // --- START OPERATOR CONTROL --- //
        // Shooting controls
        if (Controllers.getJoystickButton(11)) {
            if (Controllers.getJoystickButton(1))
                catapult.shoot(SequencerFactory.STRONG_SHOT);
            else if (Controllers.getJoystickButton(2))
                catapult.shoot(SequencerFactory.MEDIUM_SHOT);
            else if (Controllers.getJoystickButton(3)) {
                System.out.println("weak shot");
                catapult.shoot(SequencerFactory.WEAK_SHOT);
            }
            else if (Controllers.getJoystickButton(9))
                catapult.shoot(SequencerFactory.SHORT_PASS);
        }

        // Arm position controls
        if (Controllers.getJoystickButton(4))
            arm.setSetpoint(IntakeArm.IN_CATAPULT);
        else if (Controllers.getJoystickButton(5))
            arm.setSetpoint(IntakeArm.UP_POSITION);
        else if (Controllers.getJoystickButton(6))
            arm.setSetpoint(IntakeArm.PICKUP);

        else if (Controllers.getJoystickButton(7))
            arm.startRollers(IntakeArm.ROLLERS_FORWARD);
        else if (Controllers.getJoystickButton(8))
            arm.stopRollers();

        // TODO: Figure out if needed...
//        if (Math.abs(Controllers.getJoystickAxis(Controllers.JOYSTICK_AXIS_THROTTLE)) > 0.2)
//            arm.startRollers(Controllers.getJoystickAxis(Controllers.JOYSTICK_AXIS_THROTTLE));


        arm.moveArm(Controllers.getJoystickAxis(Controllers.JOYSTICK_AXIS_Y, 0.2f));
        // --- END OPERATOR CONTROL --- //
        //</editor-fold>

        // Arm updates
        arm.run();
        catapult.run();
        globalPeriodic();
    }

    public void testPeriodic() {
        compressor.start();
        globalPeriodic();
    }

    public void globalPeriodic() {
//        System.out.println("Pot value: " + arm.getPotValue());
    }
}
