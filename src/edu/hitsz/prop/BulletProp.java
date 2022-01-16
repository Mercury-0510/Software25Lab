package edu.hitsz.prop;

import edu.hitsz.aircraft.AbstractAircraft;

public class BulletProp extends BaseProp {

    public BulletProp(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
    }

    @Override
    public void active(AbstractAircraft aircraft) {
        System.out.println("BulletSupply active!");
    }
}
