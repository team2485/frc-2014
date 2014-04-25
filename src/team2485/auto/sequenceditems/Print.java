package team2485.auto.sequenceditems;

import edu.wpi.first.wpilibj.DriverStation;
import team2485.auto.SequencedItem;

/**
 *
 * @author W.A.R.Lords
 */
public class Print implements SequencedItem {
    private final String output;

    public Print(String output) {
        this.output = output;
    }

    public void run() { }

    public double duration() {
        System.out.println(output + DriverStation.getInstance().getMatchTime());
        return 0.01;
    }
}
