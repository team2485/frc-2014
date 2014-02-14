package team2485;

import com.kauailabs.nav6.frc.BufferingSerialPort;
import com.kauailabs.nav6.frc.IMUAdvanced;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.visa.VisaException;
import team2485.auto.*;
import team2485.auto.sequenceditems.BallIsSettled;
import team2485.auto.sequenceditems.Drive;
import team2485.auto.sequenceditems.ExtendShoe;
import team2485.auto.sequenceditems.MoveArm;
import team2485.auto.sequenceditems.Rotate;
import team2485.auto.sequenceditems.StopRollers;
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
    public static IMUAdvanced imu;

    private NetworkTable pid;

    private Sequencer autoSequence;

    // WPI Classes
    private Compressor compressor;

    public void robotInit() {
        drive      = new DriveTrain(new Talon(10), new Talon(8), new Encoder(13, 14), new Solenoid(5), new Solenoid(6));
        catapult   = new Catapult(1, 2, 3, 7, 4, new AnalogChannel(2));
        arm        = new IntakeArm(new Talon(9), new Talon(7), new AnalogPotentiometer(1, 1000));

        try {
            imu = new IMUAdvanced(new BufferingSerialPort(57600));
            drive.setImu(imu);
        } catch (VisaException ex) {
            new Thread(new Runnable() {
                public void run() {
                    // Try getting the IMU for 30 * 100 (3 sec)
                    try {
                        for (int i = 0; i < 30 && imu == null; i++) {
                            try {
                                imu = new IMUAdvanced(new BufferingSerialPort(57600));
                                drive.setImu(imu);
                            } catch (VisaException ex) { }

                            Thread.sleep(100);
                        }
                    } catch (InterruptedException ex) { }

                    if (imu == null) System.err.println("Could not connect to the IMU!");
                    else System.out.println("Connected to the IMU");
                }
            }, "IMU Connect").start();
        }

        pid = NetworkTable.getTable("PID");

//        compressor = new Compressor(1, 1);
//        tracker    = new TargetTracker();

        Controllers.set(new Joystick(1), new Joystick(2));
    }

    public void autonomousInit() {
//        int autonomousType = (int) SmartDashboard.getNumber("autoType", SequencerFactory.ONE_BALL);
//        autoSequence = SequencerFactory.createAuto(autonomousType);
//        tracker.resetAutoTrackState();

        drive.resetSensors();

        drive.lowGear();

//        drive.kP_G_Rotate = pid.getNumber("P_Gyro_Rotate");
//        drive.kI_G_Rotate = pid.getNumber("I_Gyro_Rotate");
//        drive.kD_G_Rotate = pid.getNumber("D_Gyro_Rotate");
//
//        drive.kP_G_Drive = pid.getNumber("P_Gyro_Drive");
//        drive.kI_G_Drive = pid.getNumber("I_Gyro_Drive");
//        drive.kD_G_Drive = pid.getNumber("D_Gyro_Drive");
//
//        drive.kP_E = pid.getNumber("P_Encoder_Drive");
//        drive.kI_E = pid.getNumber("I_Encoder_Drive");
//        drive.kD_E = pid.getNumber("D_Encoder_Drive");

        drive.setPIDEnc(drive.kP_E, drive.kI_E, drive.kD_E);
        drive.setPIDGyro(drive.kP_G_Drive, drive.kI_G_Drive, drive.kD_G_Drive);

//        System.out.println(pid.getNumber("P_Encoder_Drive"));

//        autoSequence = new Sequencer(new SequencedItem[] {
//            // wait for target
//            new SequencedDoubleItem(
//                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.WEAK_SHOT)),
//                    new MoveArm(IntakeArm.PICKUP)),
//            new Drive(-10),
//            new SequencedPause(pid.getNumber("P_Encoder_Drive")),
//            new ExtendShoe(),
//            new SequencedTripleItem(
//                    new MoveArm(IntakeArm.IN_CATAPULT),
//                    new StopRollers(),
//                    new SequencedDoubleItem(
//                            new Drive(30),
//                            new InnerSequencer(SequencerFactory.createShot(SequencerFactory.WEAK_SHOT))))
//        });

        autoSequence = new Sequencer(new SequencedItem[] {
            // wait for target
            new SequencedDoubleItem(
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.WEAK_SHOT)),
                    new MoveArm(IntakeArm.PICKUP)),
            new Drive(-10),
            new BallIsSettled(),
            new ExtendShoe(),
            new SequencedTripleItem(
                    new MoveArm(IntakeArm.IN_CATAPULT),
                    new StopRollers(),
                    new SequencedDoubleItem(
                            new Drive(0),
                            new InnerSequencer(SequencerFactory.createShot(SequencerFactory.WEAK_SHOT)))),
            new MoveArm(IntakeArm.PICKUP),
            new Drive(-30),
            new BallIsSettled(),
            new ExtendShoe(),
            new SequencedTripleItem(
                    new MoveArm(IntakeArm.IN_CATAPULT),
                    new StopRollers(),
                    new SequencedDoubleItem(
                            new Drive(45),
                            new InnerSequencer(SequencerFactory.createShot(SequencerFactory.WEAK_SHOT))))
        });


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
                -Controllers.getAxis(Controllers.XBOX_AXIS_LY, 0.2f),
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

        if (Controllers.getJoystickButton(12))
            catapult.setShoe();


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
        SmartDashboard.putNumber("battery", DriverStation.getInstance().getBatteryVoltage());
    }
}
