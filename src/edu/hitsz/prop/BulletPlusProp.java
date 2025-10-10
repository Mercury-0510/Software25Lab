package edu.hitsz.prop;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.strategy.CircularShootStrategy;
import edu.hitsz.strategy.ShootStrategy;

public class BulletPlusProp extends BaseProp {

    public BulletPlusProp(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
    }

    @Override
    public void active(AbstractAircraft aircraft) {
        ShootStrategy newshootStrategy = new CircularShootStrategy(true);
        if(aircraft.setShootStrategy(newshootStrategy)){
            System.out.println("BulletPlusSupply active!");
        } else {
            System.out.println("BulletPlusSupply advance!");
        }
    }
}