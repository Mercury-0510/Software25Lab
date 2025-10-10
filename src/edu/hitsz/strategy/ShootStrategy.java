package edu.hitsz.strategy;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.aircraft.AbstractAircraft;
import java.util.List;

/**
 * 射击策略接口，使用策略模式实现不同的射击方式
 * @author hitsz
 */
public interface ShootStrategy {
    /**
     * 射击方法
     * @param aircraft 飞机对象，通过该对象获取位置、速度、威力等参数
     * @return 子弹列表
     */
    List<BaseBullet> shoot(AbstractAircraft aircraft);

    void advance();
}