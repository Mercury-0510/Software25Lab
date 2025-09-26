package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.strategy.BossShootStrategy;

import java.util.List;

/**
 * 精英敌机
 * 可射击
 *
 * @author hitsz
 */
public class Boss extends MobEnemy {
    /**攻击方式 */

    /**
     * 子弹一次发射数量
     */
    private int shootNum = 20;

    /**
     * 子弹伤害
     */
    private int power = 10;

    /**
     * 子弹射击方向 (向上发射：-1，向下发射：1，环形子弹：0)
     */
    private int direction = 0;

    /**
     * @param locationX 精英敌机位置x坐标
     * @param locationY 精英敌机位置y坐标
     * @param speedX 精英敌机射出的子弹的基准速度
     * @param speedY 精英敌机射出的子弹的基准速度
     * @param hp    初始生命值
     * @param score 击毁得分
     */

    public Boss(int locationX, int locationY, int speedX, int speedY, int hp, int score) {
        super(locationX, locationY, speedX, speedY, hp, score);
        // 精英敌机可以射击
        this.shootStrategy = new BossShootStrategy();
    }

    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy.shoot(
                this.getLocationX(),
                this.getLocationY(),
                5,
                5,
                power,
                shootNum,
                direction
        );
    }

}