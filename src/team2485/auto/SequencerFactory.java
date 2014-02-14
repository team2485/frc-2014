package team2485.auto;

import team2485.auto.sequenceditems.*;
import team2485.comp.IntakeArm;

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
            STRONG_SHOT = 2,
            SHORT_PASS  = 3;

    private static final double TARGET_FLIP_PAUSE_TIME = 0.8;
    public static final double RETRACT_EXTEND_TIME = 0.3; // TODO: figure out duration

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
//                    new SequencedPause(TARGET_FLIP_PAUSE_TIME), // wait until the targets have flipped
//                    new WaitForTargets(),
//                    new TurnToTarget(),
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.WEAK_SHOT)),
                    new Drive(30)
                });

            case TWO_BALL:
                return new Sequencer(new SequencedItem[] {
//                    new SequencedPause(TARGET_FLIP_PAUSE_TIME), // wait until the targets have flipped
//                    new WaitForTargets(),
//                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.WEAK_SHOT)),
//                    new MoveArm(IntakeArm.PICKUP),
//                    new Drive(-10),
//                    new SequencedPause(1),
//                    new MoveArm(IntakeArm.IN_CATAPULT),
//                    new StopRollers(),
//                    new SequencedDoubleItem(new Drive(30), new InnerSequencer(SequencerFactory.createShot(SequencerFactory.WEAK_SHOT)))
                    // wait for target
//                    new SequencedDoubleItem(
//                            new InnerSequencer(SequencerFactory.createShot(SequencerFactory.WEAK_SHOT)),
//                            new MoveArm(IntakeArm.PICKUP)),
//                    new Drive(-10),
//                    new SequencedPause(pid.getNumber("P_Encoder_Drive")),
//                    new ExtendShoe(),
//                    new SequencedTripleItem(
//                            new MoveArm(IntakeArm.IN_CATAPULT),
//                            new StopRollers(),
//                            new SequencedDoubleItem(
//                                    new Drive(30),
//                                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.WEAK_SHOT))))
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
                    new ExtendShoe(),
                    new ExtendOnePiston(),
                    new RetractShooter(),
                    new RetractShoe()
                });

            case MEDIUM_SHOT:
                return new Sequencer(new SequencedItem[] {
                    new ExtendShoe(),
                    new ExtendTwoPistons(),
                    new RetractShooter(),
                    new RetractShoe()
                });

            case STRONG_SHOT:
                return new Sequencer(new SequencedItem[] {
                    new ExtendShoe(),
                    new ExtendThreePistons(),
                    new RetractShooter(),
                    new RetractShoe()
                });

            case SHORT_PASS:
                return new Sequencer(new SequencedItem[] {
                    new ExtendBoot(),
                    new RetractBoot()
                });

            default: return new Sequencer(); // return an empty sequence
        }
    }
}
