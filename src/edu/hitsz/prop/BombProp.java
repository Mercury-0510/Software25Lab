package edu.hitsz.prop;

import edu.hitsz.aircraft.AbstractAircraft;

public class BombProp extends BaseProp {

    public BombProp(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
    }

    @Override
    public void active(AbstractAircraft aircraft) {
        System.out.println("BombSupply active!");
    }
}
