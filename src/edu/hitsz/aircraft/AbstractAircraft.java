package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.strategy.ShootStrategy;

import java.util.List;

public abstract class AbstractAircraft extends AbstractFlyingObject {

    protected int maxHp;
    protected int hp;
    protected int power;
    protected int direction;
    protected ShootStrategy shootStrategy;
    
    // 定时器线程
    protected Thread powerUpTimer;

    public AbstractAircraft(int locationX, int locationY, int speedX, int speedY, int hp, int power, int direction) {
        super(locationX, locationY, speedX, speedY);
        this.hp = hp;
        this.maxHp = hp;
        this.power = power;
        this.direction = direction;
    }

    public void decreaseHp(int decrease){
        hp -= decrease;
        if(hp > maxHp){
            hp = maxHp;
        }
        if(hp <= 0){
            hp=0;
            vanish();
        }
    }

    public int getHp() {
        return hp;
    }
    
    public int getMaxHp() {
        return maxHp;
    }
    
    public boolean setShootStrategy(ShootStrategy shootStrategy) {
        if(this.shootStrategy.getClass() == shootStrategy.getClass()) {
            this.shootStrategy.advance();
            return false;
        } else {
            this.shootStrategy = shootStrategy;
            return true;
        }
    }

    public abstract List<BaseBullet> shoot();

    public int getPower() {
        return power;
    }

    public int getDirection() {
        return direction;
    }
    
    /**
     * 设置火力道具定时器
     * @param timer 新的定时器线程
     */
    public void setPowerUpTimer(Thread timer) {
        // 如果有旧的定时器,中断它
        if (powerUpTimer != null && powerUpTimer.isAlive()) {
            powerUpTimer.interrupt();
        }
        this.powerUpTimer = timer;
    }

    public abstract int getBulletSpeedX();

    public abstract int getBulletSpeedY();

}


