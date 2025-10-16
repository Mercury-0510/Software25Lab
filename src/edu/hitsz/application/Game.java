package edu.hitsz.application;

import edu.hitsz.RankList;
import edu.hitsz.aircraft.*;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.prop.*;
import edu.hitsz.rank.RankDAO;
import edu.hitsz.rank.RankDAOImpl;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * 游戏主面板，游戏启动
 *
 * @author hitsz
 */
public class Game extends JPanel {

    private int backGroundTop = 0;

    /**
     * Scheduled 线程池，用于任务调度
     */
    private final ScheduledExecutorService executorService;

    private final String difficulty;

    private boolean soundEnabled;
    /**
     * 时间间隔(ms)，控制刷新频率
     */
    private int timeInterval = 40;

    private final HeroAircraft heroAircraft;
    private final List<MobEnemy> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<BaseProp> propList;

    private MusicThread bgMusic;

    /**
     * 屏幕中出现的敌机最大数量
     * Boss是否出现
     */
    private int enemyMaxNumber = 5;
    private int bossExist = 0;
    private int scoreCount = 0;

    /**
     * 当前得分
     */
    private int score = 0;
    /**
     * 当前时刻
     */
    private int time = 0;

    /**
     * 周期（ms)
     * 指示子弹的发射、敌机的产生频率
     */
    private int cycleDuration = 400;
    private int cycleTime = 0;

    /**
     * 游戏结束标志
     */
    private boolean gameOverFlag = false;

    public Game(String difficulty, boolean soundEnabled) {
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
                    enemyAircrafts.add(EnemyGenerator.createRandomEnemy());
                }
                // 根据分数判断BOSS是否生成
                if(scoreCount >= 400 && bossExist == 0) {
                    enemyAircrafts.add(EnemyGenerator.createBoss());
                    bossExist = 1;
                    if(soundEnabled) {
                        bgMusic.close();
                        bgMusic = new MusicThread("src/videos/bgm_boss.wav", true);
                        bgMusic.start();
                    }
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
    //      Action 各部分
    //***********************

    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private void shootAction() {
        // TODO 敌机射击
        for(MobEnemy Enemy : enemyAircrafts) {
            enemyBullets.addAll(Enemy.shoot());
        }
        // 英雄射击
        heroBullets.addAll(heroAircraft.shoot());
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
                            if(soundEnabled) {
                                bgMusic.close();
                                bgMusic = new MusicThread("src/videos/bgm.wav", true);
                                bgMusic.start();
                            }
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
                if(soundEnabled) {
                    if(prop instanceof BombProp)
                        new MusicThread("src/videos/bomb_explosion.wav", false).start();
                    else
                        new MusicThread("src/videos/get_supply.wav", false).start();
                }
                prop.vanish();
            }
        }
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
