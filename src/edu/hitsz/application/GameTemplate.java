package edu.hitsz.application;

import edu.hitsz.RankList;
import edu.hitsz.aircraft.*;
import edu.hitsz.aircraft.SuperEnemy;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.prop.*;
import edu.hitsz.rank.RankDAO;
import edu.hitsz.rank.RankDAOImpl;
import edu.hitsz.observer.BombObserver;
import edu.hitsz.observer.BombEffectHandler;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * 游戏模板抽象类，使用模板方法模式实现不同难度的游戏
 *
 * @author hitsz
 */
public abstract class GameTemplate extends JPanel {

    protected int backGroundTop = 0;

    /**
     * Scheduled 线程池，用于任务调度
     */
    protected final ScheduledExecutorService executorService;

    protected final String difficulty;

    protected boolean soundEnabled;
    /**
     * 时间间隔(ms)，控制刷新频率
     */
    protected int timeInterval = 40;

    protected final HeroAircraft heroAircraft;
    protected final List<MobEnemy> enemyAircrafts;
    protected final List<BaseBullet> heroBullets;
    protected final List<BaseBullet> enemyBullets;
    protected final List<BaseProp> propList;
    
    /**
     * 观察者列表 - 用于炸弹效果通知
     */
    protected final List<BombObserver> observers;

    protected MusicThread bgMusic;

    /**
     * 屏幕中出现的敌机最大数量
     * Boss是否出现
     */
    protected int enemyMaxNumber = 5;
    protected int bossExist = 0;
    protected int scoreCount = 0;

    /**
     * 当前得分与玩家名（临时）
     */
    protected int score = 0;
    /**
     * 当前时刻
     */
    protected int time = 0;

    /**
     * 周期（ms)
     * 指示子弹的发射、敌机的产生频率
     */
    protected int cycleDuration = 400;
    protected int cycleTime = 0;

    /**
     * 英雄机射击周期（ms)
     * 控制英雄机的射击频率
     */
    protected int heroShootCycleDuration = 200; // 原来的一半，即2倍频率
    protected int heroShootCycleTime = 0;

    /**
     * 游戏结束标志
     */
    protected boolean gameOverFlag = false;

    public GameTemplate(String difficulty, boolean soundEnabled) {
        this.difficulty = difficulty;
        this.soundEnabled = soundEnabled;

        if(soundEnabled) {
            bgMusic = new MusicThread("src/videos/bgm.wav", true);
            bgMusic.start();
        }
        heroAircraft = HeroAircraft.getInstance();

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        propList = new LinkedList<>();
        
        // 初始化观察者列表并注册默认观察者
        observers = new LinkedList<>();
        registerObserver(new BombEffectHandler(this));

        /**
         * Scheduled 线程池，用于定时任务调度
         * 关于alibaba code guide：可命名的 ThreadFactory 一般需要第三方包
         * apache 第三方库： org.apache.commons.lang3.concurrent.BasicThreadFactory
         */
        this.executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("game-action-%d").daemon(true).build());

        //启动英雄机鼠标监听
        new HeroController(this, heroAircraft);

    }

    //***********************
    //      Observer Pattern Methods (主题/Subject的方法)
    //***********************

    /**
     * 注册观察者
     * @param observer 要注册的观察者
     */
    public void registerObserver(BombObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * 移除观察者
     * @param observer 要移除的观察者
     */
    public void removeObserver(BombObserver observer) {
        observers.remove(observer);
    }

    /**
     * 通知所有观察者
     * 当炸弹道具被激活时调用
     */
    public void notifyObservers() {
        for (BombObserver observer : observers) {
            observer.onBombActivate(enemyAircrafts, enemyBullets);
        }
    }

    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {

        // 定时任务：绘制、对象产生、碰撞判定、击毁及结束判定
        Runnable task = () -> {

            time += timeInterval;

            // 周期性执行（控制频率）
            if (timeCountAndNewCycleJudge()) {
                // 新敌机产生
                if(enemyAircrafts.size() <= enemyMaxNumber && bossExist == 0) {
                    enemyAircrafts.add(createEnemy());
                }
                // 根据分数判断BOSS是否生成
                if(scoreCount >= getBossThreshold() && bossExist == 0) {
                    enemyAircrafts.add(createBoss());
                    bossExist = 1;
                    onBossAppear();
                }
                // 飞机射出子弹
                shootAction();
            }

            // 子弹移动
            bulletsMoveAction();

            // 飞机移动
            aircraftsMoveAction();

            // 道具移动
            propsMoveAction();

            // 撞击检测
            crashCheckAction();

            // 后处理
            postProcessAction();

            //每个时刻重绘界面
            repaint();

            // 游戏结束检查英雄机是否存活
            if (heroAircraft.getHp() <= 0) {
                // 游戏结束
                executorService.shutdown();
                gameOverFlag = true;
                gameOver();
            }

        };

        /**
         * 以固定延迟时间进行执行
         * 本次任务执行完成后，需要延迟设定的延迟时间，才会执行新的任务
         */
        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);

    }

    //***********************
    //      Template Methods
    //***********************

    /**
     * 模板方法：创建敌机（普通/精英/超级）
     * 不同难度的子类需要实现此方法来定义具体的敌机生成策略
     */
    protected abstract MobEnemy createEnemy();

    /**
     * 模板方法：创建Boss
     * 不同难度的子类可以重写此方法来定义不同的Boss
     */
    protected MobEnemy createBoss() {
        return EnemyGenerator.createBoss();
    }

    /**
     * 模板方法：获取Boss出现的分数阈值
     * 不同难度的子类可以重写此方法来定义不同的阈值
     */
    protected int getBossThreshold() {
        return 400;
    }

    /**
     * 模板方法：当Boss出现时的处理
     * 不同难度的子类可以重写此方法来定义不同的处理方式
     */
    protected void onBossAppear() {
        if(soundEnabled) {
            bgMusic.close();
            bgMusic = new MusicThread("src/videos/bgm_boss.wav", true);
            bgMusic.start();
        }
    }

    /**
     * 模板方法：更新游戏难度（基于时间）
     * 不同难度的子类可以重写此方法来定义随时间变化的难度调整
     */
    protected void updateTimeBasedDifficulty() {
        // 默认实现为空，子类可以根据需要重写
    }

    //***********************
    //      Action 各部分
    //***********************

    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        heroShootCycleTime += timeInterval;

        boolean isNewCycle = false;
        if (cycleTime >= cycleDuration) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            // 更新基于时间的难度
            updateTimeBasedDifficulty();
            isNewCycle = true;
        }

        return isNewCycle;
    }

    /**
     * 判断是否应该发射英雄机子弹
     */
    private boolean shouldHeroShoot() {
        if (heroShootCycleTime >= heroShootCycleDuration) {
            heroShootCycleTime %= heroShootCycleDuration;
            return true;
        }
        return false;
    }

    private void shootAction() {
        // 敌机射击 - 只在新的主周期时射击
        for(MobEnemy Enemy : enemyAircrafts) {
            enemyBullets.addAll(Enemy.shoot());
        }
        // 英雄射击 - 根据英雄机射击周期判断
        if (shouldHeroShoot()) {
            heroBullets.addAll(heroAircraft.shoot());
        }
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (MobEnemy enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    private void propsMoveAction() {
        for (BaseProp prop : propList) {
            prop.forward();
        }
    }

    private void gameOver() {
        System.out.println("Game over!");
        if(soundEnabled) {
            bgMusic.close();
            MusicThread gameOverMusic = new MusicThread("src/videos/game_over.wav", false);
            gameOverMusic.start();
        }

        // 使用SwingUtilities确保在EDT线程上执行GUI操作
        SwingUtilities.invokeLater(() -> {
            // 获取父窗口
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

            // 使用RankList的静态方法显示输入对话框
            String playerName = RankList.showInputDialog(parentFrame, score, difficulty);

            if (playerName != null) {
                // 根据难度生成对应的排行榜文件路径
                String filePath = "src/edu/hitsz/rank/rank_" + difficulty + ".csv";

                // 创建排行榜DAO实例
                RankDAO rankDAO = new RankDAOImpl(filePath);

                // 添加本次游戏记录到排行榜
                rankDAO.addRecord(playerName, score);

                // 显示排行榜
                System.out.println("\n游戏结束！最终得分: " + score);
                rankDAO.showRank();

                // 显示排行榜界面
                RankList rankList = new RankList(rankDAO, difficulty);
                rankList.setVisible(true);
            }
        });
    }

    /**
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() {
        // TODO 敌机子弹攻击英雄
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            if (heroAircraft.crash(bullet)) {
                // 撞击到子弹
                // 损失一定生命值
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }
        // 英雄子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (MobEnemy enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());
                    if(soundEnabled) {
                        new MusicThread("src/videos/bullet_hit.wav", false).start();
                    }
                    bullet.vanish();
                    if (enemyAircraft.notValid()) {
                        // TODO 获得分数，产生道具补给
                        score += enemyAircraft.getScore();
                        scoreCount += enemyAircraft.getScore();
                        // 精英机
                        if(enemyAircraft instanceof EliteEnemy || enemyAircraft instanceof SuperEnemy) {
                            double rand = Math.random();
                            if(rand < 0.8) {
                                propList.add(PropGenerator.createRandomProp(rand, enemyAircraft.getLocationX(), enemyAircraft.getLocationY()));
                            }
                        }
                        // BOSS
                        if(enemyAircraft instanceof Boss) {
                            bossExist = 0;
                            scoreCount = 0;
                            for(int i = 0; i < 3; i++) {
                                double rand = Math.random();
                                propList.add(PropGenerator.createRandomProp(rand, enemyAircraft.getLocationX() - 50 + (i * 50), enemyAircraft.getLocationY()));
                            }
                            onBossDefeated();
                        }
                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // Todo: 我方获得道具，道具生效
        for (BaseProp prop : propList) {
            if (prop.notValid()) {
                continue;
            }
            if (heroAircraft.crash(prop)) {
                // 撞击到道具
                // 生效
                prop.active(heroAircraft);

                // Handle bomb prop specifically with observer pattern
                if (prop instanceof BombProp) {
                    // 通知所有观察者处理炸弹效果
                    notifyObservers();
                    if(soundEnabled) {
                        new MusicThread("src/videos/bomb_explosion.wav", false).start();
                    }
                } else {
                    if(soundEnabled) {
                        new MusicThread("src/videos/get_supply.wav", false).start();
                    }
                }
                prop.vanish();
            }
        }
    }

    /**
     * 模板方法：当Boss被击败时的处理
     * 不同难度的子类可以重写此方法来定义不同的处理方式
     */
    protected void onBossDefeated() {
        if(soundEnabled) {
            bgMusic.close();
            bgMusic = new MusicThread("src/videos/bgm.wav", true);
            bgMusic.start();
        }
    }

    public void addScore(int points) {
        this.score += points;
        this.scoreCount += points;
    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        propList.removeIf(AbstractFlyingObject::notValid);
    }


    //***********************
    //      Paint 各部分
    //***********************

    /**
     * 重写paint方法
     * 通过重复调用paint方法，实现游戏动画
     *
     * @param  g
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        // 绘制背景,图片滚动
        switch (difficulty) {
            case "easy":
                g.drawImage(ImageManager.BACKGROUND_IMAGE_EZ, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
                g.drawImage(ImageManager.BACKGROUND_IMAGE_EZ, 0, this.backGroundTop, null);
                break;
            case "normal":
                g.drawImage(ImageManager.BACKGROUND_IMAGE_NM, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
                g.drawImage(ImageManager.BACKGROUND_IMAGE_NM, 0, this.backGroundTop, null);
                break;
            case "hard":
                g.drawImage(ImageManager.BACKGROUND_IMAGE_HD, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
                g.drawImage(ImageManager.BACKGROUND_IMAGE_HD, 0, this.backGroundTop, null);
                break;
            default:
                g.drawImage(ImageManager.BACKGROUND_IMAGE_EZ, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
                g.drawImage(ImageManager.BACKGROUND_IMAGE_EZ, 0, this.backGroundTop, null);
            break;
        }
        this.backGroundTop += 1;
        if (this.backGroundTop == Main.WINDOW_HEIGHT) {
            this.backGroundTop = 0;
        }

        // 先绘制子弹，后绘制飞机
        // 这样子弹显示在飞机的下层
        paintImageWithPositionRevised(g, enemyBullets);
        paintImageWithPositionRevised(g, heroBullets);

        paintImageWithPositionRevised(g, enemyAircrafts);
        paintImageWithPositionRevised(g, propList);

        g.drawImage(ImageManager.HERO_IMAGE, heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);

        //绘制得分和生命值
        paintScoreAndLife(g);

    }

    private void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
        if (objects.size() == 0) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            BufferedImage image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            g.drawImage(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, null);
        }
    }

    private void paintScoreAndLife(Graphics g) {
        int x = 10;
        int y = 25;
        g.setColor(new Color(16711680));
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SCORE:" + this.score, x, y);
        y = y + 20;
        g.drawString("LIFE:" + this.heroAircraft.getHp(), x, y);
    }
}