package team2485.comp;

import edu.wpi.first.wpilibj.Solenoid;

/**
 * Pneumatic catching mechanism
 * @author W.A.R.Lords
 */
public class Catcher {

    private Solenoid frontExtension;

    public Catcher(Solenoid frontExtension) {
        this.frontExtension = frontExtension;
    }

    public Catcher(int frontExtensionPort) {
        this(new Solenoid(frontExtensionPort));
    }

    public void extend() {
        frontExtension.set(true);
    }

    public void retract() {
        frontExtension.set(false);
    }
}
