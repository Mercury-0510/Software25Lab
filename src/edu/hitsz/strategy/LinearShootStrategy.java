package edu.hitsz.strategy;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.aircraft.AbstractAircraft;
import java.util.LinkedList;
import java.util.List;

/**
 * 线性射击策略实现 - 整合英雄机和精英敌机的射击模式
 * 子弹呈直线发射，多发子弹横向分散
 * @author hitsz
 */
public class LinearShootStrategy implements ShootStrategy {
    
    private final boolean isHeroBullet;

    public LinearShootStrategy(boolean isHeroBullet) {
        this.isHeroBullet = isHeroBullet;
    }
    
    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> res = new LinkedList<>();
        int locationX = aircraft.getLocationX();
        int locationY = aircraft.getLocationY();
        int speedX = aircraft.getBulletSpeedX();
        int speedY = aircraft.getBulletSpeedY();
        int power = aircraft.getPower();
        int direction = aircraft.getDirection();
        
        int x = locationX;
        int y = locationY + direction * 2;
        int shootNum = 1;
        BaseBullet bullet;
        
        for (int i = 0; i < shootNum; i++) {
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            int bulletX = x + (i * 2 - shootNum + 1) * 10;
            if (isHeroBullet) {
                bullet = new HeroBullet(bulletX, y, 0, speedY, power);
            } else {
                bullet = new EnemyBullet(bulletX, y, 0, speedY, power);
            }
            res.add(bullet);
        }
        return res;
    }

    @Override
    public void advance(){};
}