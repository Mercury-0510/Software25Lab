package edu.hitsz.aircraft;

import edu.hitsz.application.Main;

/**
 * 普通敌机
 * 不可射击
 *
 * @author hitsz
 */
public abstract class MobEnemy extends AbstractAircraft {
    private final int score;

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp, int score) {
        super(locationX, locationY, speedX, speedY, hp);
        this.score = score;
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= Main.WINDOW_HEIGHT ) {
            vanish();
        }
    }

    public int getScore(){
        return score;
    };

}
