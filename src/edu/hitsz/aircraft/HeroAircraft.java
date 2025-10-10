package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.strategy.LinearShootStrategy;

import java.util.List;

public class HeroAircraft extends AbstractAircraft {
    private static HeroAircraft instance = null;

    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp, 30, -1);
        this.shootStrategy = new LinearShootStrategy(true);
    }

    public static synchronized HeroAircraft getInstance(int locationX, int locationY, int speedX, int speedY, int hp) {
        if (instance == null) {
            instance = new HeroAircraft(locationX, locationY, speedX, speedY, hp);
        } else {
            instance.setLocation(locationX, locationY);
            instance.hp = hp;
            instance.maxHp = hp;
        }
        return instance;
    }

    public static synchronized HeroAircraft getInstance() {
        if (instance == null) {
            instance = new HeroAircraft(
                Main.WINDOW_WIDTH / 2, 
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(), 
                0, 0, 300);
        }
        return instance;
    }

    @Override
    public void forward() {
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
        return this.getSpeedY() + direction * 15;
    }

}
