package edu.hitsz.factory;

import edu.hitsz.prop.BaseProp;
import edu.hitsz.prop.BombProp;

/**
 * 炸弹道具工厂实现
 * @author hitsz
 */
public class BombPropFactory implements PropItemFactory {
    
    @Override
    public BaseProp createProp(int x, int y) {
        return new BombProp(x, y, 0, 8, 30);
    }
}