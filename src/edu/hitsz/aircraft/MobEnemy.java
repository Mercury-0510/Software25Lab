package edu.hitsz.aircraft;

import edu.hitsz.application.Main;

public abstract class MobEnemy extends AbstractAircraft {
    private final int score;

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp, int score, int power, int direction) {
        super(locationX, locationY, speedX, speedY, hp, power, direction);
        this.score = score;
    }

    @Override
    public void forward() {
        super.forward();
        if (locationY >= Main.WINDOW_HEIGHT ) {
            vanish();
        }
    }

    public int getScore(){
        return score;
    }

}
