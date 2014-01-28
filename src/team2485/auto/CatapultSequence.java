package team2485.auto;

import team2485.Robot;

/**
 * Sequenced Item specialized for shooting
 *
 * @author Camille Considine
 */
public class CatapultSequence implements SequencedItem {

    private boolean done = false;

    /**
     * Default constructor
     *
     * @param feet
     */
    public CatapultSequence(double feet) {
    }

    public void run() {
        done = Robot.shoot() ;
    }

    public double duration() {
        return done ? 0 : Double.MAX_VALUE;
    }

}
