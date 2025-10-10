package edu.hitsz.strategy;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.aircraft.AbstractAircraft;
import java.util.LinkedList;
import java.util.List;

/**
 * 不射击策略实现（普通敌机使用）
 * @author hitsz
 */
public class NoShootStrategy implements ShootStrategy {

    public NoShootStrategy(boolean isHeroBullet) {
    }
    
    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        // 不射击，返回空列表
        return new LinkedList<>();
    }

    @Override
    public void advance(){};
}