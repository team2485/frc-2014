package team2485.comp;

import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Relay;
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
    public static int FULLY_RETRACTED   = 1,
            SHORT_EXTENDED              = 2,
            LONG_EXTENDED               = 3,
            FULLY_EXTENDED              = 4;

    private int currentShoeState = 1;

    private Solenoid
            solenoidShoeShort,
            solenoidShoeLong,
            solenoidBoot;

    private Relay
            centerSolenoid,
            sideSolenoids;

    private Sequencer shootSequencer;
    private AnalogChannel sonic;

    /**
     * Constructor using {@code Solenoid} objects
     *
     * @param sideSolenoids
     * @param solenoidMiddle
     * @param solenoidShoeAdjuster1
     * @param solenoidShoeAdjuster2
     * @param solenoidBoot
     * @param sonic
     */
    public Catapult(Relay sideSolenoids, Relay solenoidMiddle, Solenoid solenoidShoeAdjuster1, Solenoid solenoidShoeAdjuster2, Solenoid solenoidBoot, AnalogChannel sonic) {
        this.sideSolenoids     = sideSolenoids;
        this.centerSolenoid    = solenoidMiddle;
        this.solenoidShoeShort = solenoidShoeAdjuster1;
        this.solenoidShoeLong  = solenoidShoeAdjuster2;
        this.solenoidBoot      = solenoidBoot;
        this.sonic             = sonic;
    }

    /**
     * Constructs a new Catapult using parameter solenoid ports
     *
     * @param sideSolenoids
     * @param solenoidMiddlePort
     * @param solenoidShoeAdjusterPort1
     * @param solenoidShoeAdjusterPort2
     * @param solenoidBoot
     * @param sonic
     */
    public Catapult(int sideSolenoids, int solenoidMiddlePort, int solenoidShoeAdjusterPort1, int solenoidShoeAdjusterPort2, int solenoidBoot, AnalogChannel sonic) {
        this(new Relay(sideSolenoids), new Relay(solenoidMiddlePort), new Solenoid(solenoidShoeAdjusterPort1), new Solenoid(solenoidShoeAdjusterPort2), new Solenoid(solenoidBoot), sonic);
    }

    /**
     *
     * @param shotType
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
        sideSolenoids.set(Relay.Value.kOff);
        centerSolenoid.set(Relay.Value.kForward);
    }

    /**
     * Extends the left and right catapult pistons
     */
    public void extendTwo() {
        sideSolenoids.set(Relay.Value.kOn);
        centerSolenoid.set(Relay.Value.kOff);
    }

    /**
     * Extends all three catapult pistons
     */
    public void extendThree() {
        sideSolenoids.set(Relay.Value.kOn);
        centerSolenoid.set(Relay.Value.kForward);
    }

    public void extendRightPiston() {
        sideSolenoids.set(Relay.Value.kOff);
        centerSolenoid.set(Relay.Value.kForward);
    }

    /**
     * Retracts all three catapult pistons
     */
    public void retract() {
        sideSolenoids.set(Relay.Value.kOff);
        centerSolenoid.set(Relay.Value.kOff);
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
            // do nothing
        } else {
            currentShoeState = state;
            switch (currentShoeState) {
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
        return currentShoeState;
    }
}
