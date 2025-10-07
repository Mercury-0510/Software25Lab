package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HeroAircraftTest {

    private HeroAircraft heroAircraft;
    private int initX;
    private int initY;

    @BeforeEach
    void setUp() {
        heroAircraft = HeroAircraft.getInstance(100, 200, 0, 0, 100);
        initX = heroAircraft.getLocationX();
        initY = heroAircraft.getLocationY();
    }

    @Test
    void testGetInstanceSingletonUpdate() {
        HeroAircraft first = HeroAircraft.getInstance(150, 250, 0, 0, 150);
        HeroAircraft second = HeroAircraft.getInstance(300, 400, 0, 0, 50);
        assertSame(first, second, "应为同一个单例实例");
        assertEquals(300, second.getLocationX());
        assertEquals(400, second.getLocationY());
        assertEquals(50, second.getHp());
    }

    @Test
    void testShootBasicNotEmpty() {
        List<BaseBullet> bullets = heroAircraft.shoot();
        assertNotNull(bullets, "发射结果不应为null");
        assertFalse(bullets.isEmpty(), "至少应产生一颗子弹");
    }

    @Test
    void testDecreaseHpAndOverKill() {
        heroAircraft.decreaseHp(30);
        assertEquals(70, heroAircraft.getHp());
        heroAircraft.decreaseHp(10000); // 过杀
        assertEquals(0, heroAircraft.getHp(), "生命值应归零");

    }
}