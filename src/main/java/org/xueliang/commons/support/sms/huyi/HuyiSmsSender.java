package org.xueliang.commons.support.sms.huyi;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xueliang.commons.exception.ServerInternalException;
import org.xueliang.commons.support.sms.AbstractSmsSender;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 互亿无线
 * @author XueLiang
 * @date 2018/9/13 21:26
 */
public class HuyiSmsSender extends AbstractSmsSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(HuyiSmsSender.class);

    private final String USERNAME;

    private final String PASSWORD;

    private final String SMS_SEND_SUCCESS = "2";

    private final String smsRequestUrl = "http://106.ihuyi.com/webservice/sms.php?method=Submit&format=json";

    private final String requestUrl;

    public HuyiSmsSender(String username, String password) {
        this.USERNAME = username;
        this.PASSWORD = password;
        this.requestUrl = smsRequestUrl + "&account=" + this.USERNAME + "&password=" + this.PASSWORD;
    }

    private HttpClient httpClient;

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public boolean batchSend(List<String> mobileList, String signName, String templateId, String content) throws Exception {
        String url = this.requestUrl + "&mobile=" + String.join(",", mobileList) + "&content=" + content;
        HttpPost httpPost = new HttpPost(smsRequestUrl);
        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);
            String responseBody = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
            LOGGER.info("send sms response [{}]", responseBody);
            JSONObject jsonResponse = new JSONObject(responseBody);
            String smsId = jsonResponse.optString("smsid");
            String code = jsonResponse.optString("code");
            String msg = jsonResponse.optString("msg");
            LOGGER.info("smsId: [{}], code: [{}], msg: [{}]", smsId, code, msg);
            if (!SMS_SEND_SUCCESS.equals(code)) {
                LOGGER.info("send sms fail");
                throw new ServerInternalException(msg);
            }
            LOGGER.info("send sms success");
            return true;
        } catch (IOException e) {
            LOGGER.error("send captcha sms to mobile[number=" + String.join(",", mobileList) + "] error", e);
        }
        return false;
    }
}
