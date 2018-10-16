package org.xueliang.commons.support.verifier;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xueliang.commons.enums.ActionEnum;
import org.xueliang.commons.exception.*;
import org.xueliang.commons.support.verifier.model.ImageCaptcha;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 图片验证码
 * @author XueLiang
 * @date 2018/09/13 22:28:22
 */
public class ImageVerifier implements DataVerifier {

    private static final Logger LOGGER = LogManager.getLogger(ImageVerifier.class);

    /**
     * 验证码字符个数
     */
    private int count;

    /**
     * 验证码可用字符
     */
    private String chars;

    private static LoadingCache<String, ImageCaptcha> captchaCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(600, TimeUnit.SECONDS)
            .build(new CacheLoader<String, ImageCaptcha>() {
                @Override
                public ImageCaptcha load(String key) throws Exception {
                    LOGGER.info("no cache for key {}", key);
                    return new ImageCaptcha();
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

    public void setCount(int count) {
        this.count = count;
    }

    public void setChars(String chars) {
        this.chars = chars;
    }

    @Override
    public Object init(String appId, String data, ActionEnum action, String... relateData) throws BaseException {
        String ip = relateData[0];
        String random = RandomStringUtils.random(count, chars);
        ImageCaptcha imageCaptcha = new ImageCaptcha();
        imageCaptcha.setId(getKey(appId, action.name(), ip));
        imageCaptcha.setValue(random);
        imageCaptcha.setAction(action);
        captchaCache.put(imageCaptcha.getId(), imageCaptcha);
        return imageCaptcha;
    }

    private String getKey(String appId, String action, String ip) {
        return DigestUtils.md5Hex(appId + action + ip + System.currentTimeMillis());
    }

    public ImageCaptcha getImageCaptcha(String appId, String imageCaptchaId) {
        try {
            return captchaCache.get(imageCaptchaId);
        } catch (ExecutionException e) {
            LOGGER.warn("get image captcha by token error", e);
            return new ImageCaptcha();
        }
    }

    @Override
    public Object verify(String appId, String imageCaptchaToken, ActionEnum action, String input, String... relateData) throws BaseException {
        if (StringUtils.isEmpty(imageCaptchaToken)) {
            throw new RequiredParameterException("验证码Token不能为空");
        }
        if (StringUtils.isEmpty(input)) {
            throw new CaptchaRequiredException();
        }
        String ip = relateData[0];
        try {
            ImageCaptcha imageCaptcha = captchaCache.get(imageCaptchaToken);
            if (StringUtils.isEmpty(imageCaptcha.getId())) {
                throw new CaptchaExpiredException("图片验证码已过期");
            }
            if (!action.equals(imageCaptcha.getAction())) {
                LOGGER.info("invalid action[{}], real action is [{}]", action, imageCaptcha.getAction());
                throw new CaptchaWrongException();
            }
            if (!input.equalsIgnoreCase(imageCaptcha.getValue())) {
                LOGGER.info("invalid captcha[{}], real captcha is [{}]", input, imageCaptcha.getValue());
                throw new CaptchaWrongException();
            }
        } catch (ExecutionException e) {
            LOGGER.error("get smsCaptcha from cache error", e);
            throw new CaptchaWrongException();
        }
        return Boolean.TRUE;
    }

    @Override
    public Object verifyAndExpires(String appId, String imageCaptchaToken, ActionEnum action, String input, String... relateData) throws BaseException {
        boolean verified = (Boolean) verify(appId, imageCaptchaToken, action, input, relateData);
        if (verified) {
            String ip = relateData[0];
            captchaCache.invalidate(imageCaptchaToken);
        }
        return verified;
    }
}
