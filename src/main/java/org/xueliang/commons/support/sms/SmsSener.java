package org.xueliang.commons.support.sms;

import com.aliyuncs.exceptions.ClientException;

import java.util.List;

/**
 * 短信发送接口
 *
 * @author XueLiang
 * @date 2018/09/02 22:28:00
 */
public interface SmsSener {

    boolean batchSend(List<String> mobileList, String content) throws Exception;

    boolean send(String mobile, String content) throws Exception;
}
