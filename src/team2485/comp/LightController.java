package team2485.comp;

import edu.wpi.first.wpilibj.Relay;

public class LightController {
    private final Relay r1, r2, r3, r4, r5;

    public static final int
            BLACK          = 0,
            GREEN          = 1,
            GOLD           = 2,
            RED            = 3,
            BLUE           = 4,
            GOLD_ALT       = 5,
            WHITE_ALT      = 6,
            HAPPY_RAINBOW  = 7,
            GOLD_CHASE     = 8,
            GOLD_BLINK     = 9,
            RAINBOW_CHASE  = 10,
            GOLD_PING_PONG = 11,
            RAINBOW_CYCLE  = 12;

    public LightController(Relay r1, Relay r2, Relay r3, Relay r4, Relay r5) {
        this.r1 = r1;
        this.r2 = r2;
        this.r3 = r3;
        this.r4 = r4;
        this.r5 = r5;
    }

    public void send(int sequence) {
        r1.set((sequence & 1)  > 0 ? Relay.Value.kForward : Relay.Value.kOff);
        r2.set((sequence & 2)  > 0 ? Relay.Value.kForward : Relay.Value.kOff);
        r3.set((sequence & 4)  > 0 ? Relay.Value.kForward : Relay.Value.kOff);
        r4.set((sequence & 8)  > 0 ? Relay.Value.kForward : Relay.Value.kOff);
        r5.set((sequence & 16) > 0 ? Relay.Value.kForward : Relay.Value.kOff);
    }
}
