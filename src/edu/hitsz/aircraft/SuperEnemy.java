package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.strategy.SuperEnemyShootStrategy;

import java.util.List;

/**
 * 超级精英敌机
 * 可射击
 *
 * @author hitsz
 */
public class SuperEnemy extends MobEnemy {
    /**攻击方式 */

    /**
     * 子弹一次发射数量
     */
    private int shootNum = 3;

    /**
     * 子弹伤害
     */
    private int power = 10;

    /**
     * 子弹射击方向 (向上发射：-1，向下发射：1)
     */
    private int direction = 1;

    /**
     * @param locationX 精英敌机位置x坐标
     * @param locationY 精英敌机位置y坐标
     * @param speedX 精英敌机射出的子弹的基准速度
     * @param speedY 精英敌机射出的子弹的基准速度
     * @param hp    初始生命值
     * @param score 击毁得分
     */

    public SuperEnemy(int locationX, int locationY, int speedX, int speedY, int hp, int score) {
        super(locationX, locationY, speedX, speedY, hp, score);
        // 超级精英敌机可以散射射击
        this.shootStrategy = new SuperEnemyShootStrategy();
    }

    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy.shoot(
                this.getLocationX(),
                this.getLocationY(),
                5,
                this.getSpeedY() + direction * 5,
                power,
                shootNum,
                direction
        );
    }

}
