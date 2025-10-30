package edu.hitsz.application;

import edu.hitsz.aircraft.EnemyGenerator;
import edu.hitsz.aircraft.MobEnemy;

/**
 * 困难难度游戏实现类
 * 继承自GameTemplate
 * 困难模式：有Boss，每次召唤提升Boss血量，难度随时间增加
 *
 * @author hitsz
 */
public class HardGame extends GameTemplate {

    // 敌机生成概率
    private double eliteEnemyProbability = 0.3; // 精英敌机概率，初始30%
    
    // Boss血量
    private int bossHp = 400; // 初始Boss血量
    private int bossHpIncrement = 400; // 每次召唤增加的血量

    public HardGame(String difficulty, boolean soundEnabled) {
        super(difficulty, soundEnabled);
        // 困难模式：敌机生成更快
        this.cycleDuration = 300;
    }

    @Override
    protected MobEnemy createEnemy() {
        // 困难模式：按概率生成普通/精英敌机
        double rand = Math.random();
        if (rand < eliteEnemyProbability) {
            return EnemyGenerator.createEliteEnemy();
        } else {
            return EnemyGenerator.createNormalEnemy();
        }
    }

    @Override
    protected MobEnemy createBoss() {
        // 困难模式：创建Boss并设置递增的血量
        return EnemyGenerator.createBoss(bossHp);
    }

    @Override
    protected int getBossThreshold() {
        // 困难模式：Boss出现阈值200分
        return 200;
    }

    @Override
    protected void updateTimeBasedDifficulty() {
        // 困难模式：难度随时间快速增加
        // 每20秒增加精英敌机概率和敌机数量
        int difficultyLevel = time / 20000;
        
        // 提升精英敌机概率
        eliteEnemyProbability = Math.min(0.6, 0.3 + difficultyLevel * 0.05);
        
        // 提升最大敌机数量
        super.enemyMaxNumber = 5 + difficultyLevel;
    }

    @Override
    protected void onBossDefeated() {
        super.onBossDefeated();
        // 困难模式：每击败一次Boss，下次Boss血量增加
        bossHp += bossHpIncrement;
    }
}