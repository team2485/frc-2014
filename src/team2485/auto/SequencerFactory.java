package team2485.auto;

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
     * Returns any sequences requested
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
}
