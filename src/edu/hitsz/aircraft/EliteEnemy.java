package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.strategy.LinearShootStrategy;

import java.util.List;

public class EliteEnemy extends MobEnemy {

    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp, int score) {
        super(locationX, locationY, speedX, speedY, hp, score, 10, 1);
        this.shootStrategy = new LinearShootStrategy(false);
    }

    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy.shoot(this);
    }

    @Override
    public int getBulletSpeedX() {
        return 0;
    }

    @Override
    public int getBulletSpeedY() {
        return this.getSpeedY() + direction * 5;
    }

}