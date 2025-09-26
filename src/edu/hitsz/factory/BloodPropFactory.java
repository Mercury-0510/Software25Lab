package edu.hitsz.factory;

import edu.hitsz.prop.BaseProp;
import edu.hitsz.prop.BloodProp;

/**
 * 加血道具工厂实现
 * @author hitsz
 */
public class BloodPropFactory implements PropItemFactory {
    
    @Override
    public BaseProp createProp(int x, int y) {
        return new BloodProp(x, y, 0, 8, 30);
    }
}