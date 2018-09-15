package org.xueliang.commons.support.sms;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 短信发送接口
 *
 * @author XueLiang
 * @date 2018/09/02 22:28:00
 */
public interface SmsSener {

    boolean batchSend(List<String> mobileList, String signName, String templateId, LinkedHashMap<String, String> parameterMap) throws Exception;

    boolean send(String mobile, String signName, String templateId, LinkedHashMap<String, String> parameterMap) throws Exception;
}
