package team2485;

import com.kauailabs.nav6.frc.BufferingSerialPort;
import com.kauailabs.nav6.frc.IMU;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.visa.VisaException;
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
    public static IMU imu;
    private final static byte IMU_UPDATE_RATE = 30;
    public static Encoder leftEncoder, rightEncoder;
    public static Locator locator;
    public static boolean errorInAutonomous;
    public int potArmSmart; // Variable for the smartdashboard output for the potentiometer value and the rollers
    public int talArmSmart; // Variable for the smartdashboard output for whether the motor is running

    private static final boolean USE_G13 = false;

    private NetworkTable pid;

    private Sequencer autoSequence;

    // WPI Classes
    private Compressor compressor;

    private Relay ringLightRelay;
    private LightController lightController;
    private PressureTransducer pressureTransducer;

    public void robotInit() {
        leftEncoder  = new Encoder(13, 14);
        rightEncoder = new Encoder(12, 11);

        drive             = new DriveTrain(new Talon(10), new Talon(8), rightEncoder, new Solenoid(5));
        catapult          = new Catapult(1, 2, 3, 6, 7, new AnalogChannel(2));
        locator           = new Locator(leftEncoder, rightEncoder, drive);
        arm               = new IntakeArm(new Talon(9), new Talon(7), new AnalogPotentiometer(5, 1000));
        errorInAutonomous = false;
        lightController   = new LightController(new Relay(7), new Relay(6), new Relay(5), new Relay(4), new Relay(3)); // white red black black red


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

        pid = NetworkTable.getTable("PID");

        compressor = new Compressor(1, 1);
        ringLightRelay = new Relay(8);
        tracker    = new TargetTracker();
        pressureTransducer = new PressureTransducer(3);

        Controllers.set(new Joystick(1), new Joystick(2));
    }

    public void autonomousInit() {
        ringLightRelay.set(Relay.Value.kReverse);
        int autonomousType = (int) SmartDashboard.getNumber("autoMode", SequencerFactory.ONE_BALL_LEFT);
        autoSequence = SequencerFactory.createAuto(autonomousType);

        catapult.reset();
        tracker.resetAutoTrackState();
        drive.resetSensors();
        drive.lowGear();

        locator.setAutoPosition(autonomousType);

        ringLightRelay.set(Relay.Value.kReverse);

//        drive.kP_G_Rotate = pid.getNumber("P_Gyro_Rotate");
//        drive.kI_G_Rotate = pid.getNumber("I_Gyro_Rotate");
//        drive.kD_G_Rotate = pid.getNumber("D_Gyro_Rotate");
//        drive.kP_G_Drive = pid.getNumber("P_Gyro_Drive");
//        drive.kI_G_Drive = pid.getNumber("I_Gyro_Drive");
//        drive.kD_G_Drive = pid.getNumber("D_Gyro_Drive");
//        drive.kP_E = pid.getNumber("P_Encoder_Drive");
//        drive.kI_E = pid.getNumber("I_Encoder_Drive");
//        drive.kD_E = pid.getNumber("D_Encoder_Drive");
//        drive.setPIDEnc(drive.kP_E, drive.kI_E, drive.kD_E);
//        drive.setPIDGyro(drive.kP_G_Drive, drive.kI_G_Drive, drive.kD_G_Drive);
    }

    public void autonomousPeriodic() {
        autoSequence.run();
        globalPeriodic();

        lightController.send(LightController.HAPPY_RAINBOW);
    }

    public void teleopInit() {
        errorInAutonomous = false;

        ringLightRelay.set(Relay.Value.kOff);

        catapult.reset();
        arm.stopRollers();
    }

    private boolean prevOperatorBtn7 = false;
    public void teleopPeriodic() {
        //<editor-fold defaultstate="collapsed" desc="Driver Controls">
        // --- START DRIVER CONTROLS --- //
        drive.warlordDrive(
                Controllers.getAxis(Controllers.XBOX_AXIS_LY, 0.2f),
                Controllers.getAxis(Controllers.XBOX_AXIS_RX, 0.2f));

        // Quick turn
        if (Controllers.getButton(Controllers.XBOX_BTN_RBUMP))
            drive.setQuickTurn(true);
        else
            drive.setQuickTurn(false);

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
            lightController.send(LightController.INTAKE);
        }

        // --- END DRIVER CONTROLS --- //
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Operator Controls">
        // --- START OPERATOR CONTROL --- //
        // Shooting controls
        if (Controllers.getG13OrJoyButton(24)) {
            if (Controllers.getJoystickButton(1)) {
                catapult.shoot(SequencerFactory.TARGET_SHOT);
//                lightController.send(LightController.RAINBOW_CHASE);
            } else if (Controllers.getJoystickButton(2)) {
                catapult.shoot(SequencerFactory.TRUSS_SHOT);
//                lightController.send(LightController.RAINBOW_CHASE);
            } else if (Controllers.getJoystickButton(3)) {
                catapult.shoot(SequencerFactory.BOOT);
//                lightController.send(LightController.RAINBOW_CHASE);
            } else if (Controllers.getJoystickButton(5)) {
                catapult.shoot(SequencerFactory.FORWARD_PASS);
//                lightController.send(LightController.RAINBOW_CHASE);
            } else if (Controllers.getG13OrJoyButton(4)) {
                catapult.shoot(SequencerFactory.POWER_HIGH_SHOT);
//                lightController.send(LightController.RAINBOW_CHASE);
            }
        }

//        if (!prevJoystick11 && Controllers.getG13OrJoyButton(13))
//            catapult.toggleShoe();

        if (Controllers.getG13OrJoyButton(14))
            catapult.extendShoe();
        if (Controllers.getG13OrJoyButton(13))
            catapult.retractShoe();

        // Arm position controls
        if (Controllers.getJoystickButton(4)) {
            arm.setSetpoint(IntakeArm.IN_CATAPULT);
            lightController.send(LightController.RAINBOW_CYCLE);
        } else if (Controllers.getJoystickButton(6))
            arm.setSetpoint(IntakeArm.UP_POSITION);
        else if (Controllers.getJoystickButton(9)) {
            arm.setSetpoint(IntakeArm.PICKUP);
            lightController.send(LightController.GOLD_CHASE);
        }

        else if (!prevOperatorBtn7 && Controllers.getG13OrJoyButton(7))
            arm.stopRollers();
        prevOperatorBtn7 = Controllers.getG13OrJoyButton(7);

        arm.moveArm(-Controllers.getJoystickAxis(Controllers.JOYSTICK_AXIS_Y, 0.2f));
        // --- END OPERATOR CONTROL --- //
        //</editor-fold>

        switch ((int) DriverStation.getInstance().getMatchTime()) {
            case 45:
                lightController.send(LightController.GOLD);
                break;
            case 75:
                lightController.send(LightController.GREEN);
                break;
            case 105:
                lightController.send(LightController.BLUE);
                break;
            case 135:
                lightController.send(LightController.RED);
                break;
        }


        // Arm updates
        arm.run();
        catapult.run();
        globalPeriodic();
        lightController.send(LightController.HAPPY_RAINBOW);
    }

    public void testPeriodic() {
        compressor.start();

        ringLightRelay.set(Relay.Value.kReverse);

        globalPeriodic();
    }

    public void disabledPeriodic() {
        globalPeriodic();
    }

    public void globalPeriodic() {
        if (arm.rollersOn()) {
            talArmSmart = 1;
        } else {
            talArmSmart = 0;
        }
        potArmSmart = (int) (arm.getPotValue()) * 10 + talArmSmart;
        SmartDashboard.putNumber("ArmPot" , potArmSmart);
        System.out.println("Pot value = " + arm.getPotValue());

        locator.run();
        SmartDashboard.putString("field", locator.getX() + "," + locator.getY() + "," + (imu == null ? 0 : imu.getYaw()) + ",false");

        SmartDashboard.putNumber("pressure", pressureTransducer.getPressure());

        // DS info
        SmartDashboard.putNumber("battery", DriverStation.getInstance().getBatteryVoltage());
        SmartDashboard.putBoolean("disabled", DriverStation.getInstance().isDisabled());
    }
}
