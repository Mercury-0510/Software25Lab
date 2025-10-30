package edu.hitsz.effect;

import java.awt.*;

/**
 * 粒子效果类 - 用于爆炸等视觉效果
 */
public class Particle {
    private double x, y;
    private double vx, vy;
    private int life;
    private int maxLife;
    private Color color;
    private int size;
    
    public Particle(int x, int y, double vx, double vy, int life, Color color, int size) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.life = life;
        this.maxLife = life;
        this.color = color;
        this.size = size;
    }
    
    public void update() {
        x += vx;
        y += vy;
        vy += 0.2; // 重力效果
        life--;
    }
    
    public boolean isAlive() {
        return life > 0;
    }
    
    public void draw(Graphics g) {
        if(isAlive()) {
            // 计算透明度
            float alpha = (float)life / maxLife;
            Color c = new Color(
                color.getRed(), 
                color.getGreen(), 
                color.getBlue(), 
                (int)(alpha * 255)
            );
            g.setColor(c);
            g.fillOval((int)x, (int)y, size, size);
        }
    }
}
