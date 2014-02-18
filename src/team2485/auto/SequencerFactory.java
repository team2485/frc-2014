package team2485.auto;

import team2485.Robot;
import team2485.auto.sequenceditems.*;
import team2485.comp.IntakeArm;

/**
 * The sequencer factory instantiates all robot sequences
 *
 * @author Marty Kausas
 * @author Bryce Matsumori
 * @author Anoushka Bose
 * @author Camille Considine
 */
public class SequencerFactory {

    public static final int
            // Autonomous options
            // just move forward
            NONE                                 = 0,
            // move forward and back to truss
            NONE_TRUSS                           = 1,
            // specifying distance
            NONE_CUSTOM                          = 2,
            // if hot, shoot, not - wait to shoot, move forward, stay, start on left
            ONE_BALL_LEFT                        = 3,
            // if hot, shoot, not - wait to shoot, move forward, stay, start on right
            ONE_BALL_RIGHT                       = 4,
            // if hot, shoot, not - wait to shoot, move forward, move to truss, rotate 180, stay, start on left
            ONE_BALL_TRUSS_LEFT                  = 5,
            // if hot, shoot, not - wait to shoot, move forward, move to truss, rotate 180, stay, start on right
            ONE_BALL_TRUSS_RIGHT                 = 6,
            // if hot, shoot, not - wait to shoot, move forward, on one side, angle to the center, move diagonally back
            ONE_BALL_FROM_LEFT_TO_CENTER_TRUSS          = 7,
            // same as before, but other side version
            ONE_BALL_FROM_RIGHT_TO_CENTER_TRUSS         = 8,
            // if hot, shoot, not - wait to shoot, but use angled shot left
            ONE_BALL_ANGLED_SHOT_LEFT            = 9,
            // if hot, shoot, not - wait to shoot, but use angled shot right
            ONE_BALL_ANGLED_SHOT_RIGHT           = 10,
            // specifying angle and distance
            ONE_BALL_CUSTOM_LEFT                 = 11,
            // two balls, move forward
            TWO_BALL                             = 12,
            // three balls, move forward
            THREE_BALL                           = 13,

            // Shot options
            TARGET_SHOT                     = 0,
            TRUSS_SHOT                      = 1,
            BOOT                            = 2,
            FORWARD_PASS                    = 3,
            POWER_HIGH_SHOT                 = 4;

    public static final double TARGET_FLIP_PAUSE_TIME = 0.8;
    public static final double RETRACT_EXTEND_TIME = 0.7; // TODO: figure out duration

    /**
     * Creates the requested autonomous sequence.
     *
     * @param type The autonomous type.
     * @return The created {@code Sequencer}.
     */
    public static Sequencer createAuto(int type) {
        // TODO: Create all autonomous sequences
        // TODO: Change all forward pass to target shots
        // TODO: Check -153 as new Drive() parameter
        // TODO: Find rotateTo  values
        // TODO: Add vision targeting/hot goal detection: must return to 0

        switch (type) {
            // Starting position from anywhere on the field
            case NONE:
                return new Sequencer(new SequencedItem[] {
                    new Drive(45)
                });

            // Anywhere on the field
            case NONE_TRUSS:
                return new Sequencer(new SequencedItem[] {
                    new Drive(45),
                    new Drive(-153)
                });

            // Anywhere on the field
            case NONE_CUSTOM:
                return new Sequencer(new SequencedItem[] {
                    new Drive(45),
                    // TODO: Get custom value from dashboard
                    new Drive(0)
                });

            // Aligned on left side
            case ONE_BALL_LEFT:
                return new Sequencer(new SequencedItem[] {
                   new SequencedPause(TARGET_FLIP_PAUSE_TIME), // wait until the targets have flipped
                   new SequencedItemArray(new SequencedItem[] {
                       new ExtendShoe(),
                       new WaitForTargetDetection()}),
                   new WaitForHot(WaitForHot.LEFT),
                   new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT)),
                   new Drive(45)
                });

            // Aligned on right side
            case ONE_BALL_RIGHT:
                return new Sequencer(new SequencedItem[] {
                   new SequencedPause(TARGET_FLIP_PAUSE_TIME), // wait until the targets have flipped
                   new SequencedItemArray(new SequencedItem[] {
                       new ExtendShoe(),
                       new WaitForTargetDetection()}),
                   new WaitForHot(WaitForHot.RIGHT),
                   new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT)),
                   new Drive(45)
                });

            // Aligned on left side
            case ONE_BALL_TRUSS_LEFT:
                return new Sequencer(new SequencedItem[] {
                    new SequencedPause(TARGET_FLIP_PAUSE_TIME),
                    new SequencedItemArray(new SequencedItem[] {
                       new ExtendShoe(),
                       new WaitForTargetDetection()}),
                    new WaitForHot(WaitForHot.LEFT),
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT)),
                    new Drive(45),
                    new Drive(-63)
                });

            // Aligned on right side
            case ONE_BALL_TRUSS_RIGHT:
                return new Sequencer(new SequencedItem[] {
                    new SequencedPause(TARGET_FLIP_PAUSE_TIME),
                    new SequencedItemArray(new SequencedItem[] {
                       new ExtendShoe(),
                       new WaitForTargetDetection()}),
                    new WaitForHot(WaitForHot.RIGHT),
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT)),
                    new Drive(45),
                    new Drive(-63)
                });

            // Aligned on left side
            case ONE_BALL_FROM_LEFT_TO_CENTER_TRUSS:
                return new Sequencer(new SequencedItem[] {
                    new SequencedPause(TARGET_FLIP_PAUSE_TIME),
                    new SequencedItemArray(new SequencedItem[] {
                       new ExtendShoe(),
                       new WaitForTargetDetection()}),
                    new WaitForHot(WaitForHot.LEFT),
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT)),
                    new Drive(45),
                    new Rotate(-30),
                    new Drive(-108)
                });

            case ONE_BALL_FROM_RIGHT_TO_CENTER_TRUSS:
                return new Sequencer(new SequencedItem[] {
                    new SequencedPause(TARGET_FLIP_PAUSE_TIME),
                    new SequencedItemArray(new SequencedItem[] {
                       new ExtendShoe(),
                       new WaitForTargetDetection()}),
                    new WaitForHot(WaitForHot.RIGHT),
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT)),
                    new Drive(45),
                    new Rotate(30),
                    new Drive(-108)
                });

            case ONE_BALL_ANGLED_SHOT_LEFT:
                return new Sequencer(new SequencedItem[] {
                    new SequencedPause(TARGET_FLIP_PAUSE_TIME),
                    new SequencedItemArray(new SequencedItem[] {
                       new ExtendShoe(),
                       new WaitForTargetDetection()}),
                    new WaitForHot(WaitForHot.LEFT),
                    new Rotate(-10),
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT)),
                    new TurnToZero(),
                    new Drive(45)
                });

            case ONE_BALL_ANGLED_SHOT_RIGHT:
                return new Sequencer(new SequencedItem[] {
                    new SequencedPause(TARGET_FLIP_PAUSE_TIME),
                    new SequencedItemArray(new SequencedItem[] {
                       new ExtendShoe(),
                       new WaitForTargetDetection()}),
                    new WaitForHot(WaitForHot.RIGHT),
                    new Rotate(15),
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT)),
                    new TurnToZero(),
                    new Drive(45)
                });

            case ONE_BALL_CUSTOM_LEFT:
                return new Sequencer(new SequencedItem[] {
                    new SequencedPause(TARGET_FLIP_PAUSE_TIME),
                    new WaitForTargetDetection(),
                    new WaitForHot(WaitForHot.LEFT),
                    // TODO: get custom rotate and drive values from dashboard
                    new Rotate(0),
                    new ExtendShoe(),
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT)),
                   new Drive(0)
                });

            // Assuming we are centered on the field
            case TWO_BALL:
                return new Sequencer(new SequencedItem[] {
                    new SequencedPause(TARGET_FLIP_PAUSE_TIME),
                    new SequencedItemArray(new SequencedItem[] {
                        new WaitForTargetDetection(),
                        new MoveArm(IntakeArm.PICKUP, false) }),
                    new SequencedItemArray(new SequencedItem[] {
                        new TurnToTarget(),
                        new ExtendShoe()}),
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT)),
                    new BallHasLeft(),
                    new SequencedItemArray(new SequencedItem[] {
                        new TurnToZero(),
                        new RetractShoe()}),
                    new SequencedItemArray(new SequencedItem[] {
                        new Drive(-20),
                        new MoveArm(IntakeArm.PICKUP)}),
                    new BallIsSettled(),
                    new SequencedItemArray(new SequencedItem[] {
                        new MoveArm(IntakeArm.IN_CATAPULT),
                        new StopRollers(),
                    }),
                    new TurnToOtherTarget(),
                    new ExtendShoe(),
                    new SequencedItemArray(new SequencedItem[] {
                            new Drive(45),
                            new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT))})
                 });

            case THREE_BALL:
                return new Sequencer(new SequencedItem[] {
                    new ExtendShoe(),
                    new SequencedItemArray(new SequencedItem[] {
                        new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT)),
                        new MoveArm(IntakeArm.PICKUP, false)}),
                    new BallHasLeft(),
                    new MoveArm(IntakeArm.PICKUP),
                    new Drive(-10),
                    new BallIsSettled(),
                    new ExtendShoe(),
                    new SequencedItemArray(new SequencedItem[] {
                        new MoveArm(IntakeArm.IN_CATAPULT),
                        new StopRollers(),
                        new SequencedItemArray(new SequencedItem[] {
                            new Drive(0),
                            new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT))})}),
                    new BallHasLeft(),
                    new MoveArm(IntakeArm.PICKUP),
                    new Drive(-30),
                    new BallIsSettled(),
                    new ExtendShoe(),
                    new SequencedItemArray(new SequencedItem[] {
                            new MoveArm(IntakeArm.IN_CATAPULT),
                            new StopRollers(),
                            new SequencedItemArray(new SequencedItem[] {
                                    new Drive(45),
                                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT))})})
                });

            default: return new Sequencer(); // return an empty sequence
        }
    }

    private static final double AUTO_TRUSS_DRIVE_DIST = 96.0;
    public static Sequencer createAutoTrussShot() {
        return new Sequencer(new SequencedItem[] {
            new InnerSequencer(createAuto(TRUSS_SHOT)),
            new Rotate(180), // turn to catch?
            new Drive(AUTO_TRUSS_DRIVE_DIST)
        });
    }

    /**
     * Creates the requested shot sequence.
     *
     * @param type The shot type.
     * @return The created {@code Sequencer}.
     */
    public static Sequencer createShot(int type) {

        switch (type) {
            case POWER_HIGH_SHOT:
                return new Sequencer(new SequencedItem[] {
                    new RetractShoe(),
                    new ExtendThreePistons(),
                    new RetractShooter(),
                    new RetractShoe()
                });

            case TRUSS_SHOT:
                return new Sequencer(new SequencedItem[] {
                    new RetractShoe(),
                    new ExtendTwoPistons(),
                    new RetractShooter(),
                    new RetractShoe()
                });

            case TARGET_SHOT:
                return new Sequencer(new SequencedItem[] {
                    new ExtendShoe(),
                    new ExtendThreePistons(),
                    new RetractShooter(),
                    new RetractShoe()
                });

            case BOOT:
                return new Sequencer(new SequencedItem[] {
                    new ExtendBoot(),
                    new RetractBoot()
                });

            case FORWARD_PASS:
                return new Sequencer(new SequencedItem[] {
                    new ExtendShoe(),
                    new ExtendRightSidePiston(),
                    new RetractShooter(),
                    new RetractShoe()
                });

            default: return new Sequencer(); // return an empty sequence
        }
    }
}
