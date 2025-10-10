package edu.hitsz.factory;

import edu.hitsz.prop.BaseProp;
import edu.hitsz.prop.BulletPlusProp;

/**
 * 火力道具工厂实现
 * @author hitsz
 */
public class BulletPlusPropFactory implements PropItemFactory {

    @Override
    public BaseProp createProp(int x, int y) {
        return new BulletPlusProp(x, y, 0, 8, 30);
    }
}
