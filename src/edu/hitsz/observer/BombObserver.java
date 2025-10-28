package edu.hitsz.observer;

import edu.hitsz.aircraft.MobEnemy;
import edu.hitsz.bullet.BaseBullet;

import java.util.List;

/**
 * Observer interface for bomb events
 */
public interface BombObserver {
    /**
     * Called when bomb is activated
     * @param enemies List of all enemy aircraft
     * @param enemyBullets List of all enemy bullets
     */
    void onBombActivate(List<MobEnemy> enemies, List<BaseBullet> enemyBullets);
}