package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.strategy.NoShootStrategy;

import java.util.List;

public class NormalEnemy extends MobEnemy {
    public NormalEnemy(int locationX, int locationY, int speedX, int speedY, int hp, int score) {
        super(locationX, locationY, speedX, speedY, hp, score, 0, 1);
        this.shootStrategy = new NoShootStrategy(false);
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
        return 0;
    }

}