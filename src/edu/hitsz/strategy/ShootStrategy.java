package edu.hitsz.strategy;

import edu.hitsz.bullet.BaseBullet;
import java.util.List;

/**
 * 射击策略接口，使用策略模式实现不同的射击方式
 * @author hitsz
 */
public interface ShootStrategy {
    /**
     * 射击方法
     * @param locationX 发射位置x坐标
     * @param locationY 发射位置y坐标
     * @param speedX 子弹x方向速度
     * @param speedY 子弹y方向速度
     * @param power 子弹威力
     * @param shootNum 子弹数量
     * @param direction 射击方向 (-1向上，1向下)
     * @return 子弹列表
     */
    List<BaseBullet> shoot(int locationX, int locationY, int speedX, int speedY, int power, int shootNum, int direction);
}