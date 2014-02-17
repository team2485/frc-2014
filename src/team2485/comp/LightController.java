package team2485.comp;

import edu.wpi.first.wpilibj.Relay;

public class LightController {
    private final Relay r1;
    private final Relay r2;
    private final Relay r3;

    public LightController(Relay r1, Relay r2, Relay r3) {
        this.r1 = r1;
        this.r2 = r2;
        this.r3 = r3;
    }

    public void send(int sequence) {
        r1.set((sequence & 1) > 0 ? Relay.Value.kForward : Relay.Value.kOff);
        r2.set((sequence & 2) > 0 ? Relay.Value.kForward : Relay.Value.kOff);
        r3.set((sequence & 4) > 0 ? Relay.Value.kForward : Relay.Value.kOff);
    }
}
