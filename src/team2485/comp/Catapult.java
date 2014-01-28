package team2485.comp;

import edu.wpi.first.wpilibj.Solenoid;
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
            solenoid3;

    /**
     * Default Constructor
     *
     * @param solenoidLeft
     * @param solenoidMiddle
     * @param solenoidRight
     */
    public Catapult(Solenoid solenoidLeft, Solenoid solenoidMiddle, Solenoid solenoidRight) {
        this.solenoid1 = solenoidLeft;
        this.solenoid2 = solenoidMiddle;
        this.solenoid3 = solenoidRight;

    }

    /**
     * Constructs a new Catapult using parameter solenoid ports
     *
     * @param solenoidLeftPort
     * @param solenoidMiddlePort
     * @param solenoidRightPort
     */
    public Catapult(int solenoidLeftPort, int solenoidMiddlePort, int solenoidRightPort) {
        this(new Solenoid(solenoidLeftPort), new Solenoid(solenoidMiddlePort), new Solenoid(solenoidRightPort));
    }

    /**
     * 
     * @param shotConstant
     */
    public void shoot(int shotConstant) {
        SequencerFactory.createShot(shotConstant).run();
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

    /**
     * Retracts all three catapult pistons
     */
    public void retract() {
        solenoid1.set(false);
        solenoid2.set(false);
        solenoid3.set(false);
    }
}
