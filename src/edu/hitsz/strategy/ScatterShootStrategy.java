package edu.hitsz.strategy;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.aircraft.AbstractAircraft;

import java.util.LinkedList;
import java.util.List;

/**
 * 散射策略实现 - 超级敌机射击模式
 * 子弹呈扇形发射，每颗子弹有不同的横向速度
 * @author hitsz
 */
public class ScatterShootStrategy implements ShootStrategy {
    
    private final boolean isHeroBullet;
    private int shootNum;

    public ScatterShootStrategy(boolean isHeroBullet) {
        this.isHeroBullet = isHeroBullet;
        this.shootNum = 3;
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

        BaseBullet bullet;
        
        for (int i = 0; i < shootNum; i++) {
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散，且每颗子弹有不同的横向速度形成散射效果
            int bulletX = x + ((i * 2 - shootNum + 1) * 10)/(shootNum/2);
            int bulletSpeedX = ((i - shootNum/2) * speedX)/(shootNum/2); // 不同的横向速度
            
            if (isHeroBullet) {
                bullet = new HeroBullet(bulletX, y, bulletSpeedX, speedY, power);
            } else {
                bullet = new EnemyBullet(bulletX, y, bulletSpeedX, speedY, power);
            }
            res.add(bullet);
        }
        return res;
    }

    @Override
    public void advance() {
        if(this.shootNum <= 5) {
            this.shootNum += 2;
        }
    }
}