package org.xueliang.commons.support.sms.aliyun;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xueliang.commons.support.sms.AbstractSmsSender;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 阿里云发送短信
 * @author XueLiang
 * @date 2018/9/2 22:43
 */
public class AliyunSmsSender extends AbstractSmsSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(AliyunSmsSender.class);

    private final String REGION_ID = "cn-hangzhou";

    /**
     * 短信API产品名称（短信产品名固定，无需修改）
     */
    private final String PRODUCT = "Dysmsapi";

    /**
     * 短信API产品域名（接口地址固定，无需修改）
     */
    private final String DOMAIN = "dysmsapi.aliyuncs.com";

    private final IAcsClient acsClient;

    public AliyunSmsSender(IAcsClient acsClient) throws Exception {
        DefaultProfile.addEndpoint(this.REGION_ID, this.PRODUCT, this.DOMAIN);
        this.acsClient = acsClient;
    }

    /**
     * 批量发送短信
     * @param mobileList
     * @param parameterMap 模版内容，json字符串
     * @return
     * @throws Exception
     */
    @Override
    public boolean batchSend(List<String> mobileList, String signName, String templateCode, LinkedHashMap<String, String> parameterMap) throws Exception {
        //组装请求对象
        SendSmsRequest request = new SendSmsRequest();
        //使用post提交
        request.setMethod(MethodType.POST);
        //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式；发送国际/港澳台消息时，接收号码格式为00+国际区号+号码，如“0085200000000”
        request.setPhoneNumbers(String.join(",", mobileList));
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);
        //必填:短信模板-可在短信控制台中找到，发送国际/港澳台消息时，请使用国际/港澳台短信模版
        request.setTemplateCode(templateCode);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
        request.setTemplateParam(new JSONObject(parameterMap).toString());
        //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId(String.valueOf(System.currentTimeMillis()));
        //请求失败这里会抛ClientException异常
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        LOGGER.info("send sms result code: [{}], message: [{}]", sendSmsResponse.getCode(), sendSmsResponse.getMessage());
        if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
            return true;
        }
        return false;
    }
}
