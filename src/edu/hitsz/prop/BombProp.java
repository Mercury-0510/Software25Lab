package edu.hitsz.prop;

import edu.hitsz.aircraft.AbstractAircraft;

public class BombProp extends BaseProp {

    public BombProp(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
    }

    @Override
    public void active(AbstractAircraft aircraft) {
        // Bomb effect is handled in the Game class through observer pattern
        // When the bomb prop is collected, the Game class will handle the effect
        // by notifying observers and clearing the appropriate objects
    }
}
