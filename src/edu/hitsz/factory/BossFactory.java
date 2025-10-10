package edu.hitsz.factory;

import edu.hitsz.aircraft.Boss;
import edu.hitsz.aircraft.MobEnemy;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;

/**
 * 精英敌机工厂实现
 * @author hitsz
 */
public class BossFactory implements EnemyAircraftFactory {

    @Override
    public MobEnemy createEnemyAircraft() {
        return new Boss(
                (int) (0.5 * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                (int) (Main.WINDOW_HEIGHT * 0.15),
                5,
                0,
                300,
                100
        );
    }
}
