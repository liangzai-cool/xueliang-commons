package org.xueliang.commons.support.generator;

/**
 * 固定长度，不足部分在前面补0
 */
public class AutoIncrementFixedLengthIdentifierGenerator extends AutoIncrementIdentifierGenerator {

    /**
     * id 总长度
     */
    private int length;

    @Override
    public String nextId(String table, String appId) {
        return String.format("%0" + length + "d", Integer.parseInt(super.nextId(table, appId)));
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
