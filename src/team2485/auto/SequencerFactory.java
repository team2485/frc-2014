package team2485.auto;

import team2485.auto.sequenceditems.*;

/**
 * The sequencer factory instantiates all robot sequences
 *
 * @author Marty Kausas
 * @author Bryce Matsumori
 */
public class SequencerFactory {
    public static final int
            // Autonomous options
            // TODO: Figure out actual autonomous options
            NONE        = 0,
            ONE_BALL    = 1,
            TWO_BALL    = 2,
            THREE_BALL  = 3,

            // Shot options
            WEAK_SHOT   = 0,
            MEDIUM_SHOT = 1,
            STRONG_SHOT = 2;

    private static final double TARGET_FLIP_PAUSE_TIME = 0.8;

    /**
     * Creates the requested autonomous sequence.
     *
     * @param type The autonomous type.
     * @return The created {@code Sequencer}.
     */
    public static Sequencer createAuto(int type) {
        // TODO: Create all autonomous sequences
        switch (type) {
            case ONE_BALL:
                return new Sequencer(new SequencedItem[] {
                    new SequencedPause(TARGET_FLIP_PAUSE_TIME), // wait until the targets have flipped
                    new WaitForTargets(),
                    new TurnToTarget(),
                    new InnerSequencer(createShot(MEDIUM_SHOT)),
                    new Rotate(0.0) // rotate back to center
                });

            case TWO_BALL:
                return new Sequencer(new SequencedItem[] {
                });

            case THREE_BALL:
                return new Sequencer(new SequencedItem[] {
                });

            default: return new Sequencer(); // return an empty sequence
        }
    }


    /**
     * Creates the requested shot sequence.
     *
     * @param type The shot type.
     * @return The created {@code Sequencer}.
     */
    public static Sequencer createShot(int type) {
        switch (type) {
            case WEAK_SHOT:
                return new Sequencer(new SequencedItem[] {
                    new ExtendOnePiston(),
                    new RetractShooter()
                });

            case MEDIUM_SHOT:
                return new Sequencer(new SequencedItem[] {
                    new ExtendTwoPistons(),
                    new RetractShooter()
                });

            case STRONG_SHOT:
                return new Sequencer(new SequencedItem[] {
                    new ExtendThreePistons(),
                    new RetractShooter()
                });

            default: return new Sequencer(); // return an empty sequence
        }
    }
}
