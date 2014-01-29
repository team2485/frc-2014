package team2485.util;

 import edu.wpi.first.wpilibj.SpeedController;
/**
 * TwoSpeedControllers Class
 * Current iteration: uses one PWM channel, cable is split to two speed controllers
 * @author Anoushka Bose
 */
public class TwoSpeedControllers implements SpeedController {
    
    private SpeedController s1;
    
    /**
     * Custom constructor
     * @param s1 {@code SpeedController} 
     */
    public TwoSpeedControllers(SpeedController s1) {
        this.s1 = s1;
    }

    /**
     * {@inheritDoc}  
     */
    public double get() {
        return (s1.get());
    }
    
    /**
     * {@inheritDoc}
     */
    public void set(double d, byte b) {
        this.set(d);
    }

    /**
     * {@inheritDoc}  
     */
    public void set(double d) {
        s1.set(d);
    }

    /**
     * {@inheritDoc}  
     */    
    public void disable() {
        s1.disable();
    }
    
    /**
     * {@inheritDoc}  
     */
    public void pidWrite(double d) {
        s1.pidWrite(d);
    }
    
}

