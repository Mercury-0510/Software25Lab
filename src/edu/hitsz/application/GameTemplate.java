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
import edu.hitsz.effect.Particle;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.CopyOnWriteArrayList;

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
    protected int timeInterval = 20;

    protected final HeroAircraft heroAircraft;
    protected final List<MobEnemy> enemyAircrafts;
    protected final List<BaseBullet> heroBullets;
    protected final List<BaseBullet> enemyBullets;
    protected final List<BaseProp> propList;
    protected final List<Particle> particles;
    
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
     * 连击系统
     */
    protected int comboCount = 0;  // 连击数
    protected int maxCombo = 0;     // 最大连击数
    protected long lastKillTime = 0; // 上次击杀时间
    protected static final long COMBO_TIMEOUT = 3000; // 连击超时时间(ms)
    
    /**
     * Boss血条显示
     */
    protected Boss currentBoss = null;
    
    /**
     * 特效提示
     */
    protected String statusMessage = "";
    protected long messageDisplayTime = 0;
    protected static final long MESSAGE_DURATION = 2000; // 消息显示时长(ms)

    /**
     * 周期（ms)
     * 指示子弹的发射、敌机的产生频率
     */
    protected int cycleDuration = 200;
    protected int cycleTime = 0;

    /**
     * 英雄机射击周期（ms)
     * 控制英雄机的射击频率
     */
    protected int heroShootCycleDuration = 100;
    protected int heroShootCycleTime = 0;

    /**
     * 敌机射击周期（ms)
     * 控制敌机的射击频率
     */
    protected int enemyShootCycleDuration = 600;
    protected int enemyShootCycleTime = 0;

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
    particles = new CopyOnWriteArrayList<>();
        
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
    
    //***********************
    //      游戏增强功能
    //***********************
    
    /**
     * 显示状态消息
     */
    protected void showMessage(String message) {
        this.statusMessage = message;
        this.messageDisplayTime = System.currentTimeMillis();
    }
    
    /**
     * 增加连击
     */
    protected void addCombo() {
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastKillTime < COMBO_TIMEOUT) {
            comboCount++;
            if(comboCount > maxCombo) {
                maxCombo = comboCount;
            }
            // 连击奖励提示
            if(comboCount >= 10 && comboCount % 5 == 0) {
                showMessage("🔥 COMBO x" + comboCount + "! 🔥");
            }
        } else {
            comboCount = 1;
        }
        lastKillTime = currentTime;
    }
    
    /**
     * 重置连击
     */
    protected void resetCombo() {
        if(System.currentTimeMillis() - lastKillTime > COMBO_TIMEOUT) {
            comboCount = 0;
        }
    }
    
    /**
     * 根据连击数获得额外分数
     */
    protected int getComboBonus(int baseScore) {
        if(comboCount >= 20) return (int)(baseScore * 0.5);
        if(comboCount >= 10) return (int)(baseScore * 0.3);
        if(comboCount >= 5) return (int)(baseScore * 0.2);
        return 0;
    }
    
    /**
     * 创建爆炸粒子效果
     */
    protected void createExplosion(int x, int y, Color color, int particleCount) {
        // 更夸张的粒子效果：分两档（大颗粒 + 小颗粒），并加入随机颜色偏移与更大速度/寿命
        int bigCount = Math.max(1, particleCount / 4);
        int smallCount = particleCount - bigCount;

        // 先产生大颗粒，增强视觉冲击
        for (int i = 0; i < bigCount; i++) {
            double angle = Math.random() * Math.PI * 2;
            double speed = 4 + Math.random() * 8; // 更快
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed - Math.random() * 2; // 一部分向上
            int life = 30 + (int)(Math.random() * 40);
            int size = 6 + (int)(Math.random() * 8);
            // 适当扰动颜色
            Color c = new Color(
                    Math.min(255, Math.max(0, color.getRed() + (int)((Math.random() - 0.5) * 60))),
                    Math.min(255, Math.max(0, color.getGreen() + (int)((Math.random() - 0.5) * 60))),
                    Math.min(255, Math.max(0, color.getBlue() + (int)((Math.random() - 0.5) * 60))),
                    255
            );
            particles.add(new Particle(x - size/2 + (int)(Math.random()*8-4), y - size/2 + (int)(Math.random()*8-4), vx, vy, life, c, size));
        }

        // 再产生大量小颗粒，填充效果
        for (int i = 0; i < smallCount; i++) {
            double angle = Math.random() * Math.PI * 2;
            double speed = 2 + Math.random() * 6; // 稍大范围速度
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed + Math.random() * 1.5;
            int life = 15 + (int)(Math.random() * 30);
            int size = 2 + (int)(Math.random() * 6);
            Color c = new Color(
                    Math.min(255, Math.max(0, color.getRed() + (int)((Math.random() - 0.5) * 40))),
                    Math.min(255, Math.max(0, color.getGreen() + (int)((Math.random() - 0.5) * 40))),
                    Math.min(255, Math.max(0, color.getBlue() + (int)((Math.random() - 0.5) * 40))),
                    200
            );
            particles.add(new Particle(x - size/2 + (int)(Math.random()*6-3), y - size/2 + (int)(Math.random()*6-3), vx, vy, life, c, size));
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
                    MobEnemy boss = createBoss();
                    enemyAircrafts.add(boss);
                    if(boss instanceof Boss) {
                        currentBoss = (Boss) boss;
                        showMessage("⚠️ WARNING! BOSS APPROACHING! ⚠️");
                    }
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
            
            // 更新粒子
            updateParticles();

            // 撞击检测
            crashCheckAction();

            // 后处理
            postProcessAction();
            
            // 检查连击超时
            resetCombo();

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
        enemyShootCycleTime += timeInterval;

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
    
    private boolean shouldEnemyShoot() {
        if (enemyShootCycleTime >= enemyShootCycleDuration) {
            enemyShootCycleTime %= enemyShootCycleDuration;
            return true;
        }
        return false;
    }

    private void shootAction() {
        // 敌机射击 - 降低射击频率
        if (shouldEnemyShoot()) {
            for(MobEnemy enemy : enemyAircrafts) {
                enemyBullets.addAll(enemy.shoot());
            }
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
    
    private void updateParticles() {
        for(Particle particle : particles) {
            particle.update();
        }
        particles.removeIf(p -> !p.isAlive());
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
                        // 击杀敌机：增加连击和分数
                        addCombo();
                        int baseScore = enemyAircraft.getScore();
                        int comboBonus = getComboBonus(baseScore);
                        score += baseScore + comboBonus;
                        scoreCount += baseScore + comboBonus;
                        
                        // 创建爆炸粒子效果
                        Color explosionColor = (enemyAircraft instanceof Boss) ? 
                            new Color(255, 0, 0) : new Color(255, 165, 0);
                        int particleCount = (enemyAircraft instanceof Boss) ? 100 : 50;
                        createExplosion(enemyAircraft.getLocationX(), 
                                      enemyAircraft.getLocationY(), 
                                      explosionColor, 
                                      particleCount);
                        
                        // 精英机和超级机掉落几率较高
                        if(enemyAircraft instanceof EliteEnemy || enemyAircraft instanceof SuperEnemy) {
                            double rand = Math.random();
                            if(rand < 0.8) {
                                propList.add(PropGenerator.createRandomProp(rand, enemyAircraft.getLocationX(), enemyAircraft.getLocationY()));
                            }
                        }
                        // 简单模式下，普通敌机也有小概率掉落道具
                        else if (enemyAircraft instanceof NormalEnemy && "easy".equalsIgnoreCase(difficulty)) {
                            double rand = Math.random();
                            // 10% 概率掉落
                            if (rand < 0.2) {
                                propList.add(PropGenerator.createRandomProp(5 * rand, enemyAircraft.getLocationX(), enemyAircraft.getLocationY()));
                            }
                        }
                        // BOSS
                        if(enemyAircraft instanceof Boss) {
                            currentBoss = null;
                            bossExist = 0;
                            scoreCount = 0;
                            showMessage("✨ BOSS DEFEATED! ✨");
                            for(int i = 0; i < 3; i++) {
                                double rand = Math.random();
                                propList.add(PropGenerator.createRandomProp(rand, enemyAircraft.getLocationX() - 50 + (i * 50), enemyAircraft.getLocationY()));
                            }
                            onBossDefeated();
                        }
                    }
                }
                // 英雄机 与 敌机 相撞，敌机消失，英雄扣血并产生血状粒子效果
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    // 敌机消失
                    enemyAircraft.vanish();
                    // 英雄受到伤害，减少100HP
                    heroAircraft.decreaseHp(100);
                    // 产生血状粒子效果
                    createExplosion(enemyAircraft.getLocationX(), enemyAircraft.getLocationY(), new Color(180, 0, 0), 80);
                    // 播放爆炸音效
                    if (soundEnabled) {
                        new MusicThread("src/videos/bomb_explosion.wav", false).start();
                    }
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
                    // 炸弹道具触发大范围粒子爆炸特效
                    createExplosion(heroAircraft.getLocationX(), heroAircraft.getLocationY(), new Color(255, 215, 0), 220);
                    for (MobEnemy enemy : enemyAircrafts) {
                        if (!enemy.notValid()) {
                            createExplosion(enemy.getLocationX(), enemy.getLocationY(), new Color(255, 69, 0), 120);
                        }
                    }
                    showMessage("💣 MEGA BOMB! 💥");
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
        
        // 绘制粒子效果
        for(Particle particle : particles) {
            particle.draw(g);
        }

        g.drawImage(ImageManager.HERO_IMAGE, heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);

    // 绘制英雄机血条
    paintHeroHealthBar(g);

        //绘制得分和生命值
        paintScoreAndLife(g);
        
        //绘制Boss血条
        paintBossHealthBar(g);
        
        //绘制状态消息
        paintStatusMessage(g);

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

    private void paintHeroHealthBar(Graphics g) {
        int maxHp = heroAircraft.getMaxHp();
        if (maxHp <= 0) {
            return;
        }
        double hpPercent = (double) heroAircraft.getHp() / maxHp;
        int barWidth = 80;
        int barHeight = 8;
    int barX = heroAircraft.getLocationX() - barWidth / 2;
    int barY = heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2 - barHeight - 6;

        // 背景
        g.setColor(new Color(60, 60, 60, 180));
        g.fillRoundRect(barX, barY, barWidth, barHeight, 6, 6);

        // 血量
        int currentWidth = (int) (barWidth * Math.max(0, Math.min(1.0, hpPercent)));
        if (hpPercent > 0.5) {
            g.setColor(new Color(0, 200, 0, 220));
        } else if (hpPercent > 0.25) {
            g.setColor(new Color(255, 200, 0, 220));
        } else {
            g.setColor(new Color(255, 80, 80, 220));
        }
        g.fillRoundRect(barX, barY, currentWidth, barHeight, 6, 6);

        // 边框
        g.setColor(new Color(255, 255, 255, 200));
        g.drawRoundRect(barX, barY, barWidth, barHeight, 6, 6);
    }

    private void paintScoreAndLife(Graphics g) {
        int x = 10;
        int y = 25;
        g.setColor(new Color(16711680));
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SCORE:" + this.score, x, y);
        y = y + 20;
        g.drawString("LIFE:" + this.heroAircraft.getHp(), x, y);
        
        // 显示难度
        y = y + 20;
        g.setColor(new Color(255, 215, 0));
        g.drawString("DIFFICULTY:" + difficulty.toUpperCase(), x, y);
        
        // 显示时间
        y = y + 20;
        int seconds = time / 1000;
        g.setColor(new Color(100, 200, 255));
        g.drawString(String.format("TIME:%02d:%02d", seconds / 60, seconds % 60), x, y);
        
        // 显示连击
        if(comboCount > 1) {
            y = y + 20;
            g.setColor(new Color(255, 100, 100));
            g.setFont(new Font("SansSerif", Font.BOLD, 24));
            g.drawString("COMBO x" + comboCount + "!", x, y);
        }
        
        // 显示最大连击
        if(maxCombo > 1) {
            y = y + 20;
            g.setColor(new Color(255, 255, 100));
            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            g.drawString("MAX COMBO:" + maxCombo, x, y);
        }
    }
    
    /**
     * 绘制Boss血条
     */
    private void paintBossHealthBar(Graphics g) {
        if(currentBoss != null && currentBoss.getHp() > 0) {
            int barWidth = 300;
            int barHeight = 20;
            int barX = (Main.WINDOW_WIDTH - barWidth) / 2;
            int barY = 50;
            
            // 计算血量百分比（使用Boss的实际最大血量）
            int maxHp = currentBoss.getMaxHp();
            double hpPercent = (double)currentBoss.getHp() / maxHp;
            // 确保血条宽度不超过最大宽度
            int currentBarWidth = Math.min((int)(barWidth * hpPercent), barWidth);
            
            // 绘制背景
            g.setColor(new Color(100, 100, 100));
            g.fillRect(barX, barY, barWidth, barHeight);
            
            // 绘制血条
            if(hpPercent > 0.5) {
                g.setColor(new Color(0, 255, 0));
            } else if(hpPercent > 0.25) {
                g.setColor(new Color(255, 255, 0));
            } else {
                g.setColor(new Color(255, 0, 0));
            }
            g.fillRect(barX, barY, currentBarWidth, barHeight);
            
            // 绘制边框
            g.setColor(Color.WHITE);
            g.drawRect(barX, barY, barWidth, barHeight);
            
            // 绘制文字（显示当前血量/最大血量）
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            String text = "BOSS: " + currentBoss.getHp() + " / " + maxHp + " HP";
            g.drawString(text, barX + (barWidth - g.getFontMetrics().stringWidth(text)) / 2, barY - 5);
        }
    }
    
    /**
     * 绘制状态消息
     */
    private void paintStatusMessage(Graphics g) {
        if(!statusMessage.isEmpty()) {
            long currentTime = System.currentTimeMillis();
            if(currentTime - messageDisplayTime < MESSAGE_DURATION) {
                // 计算透明度（渐隐效果）
                float alpha = 1.0f - (float)(currentTime - messageDisplayTime) / MESSAGE_DURATION;
                g.setColor(new Color(255, 255, 255, (int)(alpha * 255)));
                g.setFont(new Font("SansSerif", Font.BOLD, 32));
                
                // 居中显示
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(statusMessage);
                int x = (Main.WINDOW_WIDTH - textWidth) / 2;
                int y = Main.WINDOW_HEIGHT / 3;
                
                // 绘制阴影
                g.setColor(new Color(0, 0, 0, (int)(alpha * 150)));
                g.drawString(statusMessage, x + 2, y + 2);
                
                // 绘制文字
                g.setColor(new Color(255, 255, 255, (int)(alpha * 255)));
                g.drawString(statusMessage, x, y);
            } else {
                statusMessage = "";
            }
        }
    }
}