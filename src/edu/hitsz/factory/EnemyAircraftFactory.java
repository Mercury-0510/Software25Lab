package edu.hitsz.factory;

import edu.hitsz.aircraft.MobEnemy;

/**
 * 敌机工厂接口，使用抽象工厂模式
 * @author hitsz
 */
public interface EnemyAircraftFactory {
    /**
     * 创建敌机实例
     * @return 敌机实例
     */
    MobEnemy createEnemyAircraft();
}