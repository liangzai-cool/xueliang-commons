package org.xueliang.commons.support.http;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpUtils {

    private static final Logger LOGGER = LogManager.getLogger(HttpUtils.class);

    private HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

    private HttpClient httpClient = null;

    private boolean allowAllHostname = false;

    public HttpUtils() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (certificate, authType) -> true).build();
            httpClientBuilder.setSSLContext(sslContext)
            .setSSLHostnameVerifier(new NoopHostnameVerifier());
            allowAllHostname = true;
        } catch (KeyManagementException e) {
            LOGGER.warn("");
        } catch (NoSuchAlgorithmException e) {

        } catch (KeyStoreException e) {
        }
        httpClient = httpClientBuilder.build();
    }

    public Object post(String url, String body, Map<String, List<String>> paramMap, Map<String, List<String>> headerMap, boolean isText) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        if (StringUtils.isNotEmpty(body)) {
            StringEntity stringEntity = new StringEntity(body, StandardCharsets.UTF_8);
            httpPost.setEntity(stringEntity);
        } else if (paramMap != null) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            paramMap.forEach((key, values) -> {
                if (values != null) {
                    values.forEach(value -> {
                        params.add(new BasicNameValuePair(key, value));
                    });
                }
            });
            httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
        }
        if (headerMap != null) {
            headerMap.forEach((name, values) -> {
                if (values != null) {
                    values.forEach(value -> {
                        httpPost.addHeader(name, value);
                    });
                }
            });
        }
        LOGGER.info("begin to request {}", url);
        long start = System.currentTimeMillis();
        HttpResponse httpResponse = httpClient.execute(httpPost);
        long end = System.currentTimeMillis();
        LOGGER.info("end to request {}", url);
        LOGGER.info("cost {} milliseconds", (end - start));
        if (isText) {
            String responseString = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
            LOGGER.info("reponse body is [{}]", responseString);
            return responseString;
        }
        return httpResponse.getEntity().getContent();
    }

    public Object get(String url, Map<String, List<String>> paramMap, Map<String, List<String>> headerMap, boolean isText) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        if (paramMap != null) {
            try {
                URIBuilder uriBuilder = new URIBuilder(httpGet.getURI());
                paramMap.forEach((key, values) -> {
                    if (values != null) {
                        values.forEach(value -> {
                            uriBuilder.addParameter(key, value);
                        });
                    }
                });
                URI uri = uriBuilder.build();
                httpGet.setURI(uri);
            } catch (URISyntaxException e) {
                LOGGER.error("build uri error", e);
                throw new IllegalStateException(e);
            }
        }
        if (headerMap != null) {
            headerMap.forEach((name, values) -> {
                if (values != null) {
                    values.forEach(value -> {
                        httpGet.addHeader(name, value);
                    });
                }
            });
        }
        LOGGER.info("begin to request {}", url);
        long start = System.currentTimeMillis();
        HttpResponse httpResponse = httpClient.execute(httpGet);
        long end = System.currentTimeMillis();
        LOGGER.info("end to request {}", url);
        LOGGER.info("cost {} milliseconds", (end - start));
        if (isText) {
            String responseString = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
            LOGGER.info("reponse body is [{}]", responseString);
            return responseString;
        }
        return httpResponse.getEntity().getContent();
    }

    public String postJSONBody(String url, String jsonString) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=UTF-8");
        return post(url, jsonString, headers);
    }

    public String post(String url, String body, Map<String, String> headers) throws IOException {
        return (String) post(url, body, null, null, true);
    }

    public String post(String url) throws IOException {
        return post(url, null, null);
    }

    public String post(String url, Map<String, List<String>> paramMap) throws IOException {
        return (String) post(url, null, paramMap, null, true);
    }

    public String get(String url) throws IOException {
        return get(url, null, null);
    }

    public String get(String url, Map<String, List<String>> paramMap, Map<String, List<String>> headerMap) throws IOException {
        return (String) get(url, paramMap, null, true);
    }

    public boolean isAllowAllHostname() {
        return allowAllHostname;
    }
}
