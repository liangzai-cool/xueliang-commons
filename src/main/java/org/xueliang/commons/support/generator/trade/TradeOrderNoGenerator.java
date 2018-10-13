package org.xueliang.commons.support.generator.trade;

import org.apache.commons.lang3.RandomStringUtils;
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
public class TradeOrderNoGenerator extends AbstractIdentifierGenerator {

    /**
     * 订单号总长度
     */
    private int length;

    /**
     * 订单号允许包含的字符
     */
    private String chars;

    /**
     * 日期时间格式化
     */
    private String dateTimeFormatPattern;

    /**
     * 是否在头部添加日期时间
     */
    private boolean withDateTimePrefix;

    private DSLContext dsl;

    private String tableName;

    private AutoIncrementFixedLengthIdentifierGenerator autoIncrementFixedLengthIdentifierGenerator = new AutoIncrementFixedLengthIdentifierGenerator();

    public TradeOrderNoGenerator() {
        autoIncrementFixedLengthIdentifierGenerator.setDsl(dsl);
        autoIncrementFixedLengthIdentifierGenerator.setTableName(tableName);

        int tailLength = length;
        if (withDateTimePrefix) {
            tailLength = tailLength - dateTimeFormatPattern.length();
        }
        autoIncrementFixedLengthIdentifierGenerator.setLength(tailLength);
    }

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

    public String getChars() {
        return chars;
    }

    public void setChars(String chars) {
        this.chars = chars;
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

    public DSLContext getDsl() {
        return dsl;
    }

    public void setDsl(DSLContext dsl) {
        this.dsl = dsl;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
