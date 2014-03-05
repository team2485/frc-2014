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
            solenoid1,
            solenoid2,
            solenoid3,
            solenoidShoeAdjuster,
            solenoidBoot;

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
    public Catapult(Solenoid solenoidLeft, Solenoid solenoidMiddle, Solenoid solenoidRight, Solenoid solenoidShoeAdjuster, Solenoid solenoidBoot, AnalogChannel sonic) {
        this.solenoid1              = solenoidLeft;
        this.solenoid2              = solenoidMiddle;
        this.solenoid3              = solenoidRight;
        this.solenoidShoeAdjuster   = solenoidShoeAdjuster;
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
    public Catapult(int solenoidLeftPort, int solenoidMiddlePort, int solenoidRightPort, int solenoidShoeAdjusterPort, int solenoidBoot, AnalogChannel sonic) {
        this(new Solenoid(solenoidLeftPort), new Solenoid(solenoidMiddlePort), new Solenoid(solenoidRightPort), new Solenoid(solenoidShoeAdjusterPort), new Solenoid(solenoidBoot), sonic);
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
        solenoid1.set(false);
        solenoid2.set(true);
        solenoid3.set(false);
    }

    /**
     * Extends the left and right catapult pistons
     */
    public void extendTwo() {
        solenoid1.set(true);
        solenoid2.set(false);
        solenoid3.set(true);
    }

    /**
     * Extends all three catapult pistons
     */
    public void extendThree() {
        solenoid1.set(true);
        solenoid2.set(true);
        solenoid3.set(true);
    }

    public void extendRightPiston() {
        solenoid1.set(false);
        solenoid2.set(false);
        solenoid3.set(true);
    }

    /**
     * Retracts all three catapult pistons
     */
    public void retract() {
        solenoid1.set(false);
        solenoid2.set(false);
        solenoid3.set(false);
    }

    public void toggleShoe() {
        solenoidShoeAdjuster.set(!solenoidShoeAdjuster.get());
    }

    /**
     * Puts the shoe piston into the intake position
     */
    public void extendShoe() {
        solenoidShoeAdjuster.set(true);
    }

    /**
     * Puts the shoe piston into the shooting position
     */
    public void retractShoe() {
        solenoidShoeAdjuster.set(false);
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
        if(sonic.getValue() < 20)
            return true;
        else
            return false;
    }

    public boolean shoeExtended() {
        return solenoidShoeAdjuster.get();
    }

    public void reset() {
        retract();
        retractBoot();
        retractShoe();
    }
}
