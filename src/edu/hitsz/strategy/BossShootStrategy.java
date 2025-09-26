package edu.hitsz.strategy;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 敌机射击策略实现
 * @author hitsz
 */
public class BossShootStrategy implements ShootStrategy {

    @Override
    public List<BaseBullet> shoot(int locationX, int locationY, int speedX, int speedY, int power, int shootNum, int direction) {
        List<BaseBullet> res = new LinkedList<>();
        double angleStep = 2 * Math.PI / shootNum; // 计算每颗子弹之间的角度间隔

        for (int i = 0; i < shootNum; i++) {
            double angle = i * angleStep; // 当前子弹的角度
            // 使用sin和cos函数基于角度计算子弹的速度分量
            int bulletSpeedX = (int)((double)speedX * Math.cos(angle));
            int bulletSpeedY = (int)((double)speedY * Math.sin(angle));
            int x = (int)((double)locationX + 120 * Math.cos(angle));
            int y = (int)((double)locationY + 120 * Math.sin(angle));
            BaseBullet bullet = new EnemyBullet(x, y, bulletSpeedX, bulletSpeedY, power);
            res.add(bullet);
        }
        return res;
    }
}

