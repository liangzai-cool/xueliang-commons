package org.xueliang.commons.support.sms.qcloud;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xueliang.commons.support.sms.alidayu.AliDayuSmsSender;

/**
 * @author XueLiang
 * @date 2018/9/3 10:14
 */
public class QcloudSmsSenderTest {

    private static QcloudSmsSender sender;

    @BeforeClass
    public static void beforeClass() throws Exception {
        int appId = 0;
        String appKey = "";
        sender = new QcloudSmsSender(appId, appKey);
    }

    @Test
    public void testSend() throws Exception {
        boolean result = sender.send("131xxxx8910", "xxx", "xxx", "123456");
        Assert.assertTrue("发送失败", result);
    }
}
