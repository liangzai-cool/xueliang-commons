package org.xueliang.commons.support.verifier;

import org.xueliang.commons.enums.ActionEnum;
import org.xueliang.commons.exception.BaseException;

public interface DataVerifier {

    /**
     * 初始化校验，如发送短信，发送邮件，生成图片验证码文字内容等
     * @param appId
     * @param data 需要验证的数据，如手机号码、邮箱地址等
     * @param action
     * @param relateData 扩展数据，用于传入IP、短信模版或其他数据
     * @return
     * @throws BaseException
     */
    Object init(String appId, String data, ActionEnum action, String... relateData) throws BaseException;

    /**
     * 执行校验，但不主动使匹配的验证码过期
     * @param appId
     * @param data
     * @param action
     * @param input
     * @param relateData 扩展数据，用于传入IP、短信模版或其他数据
     * @return
     * @throws BaseException
     */
    Object verify(String appId, String data, ActionEnum action, String input, String... relateData) throws BaseException;

    /**
     * 执行校验，且使匹配的验证码过期
     * @param appId
     * @param data
     * @param action
     * @param input
     * @param relateData 扩展数据，用于传入IP、短信模版或其他数据
     * @return
     * @throws BaseException
     */
    Object verifyAndExpires(String appId, String data, ActionEnum action, String input, String... relateData) throws BaseException;
}
