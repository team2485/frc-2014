package team2485;

import com.kauailabs.nav6.frc.BufferingSerialPort;
import com.kauailabs.nav6.frc.IMU;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.visa.VisaException;
import team2485.auto.*;
import team2485.comp.*;
import team2485.util.Controllers;

/**
 * Team 2485's code for the 2014 FRC season.
 * Robot Name: Odin
 *
 * @author Bryce Matsumori
 * @author Marty Kausas
 * @author Anoushka Bose
 * @author Camille Considine
 * @author Aidan Fay
 * @author Mike Maunu
 */
public class Robot extends IterativeRobot {
    private static final byte IMU_UPDATE_RATE = 30;
    private static final boolean USE_G13 = false;

    // Subsystems
    public static DriveTrain drive;
    public static Catapult catapult;
    public static IntakeArm arm;
    public static Catcher catcher;
    public static TargetTracker tracker;
    public static Locator locator;

    public static IMU imu;
    private Compressor compressor;
    private PressureTransducer pressureTransducer;
    private Encoder leftEncoder, rightEncoder;

    // Autonomous
    private Sequencer autoSequence;
    public static boolean errorInAutonomous;


    public void robotInit() {
        leftEncoder  = new Encoder(13, 14);

        drive             = new DriveTrain(new Talon(10), new Talon(8), leftEncoder, new Solenoid(5));
        catapult          = new Catapult(8, 7, 4, 6, 8, new AnalogChannel(2));
        arm               = new IntakeArm(new Talon(9), new Talon(7), new AnalogPotentiometer(6, 1000));
        catcher           = new Catcher(3);
        errorInAutonomous = false;

        try {
            imu = new IMU(new BufferingSerialPort(57600), IMU_UPDATE_RATE);
            drive.setImu(imu);
        } catch (VisaException ex) {
            new Thread(new Runnable() {
                public void run() {
                    // Try getting the IMU for 30 * 100 (3 sec)
                    try {
                        for (int i = 0; i < 30 && imu == null; i++) {
                            try {
                                imu = new IMU(new BufferingSerialPort(57600));
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

        compressor = new Compressor(1, 1);
        tracker    = new TargetTracker();
        pressureTransducer = new PressureTransducer(3);

        Controllers.set(new Joystick(1), new Joystick(2));
    }

    public void autonomousInit() {
        int autonomousType = (int) SmartDashboard.getNumber("autoMode", SequencerFactory.TWO_BALL_HOT);
        autoSequence = SequencerFactory.createAuto(autonomousType);

        catapult.reset();
        tracker.resetAutoTrackState();
        drive.resetSensors();
        drive.lowGear();

    }

    public void autonomousPeriodic() {
        autoSequence.run();
        globalPeriodic();

    }

    public void teleopInit() {
        errorInAutonomous = false;

        catapult.reset();
        arm.stopRollers();
    }

    private boolean prevOperatorBtn7 = false;
    private boolean prevOperatorBtn13 = false;
    private boolean prevOperatorBtn14 = false;
    public void teleopPeriodic() {
        // <editor-fold defaultstate="collapsed" desc="Driver Controls">

        drive.warlordDrive(
                Controllers.getAxis(Controllers.XBOX_AXIS_LY, 0.2f),
                Controllers.getAxis(Controllers.XBOX_AXIS_RX, 0.2f));

        // Quick turn
        if (Controllers.getButton(Controllers.XBOX_BTN_RBUMP)) {
            drive.setQuickTurn(true);
        } else {
            drive.setQuickTurn(false);
        }

        if (Controllers.getButton(Controllers.XBOX_BTN_LBUMP)) {
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

        // Driver pickup controls
        if (Controllers.getButton(Controllers.XBOX_BTN_X)) {
            arm.setSetpoint(IntakeArm.PICKUP);
        }

        // </editor-fold>


        // <editor-fold defaultstate="collapsed" desc="Operator Controls">

        // Shooting controls
        if (Controllers.getJoystickButton(24)) {
            if (Controllers.getJoystickButton(1)) {
                catapult.shoot(SequencerFactory.TARGET_SHOT);
            } else if (Controllers.getJoystickButton(2)) {
                catapult.shoot(SequencerFactory.TRUSS_SHOT);
            } else if (Controllers.getJoystickButton(3)) {
                catapult.shoot(SequencerFactory.BOOT);
            } else if (Controllers.getJoystickButton(5)) {
                catapult.shoot(SequencerFactory.MIDRANGE_SHOT_THREE_CYLINDER);
            } else if (Controllers.getJoystickButton(10)) {
                catapult.shoot(SequencerFactory.OVER_TRUSS_CATCH);
            } else if (Controllers.getJoystickButton(6)) {
                catapult.shoot(SequencerFactory.BLOOP_SHOT);
            }
        }

        if (!prevOperatorBtn14 && Controllers.getJoystickButton(14)) {
            catapult.setShoeState(catapult.getShoeState() + 1);
        }
        if (!prevOperatorBtn13 && Controllers.getJoystickButton(13)) {
            catapult.setShoeState(catapult.getShoeState() - 1);
        }

        prevOperatorBtn13 = Controllers.getJoystickButton(13);
        prevOperatorBtn14 = Controllers.getJoystickButton(14);

        // Arm position controls
        if (Controllers.getJoystickButton(4)) {
            arm.setSetpoint(IntakeArm.IN_CATAPULT);
        } else if (Controllers.getJoystickButton(9)) {
            arm.setSetpoint(IntakeArm.PICKUP);
        }
        else if (!prevOperatorBtn7 && Controllers.getJoystickButton(7)) {
            arm.stopRollers();
        }
        prevOperatorBtn7 = Controllers.getJoystickButton(7);

        // Catcher logic
        if (Controllers.getJoystickAxis(4) > 0) {
            catcher.extend();
        } else {
            catcher.retract();
        }

        arm.moveArm(-Controllers.getJoystickAxis(Controllers.JOYSTICK_AXIS_Y, 0.2f));

        // Arm updates
        arm.run();
        catapult.run();
        globalPeriodic();
    }

    public void testPeriodic() {
        compressor.start();

        if (Controllers.getJoystickButton(1))
            arm.setSetpoint(IntakeArm.STARTING_CONFIG);

        globalPeriodic();
    }

    public void disabledPeriodic() {
        globalPeriodic();
    }

    public void globalPeriodic() {

        // pot value and the rollers + whether the motor is running
        int armPotWidgetVal = (int)arm.getPotValue() * 10 + (arm.rollersOn() ? 1 : 0);
        SmartDashboard.putString("ArmPot", Double.toString(IntakeArm.UP_POSITION) + "," + armPotWidgetVal);

        SmartDashboard.putNumber("pressure", pressureTransducer.getPressure());

        // DS info
        SmartDashboard.putNumber("battery", DriverStation.getInstance().getBatteryVoltage());
        SmartDashboard.putBoolean("disabled", DriverStation.getInstance().isDisabled());
    }
}
