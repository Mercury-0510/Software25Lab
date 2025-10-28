package edu.hitsz.observer;

import edu.hitsz.aircraft.Boss;
import edu.hitsz.aircraft.MobEnemy;
import edu.hitsz.aircraft.SuperEnemy;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.application.GameTemplate;

import java.util.List;
import java.util.Iterator;

/**
 * Concrete observer that handles bomb activation effects
 */
public class BombEffectHandler implements BombObserver {
    private GameTemplate game;

    public BombEffectHandler(GameTemplate game) {
        this.game = game;
    }

    @Override
    public void onBombActivate(List<MobEnemy> enemies, List<BaseBullet> enemyBullets) {
        int totalScore = 0;

        // Clear all enemy bullets
        for (BaseBullet bullet : enemyBullets) {
            bullet.vanish();
        }

        // Process enemies
        Iterator<MobEnemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            MobEnemy enemy = enemyIterator.next();

            // Boss enemies are unaffected
            if (enemy instanceof Boss) {
                continue;
            }
            // Super enemies take damage but are not destroyed
            else if (enemy instanceof SuperEnemy) {
                enemy.decreaseHp(50); // Reduce super enemy HP
                if (enemy.notValid()) {
                    // Super enemy destroyed, add score
                    totalScore += enemy.getScore();
                }
            }
            // Normal and Elite enemies are destroyed
            else {
                enemy.vanish();
                totalScore += enemy.getScore();
            }
        }

        // Add the collected score to the game
        if (game != null) {
            game.addScore(totalScore);
        }
    }
}