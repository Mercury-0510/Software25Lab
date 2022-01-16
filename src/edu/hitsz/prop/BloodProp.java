package edu.hitsz.prop;

import edu.hitsz.aircraft.AbstractAircraft;

public class BloodProp extends BaseProp {

    public BloodProp(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
    }

    @Override
    public void active(AbstractAircraft aircraft) {
        aircraft.decreaseHp(-power);
        System.out.println("BloodSupply active!");
    }

}
