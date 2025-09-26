package edu.hitsz.factory;

import edu.hitsz.prop.BaseProp;
import edu.hitsz.prop.BulletProp;

/**
 * 火力道具工厂实现
 * @author hitsz
 */
public class BulletPropFactory implements PropItemFactory {
    
    @Override
    public BaseProp createProp(int x, int y) {
        return new BulletProp(x, y, 0, 8, 30);
    }
}