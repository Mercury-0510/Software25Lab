package edu.hitsz.prop;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.strategy.CircularShootStrategy;
import edu.hitsz.strategy.LinearShootStrategy;
import edu.hitsz.strategy.ShootStrategy;

public class BulletPlusProp extends BaseProp {

    private static final int DURATION = 5000; // 持续时间5秒

    public BulletPlusProp(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
    }

    @Override
    public void active(AbstractAircraft aircraft) {
        ShootStrategy newshootStrategy = new CircularShootStrategy(true);
        if(aircraft.setShootStrategy(newshootStrategy)){
            System.out.println("BulletPlusSupply active!");
        } else {
            System.out.println("BulletPlusSupply advance! Timer reset!");
        }
        
        // 创建新的定时器线程
        Thread timer = new Thread(() -> {
            try {
                Thread.sleep(DURATION);
                aircraft.setShootStrategy(new LinearShootStrategy(true));
                System.out.println("BulletPlusSupply effect ended!");
            } catch (InterruptedException e) {
                System.out.println("BulletPlusSupply timer interrupted (reset by new power-up)");
            }
        });
        
        // 设置定时器(会自动中断旧的)
        aircraft.setPowerUpTimer(timer);
        timer.start();
    }
}