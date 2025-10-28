package edu.hitsz.application;

import edu.hitsz.aircraft.EnemyGenerator;
import edu.hitsz.aircraft.MobEnemy;

/**
 * 简单难度游戏实现类
 * 继承自GameTemplate
 * 简单模式：无Boss，难度不随时间增加
 *
 * @author hitsz
 */
public class EasyGame extends GameTemplate {

    public EasyGame(String difficulty, boolean soundEnabled) {
        super(difficulty, soundEnabled);
    }

    @Override
    protected MobEnemy createEnemy() {
        // 简单模式：只生成普通敌机
        return EnemyGenerator.createNormalEnemy();
    }

    @Override
    protected int getBossThreshold() {
        // 简单模式：无Boss，返回极大值使其永不触发
        return Integer.MAX_VALUE;
    }

    @Override
    protected void updateTimeBasedDifficulty() {
        // 简单模式：难度不随时间增加
    }
}