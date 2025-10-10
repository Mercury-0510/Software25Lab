package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.strategy.ScatterShootStrategy;

import java.util.List;

public class SuperEnemy extends MobEnemy {

    public SuperEnemy(int locationX, int locationY, int speedX, int speedY, int hp, int score) {
        super(locationX, locationY, speedX, speedY, hp, score, 10, 1);
        this.shootStrategy = new ScatterShootStrategy(false);
    }

    @Override
    public List<BaseBullet> shoot() {
        return shootStrategy.shoot(this);
    }

    @Override
    public int getBulletSpeedX() {
        return 5;
    }

    @Override
    public int getBulletSpeedY() {
        return this.getSpeedY() + direction * 5;
    }

}
