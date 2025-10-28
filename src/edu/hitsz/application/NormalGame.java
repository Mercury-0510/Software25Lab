package edu.hitsz.application;

import edu.hitsz.aircraft.EnemyGenerator;
import edu.hitsz.aircraft.MobEnemy;

/**
 * 普通难度游戏实现类
 * 继承自GameTemplate
 * 普通模式：有Boss，每次召唤不改变Boss血量，难度随时间增加
 *
 * @author hitsz
 */
public class NormalGame extends GameTemplate {

    // 敌机生成概率
    private double eliteEnemyProbability = 0.2; // 精英敌机概率，初始20%

    public NormalGame(String difficulty, boolean soundEnabled) {
        super(difficulty, soundEnabled);
    }

    @Override
    protected MobEnemy createEnemy() {
        // 普通模式：按概率生成普通/精英敌机
        double rand = Math.random();
        if (rand < eliteEnemyProbability) {
            return EnemyGenerator.createEliteEnemy();
        } else {
            return EnemyGenerator.createNormalEnemy();
        }
    }

    @Override
    protected int getBossThreshold() {
        // 普通模式：Boss出现阈值300分
        return 300;
    }

    @Override
    protected void updateTimeBasedDifficulty() {
        // 普通模式：难度随时间增加
        // 每30秒增加精英敌机概率
        int difficultyLevel = time / 30000;
        eliteEnemyProbability = Math.min(0.5, 0.2 + difficultyLevel * 0.05);
    }
}