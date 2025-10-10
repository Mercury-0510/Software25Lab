package edu.hitsz.strategy;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.aircraft.AbstractAircraft;

import java.util.LinkedList;
import java.util.List;

/**
 * 环形射击策略实现 - Boss专用射击模式
 * 子弹呈圆形分布发射，每颗子弹朝不同方向
 * @author hitsz
 */
public class CircularShootStrategy implements ShootStrategy {
    
    private final boolean isHeroBullet;
    private int shootNum;

    public CircularShootStrategy(boolean isHeroBullet) {
        this.isHeroBullet = isHeroBullet;
        this.shootNum = 20;
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
        
        int speed = direction * (speedX + speedY);
        double angleStep = 2 * Math.PI / shootNum; // 计算每颗子弹之间的角度间隔

        for (int i = 0; i < shootNum; i++) {
            double angle = i * angleStep; // 当前子弹的角度
            // 使用sin和cos函数基于角度计算子弹的速度分量
            int bulletSpeedX = (int)((double)speed * Math.cos(angle));
            int bulletSpeedY = (int)((double)speed * Math.sin(angle));
            int x = (int)((double)locationX + 80 * Math.cos(angle));
            int y = (int)((double)locationY + 80 * Math.sin(angle));
            BaseBullet bullet;
            if (isHeroBullet) {
                bullet = new HeroBullet(x, y, bulletSpeedX, bulletSpeedY, power);
            } else {
                bullet = new EnemyBullet(x, y, bulletSpeedX, bulletSpeedY, power);
            }
            res.add(bullet);
        }
        return res;
    }

    @Override
    public void advance() {
        if(this.shootNum <= 30){
            this.shootNum += 5;
        }
    };
}