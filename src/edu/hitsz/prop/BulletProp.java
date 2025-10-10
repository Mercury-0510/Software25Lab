package edu.hitsz.prop;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.strategy.ScatterShootStrategy;
import edu.hitsz.strategy.ShootStrategy;

public class BulletProp extends BaseProp {

    public BulletProp(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
    }

    @Override
    public void active(AbstractAircraft aircraft) {
        ShootStrategy newshootStrategy = new ScatterShootStrategy(true);
        if(aircraft.setShootStrategy(newshootStrategy)){
            System.out.println("BulletSupply active!");
        } else {
            System.out.println("BulletSupply advance!");
        }
    }
}
