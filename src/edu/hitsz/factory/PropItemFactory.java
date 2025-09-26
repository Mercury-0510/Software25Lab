package edu.hitsz.factory;

import edu.hitsz.prop.BaseProp;

/**
 * 道具工厂接口，使用抽象工厂模式
 * @author hitsz
 */
public interface PropItemFactory {
    /**
     * 创建道具实例
     * @param x x坐标
     * @param y y坐标
     * @return 道具实例
     */
    BaseProp createProp(int x, int y);
}