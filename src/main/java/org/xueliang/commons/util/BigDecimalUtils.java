package org.xueliang.commons.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author xueliang
 * @since 2020-09-21 10:04
 */
public class BigDecimalUtils {

    private BigDecimalUtils() { }

    public static BigDecimal currency(BigDecimal amount, int scale, RoundingMode roundingMode) {
        return amount.setScale(scale, roundingMode);
    }

    /**
     * 货币格式，保留2位小数
     * @param amount
     * @return
     */
    public static BigDecimal currency(BigDecimal amount) {
        return currency(amount, 2, RoundingMode.HALF_UP);
    }

    /**
     * 简单货币格式，保留1位小数
     * @param amount
     * @return
     */
    public static BigDecimal simpleCurrency(BigDecimal amount) {
        return currency(amount, 1, RoundingMode.HALF_UP);
    }

    public static String formatCurrency(BigDecimal amount, int scale, RoundingMode roundingMode) {
        return currency(amount, scale, roundingMode).toString();
    }

    public static String formatCurrency(BigDecimal amount) {
        return currency(amount).toString();
    }

    public static String formatSimpleCurrency(BigDecimal amount) {
        return simpleCurrency(amount).toString();
    }
}
