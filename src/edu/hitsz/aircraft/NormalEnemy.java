package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.strategy.NoShootStrategy;

import java.util.List;

/**
 * 普通敌机
 * 不可射击
 *
 * @author hitsz
 */
public class NormalEnemy extends MobEnemy {
    public NormalEnemy(int locationX, int locationY, int speedX, int speedY, int hp, int score) {
        super(locationX, locationY, speedX, speedY, hp, score);
        // 普通敌机不射击
        this.shootStrategy = new NoShootStrategy();
    }

    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy.shoot(
            this.getLocationX(),
            this.getLocationY(),
            0,
            0,
            0,
            0,
            1
        );
    }

}