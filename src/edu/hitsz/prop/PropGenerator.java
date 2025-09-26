package edu.hitsz.prop;

import edu.hitsz.factory.BloodPropFactory;
import edu.hitsz.factory.BombPropFactory;
import edu.hitsz.factory.BulletPropFactory;
import edu.hitsz.factory.PropItemFactory;

/**
 * 道具工厂管理器，管理不同类型的道具工厂
 * @author hitsz
 */
public class PropGenerator {

    private static PropItemFactory propFactory;
    
    /**
     * 创建加血道具
     * @param x x坐标
     * @param y y坐标
     * @return 加血道具实例
     */
    public static BaseProp createBloodProp(int x, int y) {
        propFactory = new BloodPropFactory();
        return propFactory.createProp(x, y);
    }
    
    /**
     * 创建炸弹道具
     * @param x x坐标
     * @param y y坐标
     * @return 炸弹道具实例
     */
    public static BaseProp createBombProp(int x, int y) {
        propFactory = new BombPropFactory();
        return propFactory.createProp(x, y);
    }
    
    /**
     * 创建火力道具
     * @param x x坐标
     * @param y y坐标
     * @return 火力道具实例
     */
    public static BaseProp createBulletProp(int x, int y) {
        propFactory = new BulletPropFactory();
        return propFactory.createProp(x, y);
    }
    
    /**
     * 根据概率随机创建道具
     * @param rand 随机数
     * @param x x坐标
     * @param y y坐标
     * @return 道具实例
     */
    public static BaseProp createRandomProp(double rand, int x, int y) {
        if (rand < 0.3) {
            return createBloodProp(x, y);
        } else if (rand < 0.6) {
            return createBombProp(x, y);
        } else {
            return createBulletProp(x, y);
        }
    }
}