package org.xueliang.commons.support.sms.alidayu;

import com.aliyuncs.exceptions.ClientException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.Assert;

/**
 * @author XueLiang
 * @date 2018/9/2 23:21
 */
public class AlidayuSmsSenderTest {

    private static AlidayuSmsSender sender;

    @BeforeClass
    public static void beforeClass() throws Exception {
        sender = new AlidayuSmsSender("xxx",
                "xxx",
                "xxx",
                "SMS_xxx");
    }

    @Test
    public void testSend() throws Exception {
        JSONObject json = new JSONObject();
        json.put("captcha", "123456");
        boolean result = sender.send("131xxxx8910", json.toString());
        Assert.state(result, "发送失败");
    }
}
