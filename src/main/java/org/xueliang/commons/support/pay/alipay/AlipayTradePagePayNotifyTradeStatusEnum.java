package org.xueliang.commons.support.pay.alipay;

/**
 * 交易状态说明及通知触发条件
 * @author XueLiang
 * @date 2018/11/8 0:32
 */
public enum AlipayTradePagePayNotifyTradeStatusEnum {

    /**
     * 交易创建，等待买家付款
     * 触发条件描述：交易创建
     * 触发条件默认值：false（不触发通知）
     */
    WAIT_BUYER_PAY,

    /**
     * 未付款交易超时关闭，或支付完成后全额退款
     * 触发条件描述：交易关闭
     * 触发条件默认值：false（不触发通知）
     */
    TRADE_CLOSED,

    /**
     * 交易支付成功
     * 触发条件描述：支付成功
     * 触发条件默认值：true（触发通知）
     */
    TRADE_SUCCESS,

    /**
     * 交易结束，不可退款
     * 触发条件描述：交易完成
     * 触发条件默认值：false（不触发通知）
     */
    TRADE_FINISHED
}
