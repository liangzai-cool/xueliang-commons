package org.xueliang.commons.support.sms.aliyun;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.Assert;
import org.xueliang.commons.CommonConstants;

import java.util.LinkedHashMap;

/**
 * @author XueLiang
 * @date 2018/9/2 23:21
 */
public class AliyunSmsSenderTest {

    private static AliyunSmsSender sender;

    @BeforeClass
    public static void beforeClass() throws Exception {
        String accessKeyId = "";
        String accessKeySecret = "";
        IClientProfile profile = DefaultProfile.getProfile(CommonConstants.ALIYUN_CLIENT_PROFILE_REGION_ID, accessKeyId, accessKeySecret);
        IAcsClient acsClient = new DefaultAcsClient(profile);
        sender = new AliyunSmsSender(acsClient);
    }

    @Test
    public void testSend() throws Exception {
        boolean result = sender.send("131xxxx8910", "短信签名", "SMS_模版代码", new LinkedHashMap<String, String>() {
            {
                put("captcha", "123456");
            }
        });
        Assert.state(result, "发送失败");
    }
}
