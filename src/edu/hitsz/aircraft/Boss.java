package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.strategy.CircularShootStrategy;

import java.util.List;

public class Boss extends MobEnemy {

    public Boss(int locationX, int locationY, int speedX, int speedY, int hp, int score) {
        super(locationX, locationY, speedX, speedY, hp, score, 10, 1);
        this.shootStrategy = new CircularShootStrategy(false);
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
        return 5;
    }

}