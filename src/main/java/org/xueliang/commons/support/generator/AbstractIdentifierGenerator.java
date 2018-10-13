package org.xueliang.commons.support.generator;

/**
 * @author xueliang
 * @date 2018/10/13 下午8:14
 */
public abstract class AbstractIdentifierGenerator implements IdentifierGenerator {

    @Override
    public String nextId() {
        return nextId(null);
    }
}
