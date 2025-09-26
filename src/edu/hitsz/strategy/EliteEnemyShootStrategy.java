package edu.hitsz.strategy;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import java.util.LinkedList;
import java.util.List;

/**
 * 敌机射击策略实现
 * @author hitsz
 */
public class EliteEnemyShootStrategy implements ShootStrategy {
    
    @Override
    public List<BaseBullet> shoot(int locationX, int locationY, int speedX, int speedY, int power, int shootNum, int direction) {
        List<BaseBullet> res = new LinkedList<>();
        int x = locationX;
        int y = locationY + direction * 2;
        BaseBullet bullet;
        
        for (int i = 0; i < shootNum; i++) {
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            bullet = new EnemyBullet(x + (i * 2 - shootNum + 1) * 10, y, speedX, speedY, power);
            res.add(bullet);
        }
        return res;
    }
}