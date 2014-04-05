package team2485.comp;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Solenoid;
import team2485.auto.Sequencer;
import team2485.auto.SequencerFactory;

/**
 * Represents the catapult
 *
 * @author Camille Considine
 * @author Marty Kausas
 */
public class Catapult {

    private Solenoid
            sideSolenoids,
            centerSolenoid,
            solenoid3,
            solenoidShoeShort,
            solenoidShoeLong,
            solenoidBoot;

    public int CURRENT_SHOE_STATE  = 1;
    public static int FULLY_RETRACTED   = 1,
            SHORT_EXTENDED              = 2,
            LONG_EXTENDED               = 3,
            FULLY_EXTENDED              = 4;



    private Sequencer shootSequencer;
    private AnalogChannel sonic;

    /**
     * Constructor using {@code Solenoid} objects
     *
     * @param solenoidLeft
     * @param solenoidMiddle
     * @param solenoidRight
     * @param solenoidShoeAdjuster
     * @param solenoidBoot
     */
    public Catapult(Solenoid sideSolenoids, Solenoid solenoidMiddle, Solenoid solenoidShoeAdjuster1, Solenoid solenoidShoeAdjuster2, Solenoid solenoidBoot, AnalogChannel sonic) {
        this.sideSolenoids          = sideSolenoids;
        this.centerSolenoid         = solenoidMiddle;
        this.solenoidShoeShort      = solenoidShoeAdjuster1;
        this.solenoidShoeLong       = solenoidShoeAdjuster2;
        this.solenoidBoot           = solenoidBoot;
        this.sonic                  = sonic;
    }

    /**
     * Constructs a new Catapult using parameter solenoid ports
     *
     * @param solenoidLeftPort
     * @param solenoidMiddlePort
     * @param solenoidRightPort
     * @param solenoidShoeAdjusterPort
     */
    public Catapult(int sideSolenoids, int solenoidMiddlePort, int solenoidShoeAdjusterPort1, int solenoidShoeAdjusterPort2, int solenoidBoot, AnalogChannel sonic) {
        this(new Solenoid(sideSolenoids), new Solenoid(solenoidMiddlePort), new Solenoid(solenoidShoeAdjusterPort1), new Solenoid(solenoidShoeAdjusterPort2), new Solenoid(solenoidBoot), sonic);
    }

    /**
     *
     * @param shotConstant
     */
    public void shoot(int shotType) {
        if (shootSequencer == null) {
            shootSequencer = SequencerFactory.createShot(shotType);
        }
    }

    public void run() {
        if (shootSequencer != null) {
            if (shootSequencer.run()) shootSequencer = null;
        }
    }

    /**
     * Extends the center catapult piston
     */
    public void extendOne() {
        sideSolenoids.set(false);
        centerSolenoid.set(true);
    }

    /**
     * Extends the left and right catapult pistons
     */
    public void extendTwo() {
        sideSolenoids.set(true);
        centerSolenoid.set(false);
    }

    /**
     * Extends all three catapult pistons
     */
    public void extendThree() {
        sideSolenoids.set(true);
        centerSolenoid.set(true);
    }

    public void extendRightPiston() {
        sideSolenoids.set(false);
        centerSolenoid.set(false);
    }

    /**
     * Retracts all three catapult pistons
     */
    public void retract() {
        sideSolenoids.set(false);
        centerSolenoid.set(false);
    }

    public void toggleShoe() {
        solenoidShoeShort.set(!solenoidShoeShort.get());
    }


    /**
     * Puts the shoe piston into the intake position
     */
    public void extendShoeFull() {
        setShoeState(FULLY_EXTENDED);
    }

    /**
     * Puts the shoe piston into the shooting position
     */
    public void retractShoeFull() {
        setShoeState(FULLY_RETRACTED);
    }

    public void extendShoeLongPiston() {
        setShoeState(LONG_EXTENDED);
    }

    public void extendShoeShortPiston() {
        setShoeState(SHORT_EXTENDED);
    }

    /**
     * Extends the boot for a close pass
     */
    public void extendBoot() {
        solenoidBoot.set(true);
    }

    /**
     * Retracts the boot after a close pass
     */
    public void retractBoot() {
        solenoidBoot.set(false);
    }

    public boolean inCatapult() {
        System.out.println("Sonic value " + sonic.getValue());
        return sonic.getValue() < 20; // was 25
    }

    public boolean shoeShortExtended() {
        return solenoidShoeShort.get();
    }

    public boolean shoeLongExtended() {
        return solenoidShoeLong.get();
    }

    public void reset() {
        retract();
        retractBoot();
        retractShoeFull();
    }

    public void setShoeState(int state) {
        if (state < FULLY_RETRACTED || state > FULLY_EXTENDED) {
            ; // do nothing
        } else {
            CURRENT_SHOE_STATE = state;
            switch (CURRENT_SHOE_STATE) {
                // fully retracted
                case 1:
                    solenoidShoeShort.set(false);
                    solenoidShoeLong.set(false);
                    break;
                // short piston extended
                case 2:
                    solenoidShoeShort.set(true);
                    solenoidShoeLong.set(false);
                    break;
                // long piston extended
                case 3:
                    solenoidShoeShort.set(false);
                    solenoidShoeLong.set(true);
                    break;
                // full shoe extended
                case 4:
                    solenoidShoeShort.set(true);
                    solenoidShoeLong.set(true);
                    break;
            }
        }
    }

    public int getShoeState() {
        return CURRENT_SHOE_STATE;
    }
}
