package org.xueliang.commons.support.sms.qcloud;

import com.github.qcloudsms.SmsMultiSender;
import com.github.qcloudsms.SmsMultiSenderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xueliang.commons.support.sms.AbstractSmsSender;

import java.util.*;

/**
 * 腾讯云
 * @author XueLiang
 * @date 2018/9/3 9:39
 */
public class QcloudSmsSender extends AbstractSmsSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(QcloudSmsSender.class);

    /**
     * 短信应用SDK AppID
     */
    private final int APP_ID;

    /**
     * 短信应用SDK AppKey
     */
    private final String APP_KEY;

    public  QcloudSmsSender(int appId, String appKey) {
        this.APP_ID = appId;
        this.APP_KEY = appKey;
    }

    /**
     *
     * @param mobileList 需要发送短信的手机号码
     * @param signName 签名
     * @param templateId 短信模板ID，需要在短信应用中申请
     * @param parameterMap 短信参数占位符，使用${code DELIMITER}分隔，内容如：验证码,123456
     * @return
     * @throws Exception
     */
    @Override
    public boolean batchSend(List<String> mobileList, String signName, String templateId, LinkedHashMap<String, String> parameterMap) throws Exception {
        int smsTemplateId = Integer.parseInt(templateId);
        ArrayList<String> smsMobileList = new ArrayList<>(mobileList);
        ArrayList<String> smsParamList = new ArrayList<>(parameterMap.values());
        SmsMultiSender msender = new SmsMultiSender(this.APP_ID, this.APP_KEY);
        SmsMultiSenderResult result =  msender.sendWithParam("86", smsMobileList, smsTemplateId, smsParamList, signName, "", "");
        LOGGER.info("send sms result code: [{}], message: [{}]", result.result, result.errMsg);
        return result.result == 0;
    }
}
