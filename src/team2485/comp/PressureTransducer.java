package team2485.comp;

import edu.wpi.first.wpilibj.AnalogChannel;

/**
 * Represents a pressure transducer.
 * @author Marty Kausas
 */
public class PressureTransducer extends AnalogChannel {

    /**
     * Creates a new pressure transducer, with the specified analog channel.
     * @param channel the analog channel
     */
    public PressureTransducer(int channel) {
        super(channel);
    }

    /**
     * Returns the PSI reading, converted from the analog value.
     * @return PSI
     */
    public double getPressure() {
        return (super.getValue() * -2.6243) + 1102.6;
    }
}
