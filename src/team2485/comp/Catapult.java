package team2485.comp;

import edu.wpi.first.wpilibj.*;
import team2485.auto.*;
import team2485.util.*;

/**
 * Class representing the catapult.
 *
 * @author Camille Considine
 */
public class Catapult {
    
    private Solenoid 
            catapultPiston1,
            catapultPiston2, 
            catapultPiston3;
    
    public Catapult(Solenoid catapultPiston1, Solenoid catapultPiston2, Solenoid catapultPiston3) {
        this.catapultPiston1 = catapultPiston1;
        this.catapultPiston2 = catapultPiston2;
        this.catapultPiston3 = catapultPiston3;
    }
        
    public void longShot() {        
        catapultPiston1.set(true);
        catapultPiston2.set(true);  
        catapultPiston3.set(true);
    }
    
    public void shortShot() {        
        catapultPiston1.set(true);
        catapultPiston2.set(true);  
        catapultPiston3.set(false);
    }
    
    public void trussShot() {        
        catapultPiston1.set(true);
        catapultPiston2.set(false);  
        catapultPiston3.set(false);
    }
    
    public void retract() {        
        catapultPiston1.set(false);
        catapultPiston2.set(false);  
        catapultPiston3.set(false);
    }
    
    public void shoot() {
    
    }
    
//    public int getState() {
//    
//    }
    
    
}


