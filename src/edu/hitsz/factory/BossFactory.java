package edu.hitsz.factory;

import edu.hitsz.aircraft.Boss;
import edu.hitsz.aircraft.MobEnemy;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;

/**
 * Boss敌机工厂实现
 * @author hitsz
 */
public class BossFactory implements EnemyAircraftFactory {

    private int hp; // Boss血量
    
    /**
     * 默认构造函数，创建默认血量400的Boss
     */
    public BossFactory() {
        this.hp = 400;
    }
    
    /**
     * 自定义血量构造函数
     * @param hp Boss血量
     */
    public BossFactory(int hp) {
        this.hp = hp;
    }

    @Override
    public MobEnemy createEnemyAircraft() {
        return new Boss(
                (int) (0.5 * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                (int) (Main.WINDOW_HEIGHT * 0.15),
                5,
                0,
                this.hp,
                100
        );
    }
}
