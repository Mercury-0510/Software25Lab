package edu.hitsz.prop;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.strategy.LinearShootStrategy;
import edu.hitsz.strategy.ScatterShootStrategy;
import edu.hitsz.strategy.ShootStrategy;

public class BulletProp extends BaseProp {

    private static final int DURATION = 5000; // 持续时间5秒

    public BulletProp(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
    }

    @Override
    public void active(AbstractAircraft aircraft) {
        ShootStrategy newshootStrategy = new ScatterShootStrategy(true);
        if(aircraft.setShootStrategy(newshootStrategy)){
            System.out.println("BulletSupply active!");
        } else {
            System.out.println("BulletSupply advance! Timer reset!");
        }
        
        // 创建新的定时器线程
        Thread timer = new Thread(() -> {
            try {
                Thread.sleep(DURATION);
                aircraft.setShootStrategy(new LinearShootStrategy(true));
                System.out.println("BulletSupply effect ended!");
            } catch (InterruptedException e) {
                System.out.println("BulletSupply timer interrupted (reset by new power-up)");
            }
        });
        
        // 设置定时器(会自动中断旧的)
        aircraft.setPowerUpTimer(timer);
        timer.start();
    }
}
