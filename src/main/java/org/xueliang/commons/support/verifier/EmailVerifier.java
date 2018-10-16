package org.xueliang.commons.support.verifier;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.xueliang.commons.enums.ActionEnum;
import org.xueliang.commons.exception.*;
import org.xueliang.commons.support.verifier.model.EmailCaptcha;
import org.xueliang.commons.util.ConfigProvider;

import javax.annotation.PostConstruct;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class EmailVerifier implements DataVerifier {

    private static final Logger LOGGER = LogManager.getLogger(EmailVerifier.class);

    private static final String CONFIG_PREFIX = "smtp.";

    private static LoadingCache<String, EmailCaptcha> captchaCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(600, TimeUnit.SECONDS)
            .build(new CacheLoader<String, EmailCaptcha>() {
                @Override
                public EmailCaptcha load(String key) throws Exception {
                    LOGGER.info("no cache for key {}", key);
                    return new EmailCaptcha();
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

    @Autowired
    private ConfigProvider configProvider;

    @PostConstruct
    public void init() {
        configProvider.setConfigTables("passport_config");
    }

    @Override
    public Object init(String appId, String email, ActionEnum action, String... relateData) throws BaseException {
        if (StringUtils.isEmpty(email)) {
            throw new EmptyParameterException("邮箱地址");
        }
        String ip = relateData[0];
        String subject = relateData[1];
        String random = RandomStringUtils.randomNumeric(6);
        String content = relateData[2].replaceAll("\\$\\{captcha\\}", random);
        checkCount(ip);
        LOGGER.info("init bind email[address={}][relateData={}]", email, ip);
        Map<String, String> configMap = configProvider.getAllConfig(CONFIG_PREFIX);
        try {
            LOGGER.info("send mail start, email is [{}]", email);
            String smtpHost = configMap.get("smtp.host");
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost);
            boolean smtpAuth = Boolean.parseBoolean(configMap.get("smtp.auth"));
            String smtpAuthUser = configMap.get("smtp.auth.user");
            String smtpAuthPassword = configMap.get("smtp.auth.password");
            boolean smtpSSLAuth = Boolean.parseBoolean(configMap.get("smtp.ssl.auth"));
            boolean debug = Boolean.parseBoolean(configMap.get("smtp.debug"));
            String mailSender = configMap.get("smtp.mail.sender");
            String mailSenderNickName = configMap.get("smtp.mail.sender.nickName");
            if (smtpAuth) {
                props.put("mail.smtp.auth", "true");
                if (smtpSSLAuth) {
                    props.setProperty("mail.smtp.socketFactory.class",
                            "javax.net.ssl.SSLSocketFactory");
                    props.setProperty(
                            "mail.smtp.socketFactory.fallback", "false");
                    props.setProperty("mail.smtp.port", "465");
                    props.setProperty("mail.smtp.socketFactory.port",
                            "465");
                }
            }
            Session session = Session.getDefaultInstance(props);
            session.setDebug(debug);
            MimeMessage mimeMessage = new MimeMessage(session);
            if (!StringUtils.isEmpty(mailSenderNickName)) {
                String nickName = MimeUtility.encodeText(mailSenderNickName);
                mimeMessage.setFrom(new InternetAddress(nickName + " <" + mailSender + ">"));
            } else {
                mimeMessage.setFrom(new InternetAddress(mailSender));
            }
            mimeMessage.addRecipient(RecipientType.TO,
                        new InternetAddress(email));
            mimeMessage.setSubject(subject);
            mimeMessage.setSentDate(new Date());
            mimeMessage.setContent(content, "text/html;charset=utf-8");
            mimeMessage.saveChanges();
            Transport transport = session.getTransport("smtp");
            if (smtpAuth) {
                transport.connect(smtpHost, smtpAuthUser,
                        smtpAuthPassword);
            } else {
                transport.connect(smtpHost, null, null);
            }
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
            transport.close();
            LOGGER.info("send email success");
            EmailCaptcha emailCaptcha = new EmailCaptcha();
            emailCaptcha.setEmail(email);
            emailCaptcha.setAction(action);
            emailCaptcha.setValue(random);
            emailCaptcha.setSendTime(LocalDateTime.now());
            emailCaptcha.setId(DigestUtils.md5Hex(String.valueOf(System.currentTimeMillis())));
            LOGGER.info("new sms captcha: {}", emailCaptcha);
            captchaCache.put(getKey(appId, email, ip), emailCaptcha);
            return true;
        } catch (IOException e) {
            LOGGER.error("send captcha email to email[address=" + email + "][ip=" + ip + "] io error", e);
        } catch (MessagingException e) {
            LOGGER.error("send captcha email to email[address=" + email + "][ip=" + ip + "] connect error", e);
        }
        throw new ServerInternalException();
    }

    @Override
    public Object verify(String appId, String email, ActionEnum action, String input, String... relateData) throws BaseException {
        if (StringUtils.isEmpty(email)) {
            throw new EmptyParameterException("电子邮箱地址");
        }
        if (StringUtils.isEmpty(input)) {
            throw new EmptyParameterException("验证码");
        }
        String ip = relateData[0];
        String key = getKey(appId, email, ip);
        try {
            EmailCaptcha emailCaptcha = captchaCache.get(key);
            if (StringUtils.isEmpty(emailCaptcha.getId())) {
                throw new CaptchaExpiredException();
            }
            if (!email.equals(emailCaptcha.getEmail())) {
                LOGGER.info("invalid email[{}], real email is [{}]", email, emailCaptcha.getEmail());
                throw new CaptchaWrongException();
            }
            if (!action.equals(emailCaptcha.getAction())) {
                LOGGER.info("invalid action[{}], real action is [{}]", action, emailCaptcha.getAction());
                throw new CaptchaWrongException();
            }
            if (!input.equals(emailCaptcha.getValue())) {
                LOGGER.info("invalid captcha[{}], real captcha is [{}]", input, emailCaptcha.getValue());
                throw new CaptchaWrongException();
            }
        } catch (ExecutionException e) {
            LOGGER.error("get emailCaptcha from cache error", e);
            throw new CaptchaWrongException();
        }
        return Boolean.TRUE;
    }

    @Override
    public Object verifyAndExpires(String appId, String email, ActionEnum action, String input, String... relateData) throws BaseException {
        boolean verified = (Boolean) verify(appId, email, action, input, relateData);
        if (verified) {
            String ip = relateData[0];
            String key = getKey(appId, email, ip);
            captchaCache.invalidate(key);
        }
        return verified;
    }

    private String getKey(String appId, String email, String ip) {
        return email + ip;
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
