package org.xueliang.commons.support.verifier;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.afs.model.v20180112.AuthenticateSigRequest;
import com.aliyuncs.afs.model.v20180112.AuthenticateSigResponse;
import com.aliyuncs.profile.DefaultProfile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xueliang.commons.enums.ActionEnum;
import org.xueliang.commons.exception.BaseException;
import org.xueliang.commons.exception.InvalidParameterException;
import org.xueliang.commons.exception.ServerInternalErrorException;

/**
 * 投篮验证
 * @author XueLiang
 * @date 2018/10/19 18:05
 */
public class ShootingVerifier implements DataVerifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShootingVerifier.class);

    private final String REGION_ID = "cn-hangzhou";

    /**
     * API产品名称（产品名固定，无需修改）
     */
    private final String PRODUCT = "afs";

    /**
     * API产品域名（接口地址固定，无需修改）
     */
    private final String DOMAIN = "afs.aliyuncs.com";

    private IAcsClient acsClient;

    private int verifySuccessCode = 100;

    private String appKey;

    public ShootingVerifier(IAcsClient acsClient) {
        DefaultProfile.addEndpoint(REGION_ID, PRODUCT, DOMAIN);
        this.acsClient = acsClient;
    }

    @Override
    public Object init(String appId, String data, ActionEnum action, String... relateData) throws BaseException {
        return new UnsupportedOperationException("无需初始化操作");
    }

    @Override
    public Object verify(String appId, String data, ActionEnum action, String input, String... relateData) throws BaseException {
        return new UnsupportedOperationException("无需初始化操作");
    }

    @Override
    public Object verifyAndExpires(String appId, String data, ActionEnum action, String aliyunShootingSessionId, String... relateData) throws BaseException {
        if (StringUtils.isEmpty(aliyunShootingSessionId)) {
            throw new InvalidParameterException("缺少投篮验证会话ID，请先进行投篮验证");
        }
        if (relateData == null || relateData.length == 0) {
            LOGGER.error("lose IP data");
            throw new ServerInternalErrorException();
        }
        String ip = relateData[0];
        LOGGER.info("aliyun shooting session id: {}, IP: {}", aliyunShootingSessionId, ip);
        AuthenticateSigRequest request = new AuthenticateSigRequest();
        request.setSessionId(aliyunShootingSessionId);
        request.setAppKey("");
        request.setRemoteIp(ip);
        try {
            AuthenticateSigResponse response = acsClient.getAcsResponse(request);
            boolean isVerifySuccess = response.getCode() == verifySuccessCode;
            LOGGER.info("response code is {}, is success ?", response.getCode(), isVerifySuccess);
            return isVerifySuccess;
        } catch (Exception e) {
            LOGGER.error("shooting verify error", e);
            throw new ServerInternalErrorException();
        }
    }

    public IAcsClient getAcsClient() {
        return acsClient;
    }

    public void setAcsClient(IAcsClient acsClient) {
        this.acsClient = acsClient;
    }

    public int getVerifySuccessCode() {
        return verifySuccessCode;
    }

    public void setVerifySuccessCode(int verifySuccessCode) {
        this.verifySuccessCode = verifySuccessCode;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
}
