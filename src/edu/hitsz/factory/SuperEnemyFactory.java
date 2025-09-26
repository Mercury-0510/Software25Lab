package edu.hitsz.factory;

import edu.hitsz.aircraft.SuperEnemy;
import edu.hitsz.aircraft.MobEnemy;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;

/**
 * 精英敌机工厂实现
 * @author hitsz
 */
public class SuperEnemyFactory implements EnemyAircraftFactory {
    
    @Override
    public MobEnemy createEnemyAircraft() {
        return new SuperEnemy(
            (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
            (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
            0,
            4,
            90,
            50
        );
    }
}