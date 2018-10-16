package org.xueliang.commons.support.verifier;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xueliang.commons.Constants;
import org.xueliang.commons.enums.ActionEnum;
import org.xueliang.commons.exception.*;
import org.xueliang.commons.support.verifier.model.SMSCaptcha;
import org.xueliang.commons.support.sms.SmsSener;
import org.xueliang.commons.util.ConfigProvider;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 验证手机
 * @author XueLiang
 * @date 2018/09/13 22:26:19
 */
public class MobileVerifier implements DataVerifier {

    private static final Logger LOGGER = LogManager.getLogger(MobileVerifier.class);

    private static LoadingCache<String, SMSCaptcha> captchaCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(600, TimeUnit.SECONDS)
            .build(new CacheLoader<String, SMSCaptcha>() {
                @Override
                public SMSCaptcha load(String key) throws Exception {
                    LOGGER.info("no cache for key {}", key);
                    return new SMSCaptcha();
                }
    });

    private static LoadingCache<String, Integer> ipCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(3600, TimeUnit.SECONDS)
            .build(new CacheLoader<String, Integer>() {
                @Override
                public Integer load(String key) throws Exception {
                    LOGGER.info("no cache for key {}", key);
                    return 0;
                }
    });

    private SmsSener smsSender;

    private ConfigProvider configProvider;

    private int count;

    public void setSmsSender(SmsSener smsSender) {
        this.smsSender = smsSender;
    }

    public void setConfigProvider(ConfigProvider configProvider) {
        this.configProvider = configProvider;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public Object init(String appId, String mobile, ActionEnum action, String... relateData) throws BaseException {
        if (StringUtils.isEmpty(mobile)) {
            throw new RequiredParameterException("手机号码");
        }
        String ip = relateData[0];
        checkCount(ip);
        String signName = configProvider.getConfig(Constants.CONFIG_KEY_NAME_SMS_SIGN_NAME);
        String templateId = configProvider.getConfig(Constants.CONFIG_KEY_NAME_SMS_CAPTCHA_TEMPLATE_ID);
        LOGGER.info("init bind mobile[number={}][relateData={}]", mobile, ip);
        String random = RandomStringUtils.randomNumeric(this.count);
        try {
            LocalDateTime sendTime = LocalDateTime.now();
            LinkedHashMap<String, String> parameterMap = new LinkedHashMap<>();
            parameterMap.put("code", random);
            boolean isSendSuccess = smsSender.send(mobile, signName, templateId, parameterMap);
            if (!isSendSuccess) {
                throw new ServerInternalException("短信发送失败");
            }
            String id = getKey(appId, mobile, action.name());
            SMSCaptcha smsCaptcha = new SMSCaptcha();
            smsCaptcha.setMobile(mobile);
            smsCaptcha.setAction(action);
            smsCaptcha.setValue(random);
            smsCaptcha.setSendTime(sendTime);
            smsCaptcha.setId(id);
            LOGGER.info("new sms captcha: {}", smsCaptcha);
            captchaCache.put(id, smsCaptcha);
            return true;
        } catch (BaseException e) {
            LOGGER.error("send captcha sms to mobile[number=" + mobile + "][ip=" + ip + "] error", e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("send captcha sms to mobile[number=" + mobile + "][ip=" + ip + "] error", e);
            throw new ServerInternalException();
        }
    }

    @Override
    public Object verify(String appId, String mobile, ActionEnum action, String input, String... relateData) throws BaseException {
        if (StringUtils.isEmpty(mobile)) {
            throw new RequiredParameterException("手机号码");
        }
        if (StringUtils.isEmpty(input)) {
            throw new CaptchaRequiredException();
        }
        String key = getKey(appId, mobile, action.name());
        try {
            SMSCaptcha smsCaptcha = captchaCache.get(key);
            if (StringUtils.isEmpty(smsCaptcha.getId())) {
                throw new CaptchaExpiredException();
            }
            if (!mobile.equals(smsCaptcha.getMobile())) {
                LOGGER.info("invalid mobile[{}], real mobile is [{}]", mobile, smsCaptcha.getMobile());
                throw new CaptchaWrongException();
            }
            if (!action.equals(smsCaptcha.getAction())) {
                LOGGER.info("invalid action[{}], real action is [{}]", action, smsCaptcha.getAction());
                throw new CaptchaWrongException();
            }
            if (!input.equals(smsCaptcha.getValue())) {
                LOGGER.info("invalid captcha[{}], real captcha is [{}]", input, smsCaptcha.getValue());
                throw new CaptchaWrongException();
            }
        } catch (ExecutionException e) {
            LOGGER.error("get smsCaptcha from cache error", e);
            throw new CaptchaWrongException();
        }
        return Boolean.TRUE;
    }

    @Override
    public Object verifyAndExpires(String appId, String mobile, ActionEnum action, String input, String... relateData) throws BaseException {
        boolean verified = (Boolean) verify(appId, mobile, action, input, relateData);
        if (verified) {
            String ip = relateData[0];
            String key = getKey(appId, mobile, action.name());
            captchaCache.invalidate(key);
        }
        return verified;
    }

    private String getKey(String appId, String mobile, String action) {
        return DigestUtils.md5Hex(appId + mobile + action);
    }

    private void checkCount(String ip) throws BaseException {
        try {
            int count = ipCache.get(ip);
            if (count >= 10) {
                throw new RequestRateLimitException();
            }
            ipCache.put(ip, ++count);
        } catch (ExecutionException e) {
            LOGGER.error("check ip[" + ip + "] send  from cache error", e);
            throw new ServerInternalException();
        }
    }
}
