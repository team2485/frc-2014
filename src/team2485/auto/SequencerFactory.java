package team2485.auto;

import team2485.Robot;
import team2485.auto.sequenceditems.ExtendOnePiston;
import team2485.auto.sequenceditems.ExtendThreePistons;
import team2485.auto.sequenceditems.ExtendTwoPistons;
import team2485.auto.sequenceditems.RetractSequence;

/**
 * The sequencer factory instantiates all robot sequences
 *
 * @author Marty Kausas
 * @author Bryce Matsumori
 */
public class SequencerFactory {

    // autonomous constants
    public static final int
            NONE       = 0,
            ONE_BALL   = 1,
            TWO_BALL   = 2,
            THREE_BALL = 3;

    /**
     * Returns any autnomous sequences requested
     *
     * @param type
     * @return {@code Sequencer}
     */
    public static Sequencer createAuto(int type) {
        switch (type) {
            case ONE_BALL:
                return new Sequencer(new SequencedItem[] {

                });
            case TWO_BALL:
                return new Sequencer(new SequencedItem[] {

                });
            case THREE_BALL:
                return new Sequencer(new SequencedItem[] {

                });
            default:
                return new Sequencer(); // return an empty sequence
        }
    }

    // shot constants
    public static final int
            WEAK_SHOT = 0,
            MEDIUM_SHOT = 1,
            STRONG_SHOT = 2;

    /**
     * Returns any shot sequences requested
     *
     * @param type
     * @return {@code Sequencer}
     */
    public static Sequencer createShot(int type) {
    switch (type) {
        case WEAK_SHOT:
            return new Sequencer(new SequencedItem[] {
                new ExtendOnePiston(),
                new RetractSequence()
            });
        case MEDIUM_SHOT:
            return new Sequencer(new SequencedItem[] {
                new ExtendTwoPistons(),
                new RetractSequence()
            });
        case STRONG_SHOT:
            return new Sequencer(new SequencedItem[] {
                new ExtendThreePistons(),
                new RetractSequence()
            });
        default:
            return new Sequencer(); // return an empty sequence
        }
    }
}
