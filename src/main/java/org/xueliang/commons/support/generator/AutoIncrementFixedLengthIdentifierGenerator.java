package org.xueliang.commons.support.generator;

import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.xueliang.commons.DataStatusEnum;

/**
 * 固定长度，不足部分在前面补0
 */
public class AutoIncrementFixedLengthIdentifierGenerator extends AutoIncrementIdentifierGenerator {

    /**
     * id 总长度
     */
    private int length;

    @Override
    public String nextId(String table) {
        return String.format("%0" + length + "d", Integer.parseInt(super.nextId(table)));
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
