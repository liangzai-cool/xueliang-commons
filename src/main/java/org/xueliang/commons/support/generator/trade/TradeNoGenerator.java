package org.xueliang.commons.support.generator.trade;

import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.xueliang.commons.support.generator.AbstractIdentifierGenerator;
import org.xueliang.commons.support.generator.AutoIncrementFixedLengthIdentifierGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 订单号自动生成类
 * @author xueliang
 * @date 2018/10/13 下午8:10
 */
public class TradeNoGenerator extends AbstractIdentifierGenerator {

    /**
     * 订单号总长度
     */
    private int length;

    /**
     * 日期时间格式化
     */
    private String dateTimeFormatPattern;

    /**
     * 是否在头部添加日期时间
     */
    private boolean withDateTimePrefix;

    private AutoIncrementFixedLengthIdentifierGenerator autoIncrementFixedLengthIdentifierGenerator;

    @Override
    public String nextId(String flag) {
        String dateTimeString = StringUtils.EMPTY;
        if (withDateTimePrefix) {
            dateTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormatPattern));
        }
        String orderNo = dateTimeString + autoIncrementFixedLengthIdentifierGenerator.nextId(flag);
        return orderNo;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getDateTimeFormatPattern() {
        return dateTimeFormatPattern;
    }

    public void setDateTimeFormatPattern(String dateTimeFormatPattern) {
        this.dateTimeFormatPattern = dateTimeFormatPattern;
    }

    public boolean isWithDateTimePrefix() {
        return withDateTimePrefix;
    }

    public void setWithDateTimePrefix(boolean withDateTimePrefix) {
        this.withDateTimePrefix = withDateTimePrefix;
    }

    public AutoIncrementFixedLengthIdentifierGenerator getAutoIncrementFixedLengthIdentifierGenerator() {
        return autoIncrementFixedLengthIdentifierGenerator;
    }

    public void setAutoIncrementFixedLengthIdentifierGenerator(AutoIncrementFixedLengthIdentifierGenerator autoIncrementFixedLengthIdentifierGenerator) {
        this.autoIncrementFixedLengthIdentifierGenerator = autoIncrementFixedLengthIdentifierGenerator;
    }
}
