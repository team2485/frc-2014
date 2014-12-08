package team2485.auto;

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
            NONE                            = -1,
            // just move forward
            FORWARD                         = 0,
            ONE_BALL_LEFT                   = 3,
            // if hot, shoot, not - wait to shoot, move forward, stay, start on right
            ONE_BALL_RIGHT                  = 4,
            TWO_BALL_NO_HOT                 = 14,
            // two balls, move forward
            TWO_BALL_HOT                    = 12,
            // three balls, move forward
            THREE_BALL                      = 13,
            THREE_BALL_HOT                  = 15,

            // Shot options
            TARGET_SHOT                     = 0,
            TRUSS_SHOT                      = 1,
            BOOT                            = 2,
            FORWARD_PASS                    = 3,
            POWER_HIGH_SHOT                 = 4,
            TARGET_SHOT_WITHOUT_RETRACTION  = 5,
            MIDRANGE_SHOT_THREE_CYLINDER    = 6,
            MIDRANGE_SHOT_TWO_CYLINDER      = 7,
            OVER_TRUSS_CATCH                = 8,
            BLOOP_SHOT                      = 9;

    public static final double TARGET_FLIP_PAUSE_TIME = 0.2, OPERATOR_WAIT_TIME = 1.8;
    public static final double RETRACT_EXTEND_TIME = 0.7;

    /**
     * Creates the requested autonomous sequence.
     *
     * @param type The autonomous type.
     * @return The created {@code Sequencer}.
     */
    public static Sequencer createAuto(int type) {
        switch (type) {
            case NONE:
                return new Sequencer();

            // Starting position from anywhere on the field
            case FORWARD:
                return new Sequencer(new SequencedItem[] {
                    new Drive(45)
                });

            // Aligned on left or right side
            case ONE_BALL_LEFT:
            case ONE_BALL_RIGHT:
                return new Sequencer(new SequencedItem[] {
                    new SequencedMultipleItem(new SequencedItem[] {
                        new SequencedPause(OPERATOR_WAIT_TIME), // wait until the Operator has shown card
                        new MoveArmNoWait(IntakeArm.IN_CATAPULT - 150),
                        new FullyExtendShoe(),
                        new Drive(45),
                    }),
                    new WaitForTarget(),
                    new SequencedPause(0.7),
                    new WaitForHot(type == ONE_BALL_LEFT ? WaitForHot.LEFT : WaitForHot.RIGHT),
                    new DisableArmPID(),
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT)),
                    new SequencedPause(1),
                    new Drive(60)
                });

            case TWO_BALL_NO_HOT:
                return new Sequencer(new SequencedItem[] {
                    new MoveArm(IntakeArm.PICKUP - 150),
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT_WITHOUT_RETRACTION)),
                    new SequencedMultipleItem(new SequencedItem[] {
                        new MoveArm(IntakeArm.PICKUP, false),
                        new RetractShooter(),
                        new WaitForBallToLeave(),
                    }),
                    new SequencedPause(0.2),
                    new SequencedMultipleItem(new SequencedItem[] {
                        new FullyRetractShoe(),
                        new MoveArm(IntakeArm.PICKUP, true)
                    }),
                    new DetectBallInCatapult(),
                    new SequencedPause(0.1),
                    new SequencedMultipleItem(new SequencedItem[] {
                        new MoveArm(IntakeArm.IN_CATAPULT - 25),
                        new StopRollers()
                    }),
                    new DisableArmPID(),
                    new SequencedPause(0.6), // settle time
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT_WITHOUT_RETRACTION)), // second shot
                    new SequencedMultipleItem(new SequencedItem[] {
                        new RetractShooter(),
                        new WaitForBallToLeave(),
                        new FullyRetractShoe(),
                        new Drive(65)
                    }),
                    new DisableEncoderPID()
            });

            // from left
            case TWO_BALL_HOT:
                return new Sequencer(new SequencedItem[] {
                    new FullyExtendShoe(),
                    new MoveArmNoWait(IntakeArm.IN_CATAPULT - 100, false),
                    new Drive(45),
                    new DisableEncoderPID(),
                    new WaitForTarget(),
                    new TurnToTarget(),
                    new DisableIMUPID(),
                    new SequencedPause(0.6),
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT_WITHOUT_RETRACTION)),
                    new SequencedMultipleItem(new SequencedItem[] {
                        new RetractShooter(),
                        new WaitForBallToLeave(),
                        new FullyRetractShoe(),
                        new MoveArm(IntakeArm.PICKUP, true),
                        new TurnToZero()
                    }),
                    new DisableIMUPID(),
                    new SequencedPause(0.15),
                    new Drive(-5),
                    new DisableEncoderPID(),
                    new DetectBallInCatapult(),
                    new SequencedMultipleItem(new SequencedItem[] {
                        new MoveArm(IntakeArm.IN_CATAPULT - 100),
                        new StopRollers(),
                    }),
                    new SequencedMultipleItem(new SequencedItem[] {
                        new Drive(50),
                        new FullyExtendShoe(),
                    }),
                    new DisableEncoderPID(),
                    new TurnToOtherTarget(),
                    new DisableIMUPID(),
                    new SequencedPause(0.6),
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT)),
                    new RetractShooter()
            });

            case THREE_BALL:
                return new Sequencer(new SequencedItem[] {
                    new SequencedMultipleItem(new SequencedItem[] {
                        new MoveArmNoWait(IntakeArm.PICKUP - 100, false),
                        new SequencedPause(0.4)
                    }),
                    new DisableArmPID(),
                    new SequencedPause(0.8),
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT_WITHOUT_RETRACTION)),
                    new SequencedMultipleItem(new SequencedItem[] {
                        new MoveArm(IntakeArm.PICKUP, false),
                        new RetractShooter(),
                        new WaitForBallToLeave(),
                    }),
                    new SequencedMultipleItem(new SequencedItem[] {
                        new FullyRetractShoe(),
                        new MoveArm(IntakeArm.PICKUP, true)
                    }),
                    new DetectBallInCatapult(),
                    new SequencedPause(0.1),
                    new SequencedMultipleItem(new SequencedItem[] {
                        new MoveArm(IntakeArm.IN_CATAPULT - 25),
                        new StopRollers(),
                    }),
                    new DisableArmPID(),
                    new SequencedPause(0.5), // settle time
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT_WITHOUT_RETRACTION)), // second shot

                    new SequencedMultipleItem(new SequencedItem[] {
                        new RetractShooter(),
                        new MoveArm(IntakeArm.PICKUP)
                    }),

                    new SequencedMultipleItem(new SequencedItem[] {
                        new FullyRetractShoe(),
                        new Drive(-24),
                    }),

                    new DetectBallInCatapult(),
                    new SequencedMultipleItem(new SequencedItem[] {
                        new MoveArm(IntakeArm.IN_CATAPULT - 25),
                        new StopRollers(),
                        new Drive(50)
                    }),
                    new DisableArmPID(),
                    new SequencedPause(0.6), // settle time
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT_WITHOUT_RETRACTION)), // third shot

                    new SequencedMultipleItem(new SequencedItem[] {
                        new RetractShooter(),
                        new WaitForBallToLeave()
                    }),
                    new FullyRetractShoe(),
                    new DisableEncoderPID()
                });

            case THREE_BALL_HOT:
                return new Sequencer(new SequencedItem[] {
                    new SequencedMultipleItem(new SequencedItem[] {
                        new SequencedPause(TARGET_FLIP_PAUSE_TIME), // wait until the targets have flipped
                        new FullyExtendShoe(),
                        new SetLowGear(),
                        new ExtendCatcher(),
                        new MoveArmNoWait(IntakeArm.PICKUP - 100)
                    }),
                    new TurnToTarget(),
                    new DisableIMUPID(),
                    new SequencedMultipleItem(new SequencedItem[] {
                        new SequencedPause(0.8),
                        new DisableArmPID(),
                    }),
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT_WITHOUT_RETRACTION)),
                    new SequencedMultipleItem(new SequencedItem[] {
                        new MoveArmNoWait(IntakeArm.PICKUP, false),
                        new RetractShooter(),
                        new WaitForBallToLeave(),
                        new FullyRetractShoe(),
                    }),
                    new SequencedPause(1),
                    new MoveArm(IntakeArm.PICKUP, true),
                    new DetectBallInCatapult(),
                    new SequencedMultipleItem(new SequencedItem[] {
                        new MoveArmNoWait(IntakeArm.PICKUP - 100, false),
                        new StopRollers(),
                        new FullyExtendShoe(),
                    }),
                    new TurnToOtherTarget(),
                    new DisableIMUPID(),
                    new SequencedPause(0.8), // settle time
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT_WITHOUT_RETRACTION)),
                    new SequencedMultipleItem(new SequencedItem[] {
                        new RetractShooter(),
                        new WaitForBallToLeave(),
                        new FullyRetractShoe(),
                    }),
                    new SequencedPause(0.8),
                    new MoveArm(IntakeArm.PICKUP, true),
                    new DetectBallInCatapult(),
                    new SequencedMultipleItem(new SequencedItem[] {
                        new StopRollers(),
                        new FullyExtendShoe()
                    }),
                    new SequencedPause(0.8), // settle time
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT_WITHOUT_RETRACTION)),
                    new SequencedMultipleItem(new SequencedItem[] {
                        new SetHighGear(),
                        new Drive(80),
                        new RetractShooter(),
                        new WaitForBallToLeave(),
                        new FullyRetractShoe()
                    }),
                    new DisableEncoderPID()
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
                    new FullyRetractShoe(),
                    new ExtendThreePistons(),
                    new RetractShooter(),
                    new FullyRetractShoe()
                });

            case TRUSS_SHOT:
                return new Sequencer(new SequencedItem[] {
                    new ExtendShoeShortPiston(),
                    new ExtendTwoPistons(),
                    new RetractShooter(),
                    new FullyRetractShoe()
                });

            case TARGET_SHOT:
                return new Sequencer(new SequencedItem[] {
                    new FullyExtendShoe(),
                    new ExtendThreePistons(),
                    new RetractShooter(),
                    new FullyRetractShoe()
                });

            case BOOT:
                return new Sequencer(new SequencedItem[] {
                    new ExtendBoot(),
                    new RetractBoot()
                });

            case FORWARD_PASS:
                return new Sequencer(new SequencedItem[] {
                    new FullyExtendShoe(),
                    new ExtendRightSidePiston(),
                    new RetractShooter(),
                    new FullyRetractShoe()
                });

            case TARGET_SHOT_WITHOUT_RETRACTION:
                return new Sequencer(new SequencedItem[] {
                    new FullyExtendShoe(),
                    new ExtendThreePistons(),
                    new Print("time during shot = ")
                });

            case MIDRANGE_SHOT_THREE_CYLINDER:
                return new Sequencer(new SequencedItem[] {
                    new ExtendShoeLongPiston(),
                    new ExtendThreePistons(),
                    new RetractShooter(),
                    new FullyRetractShoe()
                });

            case MIDRANGE_SHOT_TWO_CYLINDER:
                return new Sequencer(new SequencedItem[] {
                    new ExtendShoeLongPiston(),
                    new ExtendTwoPistons(),
                    new RetractShooter(),
                    new FullyRetractShoe()
                });

            case OVER_TRUSS_CATCH:
                return new Sequencer(new SequencedItem[] {
                    new FullyRetractShoe(),
                    new ExtendTwoPistons(),
                    new RetractShooter(),
                    new FullyRetractShoe()
                });

            case BLOOP_SHOT:
                return new Sequencer(new SequencedItem[] {
                    new FullyRetractShoe(),
                    new ExtendOnePiston(),
                    new RetractShooter(),
                });

            default: return new Sequencer(); // return an empty sequence
        }
    }
}
