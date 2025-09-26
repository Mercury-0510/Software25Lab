package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.strategy.HeroShootStrategy;

import java.util.List;

/**
 * 英雄飞机，游戏玩家操控
 * 使用单例模式确保游戏中只有一个英雄机实例
 * @author hitsz
 */
public class HeroAircraft extends AbstractAircraft {

    /**
     * 单例实例
     */
    private static HeroAircraft instance = null;

    /**攻击方式 */

    /**
     * 子弹一次发射数量
     */
    private int shootNum = 1;

    /**
     * 子弹伤害
     */
    private int power = 30;

    /**
     * 子弹射击方向 (向上发射：-1，向下发射：1)
     */
    private int direction = -1;

    /**
     * 私有构造函数，实现单例模式
     * @param locationX 英雄机位置x坐标
     * @param locationY 英雄机位置y坐标
     * @param speedX 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param speedY 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param hp    初始生命值
     */
    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        // 设置英雄机射击策略
        this.shootStrategy = new HeroShootStrategy();
    }

    /**
     * 获取英雄机单例实例
     * @param locationX 英雄机位置x坐标
     * @param locationY 英雄机位置y坐标
     * @param speedX 英雄机射出的子弹的基准速度
     * @param speedY 英雄机射出的子弹的基准速度
     * @param hp 初始生命值
     * @return 英雄机单例实例
     */
    public static synchronized HeroAircraft getInstance(int locationX, int locationY, int speedX, int speedY, int hp) {
        if (instance == null) {
            instance = new HeroAircraft(locationX, locationY, speedX, speedY, hp);
        } else {
            // 如果实例已存在，更新位置和生命值
            instance.setLocation(locationX, locationY);
            instance.hp = hp;
            instance.maxHp = hp;
        }
        return instance;
    }

    /**
     * 获取英雄机单例实例（使用默认参数）
     * @return 英雄机单例实例
     */
    public static synchronized HeroAircraft getInstance() {
        if (instance == null) {
            // 使用默认参数：屏幕中央，底部位置，静止，100生命值
            instance = new HeroAircraft(
                Main.WINDOW_WIDTH / 2, 
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(), 
                0, 0, 500);
        }
        return instance;
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    @Override
    /**
     * 通过射击产生子弹
     * @return 射击出的子弹List
     */
    public List<BaseBullet> shoot() {
        return shootStrategy.shoot(
            this.getLocationX(),
            this.getLocationY(),
            0,
            this.getSpeedY() + direction * 20,
            power,
            shootNum,
            direction
        );
    }

}
