package edu.hitsz.aircraft;

import edu.hitsz.factory.BossFactory;
import edu.hitsz.factory.EliteEnemyFactory;
import edu.hitsz.factory.SuperEnemyFactory;
import edu.hitsz.factory.EnemyAircraftFactory;
import edu.hitsz.factory.NormalEnemyFactory;

/**
 * 敌机工厂管理器，管理不同类型的敌机工厂
 * @author hitsz
 */
public class EnemyGenerator {
    
    private static EnemyAircraftFactory enemyFactory;
    
    /**
     * 创建普通敌机
     * @return 普通敌机实例
     */
    public static MobEnemy createNormalEnemy() {
        enemyFactory = new NormalEnemyFactory();
        return enemyFactory.createEnemyAircraft();
    }
    
    /**
     * 创建精英敌机
     * @return 精英敌机实例
     */
    public static MobEnemy createEliteEnemy() {
        enemyFactory = new EliteEnemyFactory();
        return enemyFactory.createEnemyAircraft();
    }

    /**
     * 创建超级精英敌机
     * @return 超级精英敌机实例
     */
    public static MobEnemy createSuperEnemy() {
        enemyFactory = new SuperEnemyFactory();
        return enemyFactory.createEnemyAircraft();
    }
    
    /**
     * 根据概率随机创建敌机
     * @return 敌机实例
     */
    public static MobEnemy createRandomEnemy() {
        double rand = Math.random();
        if (rand < 0.9) {
            return createNormalEnemy();
        } else if(rand < 0.97){
            return createEliteEnemy();
        } else {
            return createSuperEnemy();
        }
    }

    /**
     * 创建Boss
     * @return Boss实例
     */
    public static MobEnemy createBoss() {
        enemyFactory = new BossFactory();
        return enemyFactory.createEnemyAircraft();
    }
}