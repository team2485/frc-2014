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
            NONE                                = -1,
            // just move forward
            FORWARD                              = 0,
            // move forward and back to truss
            FORWARD_TRUSS                        = 1,
            // specifying distance
            FORWARD_CUSTOM                       = 2,
            // if hot, shoot, not - wait to shoot, move forward, stay, start on left
            ONE_BALL_LEFT                        = 3,
            // if hot, shoot, not - wait to shoot, move forward, stay, start on right
            ONE_BALL_RIGHT                       = 4,
            // if hot, shoot, not - wait to shoot, move forward, move to truss, rotate 180, stay, start on left
            ONE_BALL_TRUSS_LEFT                  = 5,
            // if hot, shoot, not - wait to shoot, move forward, move to truss, rotate 180, stay, start on right
            ONE_BALL_TRUSS_RIGHT                 = 6,
            // if hot, shoot, not - wait to shoot, move forward, on one side, angle to the center, move diagonally back
            ONE_BALL_FROM_LEFT_TO_CENTER_TRUSS   = 7,
            // if hot, shoot, not - wait to shoot, but use angled shot left
            ONE_BALL_FROM_RIGHT_TO_CENTER_TRUSS  = 8,
            // same as before, but other side version
            ONE_BALL_ANGLED_SHOT_LEFT            = 9,
            // if hot, shoot, not - wait to shoot, but use angled shot right
            ONE_BALL_ANGLED_SHOT_RIGHT           = 10,
            // specifying angle and distance
            ONE_BALL_CUSTOM_LEFT                 = 11,
            // two balls, move forward
            TWO_BALL_HOT                         = 12,
            // three balls, move forward
            THREE_BALL                           = 13,

            TWO_BALL_NO_HOT                      = 14,
            THREE_BALL_HOT                       = 15,

            // Shot options
            TARGET_SHOT                     = 0,
            TRUSS_SHOT                      = 1,
            BOOT                            = 2,
            FORWARD_PASS                    = 3,
            POWER_HIGH_SHOT                 = 4,
            TARGET_SHOT_WITHOUT_RETRACTION  = 5,
            MIDRANGE_SHOT_THREE_CYLINDER    = 6,
            MIDRANGE_SHOT_TWO_CYLINDER      = 7,
            OVER_TRUSS_CATCH                = 8;

    public static final double TARGET_FLIP_PAUSE_TIME = 0.2;
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
            case NONE:
                return new Sequencer();
            // Starting position from anywhere on the field
            case FORWARD:
                return new Sequencer(new SequencedItem[] {
                    new Drive(45)
                });

            // Anywhere on the field
            case FORWARD_TRUSS:
                return new Sequencer(new SequencedItem[] {
                    new Drive(45),
                    new Drive(-153)
                });

            // Anywhere on the field
            case FORWARD_CUSTOM:
                return new Sequencer(new SequencedItem[] {
                    new Drive(45),
                    // TODO: Get custom value from dashboard
                    new Drive(0)
                });

            // Aligned on left side
            case ONE_BALL_LEFT:
                return new Sequencer(new SequencedItem[] {
                    new SequencedDoubleItem(
                        new SequencedPause(TARGET_FLIP_PAUSE_TIME), // wait until the targets have flipped
                        new MoveArmNoWait(IntakeArm.IN_CATAPULT - 50)
                    ),
                    new SequencedMultipleItem(new SequencedItem[] {
                        new WaitForTarget(),
                        new MoveArm(IntakeArm.IN_CATAPULT)
                    }),
                    new WaitForHot(WaitForHot.IN_FRONT),
                    new DisableArmPID(),
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT)),
                    new Drive(50)
                });


            // Aligned on right side
            case ONE_BALL_RIGHT:
                return new Sequencer(new SequencedItem[] {
                    new SequencedDoubleItem(
                        new SequencedPause(TARGET_FLIP_PAUSE_TIME), // wait until the targets have flipped
                        new MoveArmNoWait(IntakeArm.IN_CATAPULT - 50)
                    ),
                    new SequencedMultipleItem(new SequencedItem[] {
                        new WaitForTarget(),
                        new MoveArm(IntakeArm.IN_CATAPULT)
                    }),
                    new WaitForHot(WaitForHot.IN_FRONT),
                    new DisableArmPID(),
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT)),
                    new Drive(50)
                });

            // Aligned on left side
            case ONE_BALL_TRUSS_LEFT:
                return new Sequencer(new SequencedItem[] {
                    new InnerSequencer(createAuto(ONE_BALL_LEFT)),
                    new Drive(-63)
                });

            // Aligned on right side
            case ONE_BALL_TRUSS_RIGHT:
                return new Sequencer(new SequencedItem[] {
                    new InnerSequencer(createAuto(ONE_BALL_RIGHT)),
                    new Drive(-63)
                });

            // Aligned on left side
            case ONE_BALL_FROM_LEFT_TO_CENTER_TRUSS:
                return new Sequencer(new SequencedItem[] {
                    new InnerSequencer(createAuto(ONE_BALL_LEFT)),
                    new Rotate(-30),
                    new Drive(-37)
                });

            case ONE_BALL_FROM_RIGHT_TO_CENTER_TRUSS:
                return new Sequencer(new SequencedItem[] {
                    new InnerSequencer(createAuto(ONE_BALL_RIGHT)),
                    new Rotate(30),
                    new Drive(-37)
                });

            case ONE_BALL_ANGLED_SHOT_LEFT:
                return new Sequencer(new SequencedItem[] {
                    new SequencedPause(TARGET_FLIP_PAUSE_TIME),
                    new SequencedMultipleItem(new SequencedItem[] {
                       new FullyExtendShoe(),
                       new WaitForTarget(),
                       new Drive(45)}),
                    new WaitForHot(WaitForHot.IN_FRONT),
                    new Rotate(-10),
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT)),
                    new TurnToZero(),
                });

            case ONE_BALL_ANGLED_SHOT_RIGHT:
                return new Sequencer(new SequencedItem[] {
                    new SequencedPause(TARGET_FLIP_PAUSE_TIME),
                    new SequencedMultipleItem(new SequencedItem[] {
                       new FullyExtendShoe(),
                       new WaitForTarget(),
                       new Drive(45)}),
                    new WaitForHot(WaitForHot.IN_FRONT),
                    new Rotate(15),
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT)),
                    new TurnToZero(),
                });

            case ONE_BALL_CUSTOM_LEFT:
                return new Sequencer(new SequencedItem[] {
                    new SequencedPause(TARGET_FLIP_PAUSE_TIME),
                    new WaitForTarget(),
                    new WaitForHot(WaitForHot.IN_FRONT),
                    // TODO: get custom rotate and drive values from dashboard
                    new Rotate(0),
                    new FullyExtendShoe(),
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT)),
                   new Drive(0)
                });

            case TWO_BALL_NO_HOT:
                return new Sequencer(new SequencedItem[] {
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT_WITHOUT_RETRACTION)),
//                    new InnerSequencer(SequencerFactory.createAuto(SequencerFactory.TARGET_SHOT)),
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
//                        new FullyExtendShoe(),
                    }),
                    new DisableArmPID(),
                    new SequencedPause(0.6), // settle time
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT_WITHOUT_RETRACTION)), // second shot
                    new SequencedMultipleItem(new SequencedItem[] {
                        new RetractShooter(),
                        new WaitForBallToLeave(),
                        new FullyRetractShoe(),
                        new Drive(60)
                    }),
                    new DisableEncoderPID()
            });

            // from left
            case TWO_BALL_HOT:
                return new Sequencer(new SequencedItem[] {
                    new SequencedDoubleItem(
                            new SequencedPause(TARGET_FLIP_PAUSE_TIME),
                            new FullyExtendShoe()),
                    new WaitForTarget(),
                    new MoveArmNoWait(IntakeArm.IN_CATAPULT - 100, false),
                    new Drive(65),
                    new DisableEncoderPID(),
                    new TurnToTarget(),
                    new DisableIMUPID(),
                    new SequencedPause(0.1),
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT_WITHOUT_RETRACTION)),
                    new SequencedMultipleItem(new SequencedItem[] {
                        new RetractShooter(),
                        new WaitForBallToLeave(),
                        new FullyRetractShoe(),
                        new MoveArm(IntakeArm.PICKUP, true),
                        new TurnToZero()
                    }),
                    new DisableIMUPID(),
                    new SequencedPause(.15),
                    new Drive(-10),
                    new DisableEncoderPID(),
                    new DetectBallInCatapult(),
                    new SequencedMultipleItem(new SequencedItem[] {
                        new MoveArm(IntakeArm.IN_CATAPULT - 100),
                        new StopRollers(),
                    }),
                    new SequencedDoubleItem(
                        new Drive(65),
                        new FullyExtendShoe()),
                    new DisableEncoderPID(),
                    new TurnToOtherTarget(),
                    new DisableIMUPID(),
                    new SequencedPause(0.1),
                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT_WITHOUT_RETRACTION)),
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
//                    new SequencedPause(0.1),
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

                // <editor-fold defaultstate="collapsed" desc="Previous three ball">
//                return new Sequencer(new SequencedItem[] {
//                    new SequencedMultipleItem(new SequencedItem[] {
//                        new MoveArmNoWait(IntakeArm.PICKUP - 100, false),
//                        new SequencedPause(1.5)
//                    }),
//                    new DisableArmPID(),
//                    new SequencedPause(0.8),
//
//
//                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT_WITHOUT_RETRACTION)),
//                    new SequencedMultipleItem(new SequencedItem[] {
//                        new MoveArmNoWait(IntakeArm.PICKUP, false),
//                        new RetractShooter(),
//                        new WaitForBallToLeave()
//                    }),
//                    new SequencedPause(0.8),
//                    new MoveArm(IntakeArm.PICKUP, true),
//                    new DetectBallInCatapult(),
//                    new SequencedMultipleItem(new SequencedItem[] {
//                        new MoveArm(IntakeArm.IN_CATAPULT),
//                        new StopRollers(),
//                        new FullyExtendShoe(),
//                    }),
//                    new SequencedPause(1.0), // settle time
//                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT_WITHOUT_RETRACTION)),
//                    new SequencedMultipleItem(new SequencedItem[] {
//                        new RetractShooter(),
//                        new WaitForBallToLeave(),
//                        new FullyRetractShoe(),
//                    }),
//                    new SequencedPause(0.5),
//                    new SequencedDoubleItem(
//                        new MoveArm(IntakeArm.PICKUP, true),
//                        new Drive(-24)),
//                    new DisableEncoderPID(),
//                    new DetectBallInCatapult(),
//                    new SequencedMultipleItem(new SequencedItem[] {
//                        new MoveArm(IntakeArm.IN_CATAPULT),
//                        new StopRollers(),
//                        new Drive(50),
//                        new FullyExtendShoe()
//                    }),
//                    new DisableEncoderPID(),
//                    new SequencedPause(0.8), // settle time
//                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT_WITHOUT_RETRACTION)),
//                    new RetractShooter()
//                 });
                // </editor-fold>

                // <editor-fold defaultstate="collapsed" desc="Auto from SD (pre-catcher)">
//                return new Sequencer(new SequencedItem[] {
//                    new FullyExtendShoe(),
//                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT_WITHOUT_RETRACTION)),
//                    new SequencedMultipleItem(new SequencedItem[] {
//                        new MoveArmNoWait(IntakeArm.PICKUP, false),
//                        new RetractShooter(),
//                        new WaitForBallToLeave(),
//                        new FullyRetractShoe(),
//                    }),
//                    new SequencedPause(0.8),
//                    new MoveArm(IntakeArm.PICKUP, true),
//                    new DetectBallInCatapult(),
//                    new SequencedMultipleItem(new SequencedItem[] {
//                        new MoveArm(IntakeArm.IN_CATAPULT),
//                        new StopRollers(),
//                        new FullyExtendShoe(),
//                    }),
//                    new SequencedPause(1.0), // settle time
//                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT_WITHOUT_RETRACTION)),
//                    new SequencedMultipleItem(new SequencedItem[] {
//                        new RetractShooter(),
//                        new WaitForBallToLeave(),
//                        new FullyRetractShoe(),
//                    }),
//                    new SequencedPause(0.5),
//                    new SequencedDoubleItem(
//                        new MoveArm(IntakeArm.PICKUP, true),
//                        new Drive(-24)),
//                    new DisableEncoderPID(),
//                    new DetectBallInCatapult(),
//                    new SequencedMultipleItem(new SequencedItem[] {
//                        new MoveArm(IntakeArm.IN_CATAPULT),
//                        new StopRollers(),
//                        new Drive(30),
//                        new FullyExtendShoe()
//                    }),
//                    new DisableEncoderPID(),
//                    new SequencedPause(0.8), // settle time
//                    new InnerSequencer(SequencerFactory.createShot(SequencerFactory.TARGET_SHOT_WITHOUT_RETRACTION)),
//                    new RetractShooter()
//                 });
            // </editor-fold>

            // AKA MARTY'S BABY
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
                    new SequencedDoubleItem(
                        new SequencedPause(0.8),
                        new DisableArmPID()),
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

            default: return new Sequencer(); // return an empty sequence
        }
    }
}
