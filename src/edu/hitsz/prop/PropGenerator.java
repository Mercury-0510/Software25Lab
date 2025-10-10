package edu.hitsz.prop;

import edu.hitsz.factory.*;

/**
 * 道具工厂管理器，管理不同类型的道具工厂
 * @author hitsz
 */
public class PropGenerator {

    private static PropItemFactory propFactory;

    public static BaseProp createBloodProp(int x, int y) {
        propFactory = new BloodPropFactory();
        return propFactory.createProp(x, y);
    }

    public static BaseProp createBombProp(int x, int y) {
        propFactory = new BombPropFactory();
        return propFactory.createProp(x, y);
    }

    public static BaseProp createBulletProp(int x, int y) {
        propFactory = new BulletPropFactory();
        return propFactory.createProp(x, y);
    }

    public static BaseProp createBulletPlusProp(int x, int y) {
        propFactory = new BulletPlusPropFactory();
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
        } else if (rand < 0.8){
            return createBulletProp(x, y);
        } else {
            return createBulletPlusProp(x, y);
        }
    }
}