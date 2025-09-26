package edu.hitsz.factory;

import edu.hitsz.aircraft.MobEnemy;
import edu.hitsz.aircraft.NormalEnemy;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;

/**
 * 普通敌机工厂实现
 * @author hitsz
 */
public class NormalEnemyFactory implements EnemyAircraftFactory {
    
    @Override
    public MobEnemy createEnemyAircraft() {
        return new NormalEnemy(
            (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
            (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
            0,
            10,
            30,
            10
        );
    }
}