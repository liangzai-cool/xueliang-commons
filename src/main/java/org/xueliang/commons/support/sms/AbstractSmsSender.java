package org.xueliang.commons.support.sms;

import com.aliyuncs.exceptions.ClientException;

import java.util.Arrays;
import java.util.List;

/**
 * @author XueLiang
 * @date 2018/9/2 22:31
 */
public abstract class AbstractSmsSender implements SmsSener {

    @Override
    public abstract boolean batchSend(List<String> mobileList, String content) throws Exception;

    public boolean send(String mobile, String content) throws Exception {
        return batchSend(Arrays.asList(new String[]{ mobile }), content);
    }
}
