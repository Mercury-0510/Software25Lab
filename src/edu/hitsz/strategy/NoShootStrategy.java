package edu.hitsz.strategy;

import edu.hitsz.bullet.BaseBullet;
import java.util.LinkedList;
import java.util.List;

/**
 * 不射击策略实现（普通敌机使用）
 * @author hitsz
 */
public class NoShootStrategy implements ShootStrategy {
    
    @Override
    public List<BaseBullet> shoot(int locationX, int locationY, int speedX, int speedY, int power, int shootNum, int direction) {
        // 不射击，返回空列表
        return new LinkedList<>();
    }
}